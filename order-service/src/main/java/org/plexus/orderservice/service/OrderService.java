package org.plexus.orderservice.service;

import org.modelmapper.ModelMapper;
import org.plexus.orderservice.dto.OrderDTO;
import org.plexus.orderservice.dto.OrderLineItemDTO;
import org.plexus.orderservice.proxy.AuthorizationProxy;
import org.plexus.orderservice.proxy.InventoryProxy;
import org.plexus.orderservice.proxy.ProductProxy;
import org.plexus.orderservice.model.Order;
import org.plexus.orderservice.model.OrderLineItem;
import org.plexus.orderservice.repository.OrderLineItemRepository;
import org.plexus.orderservice.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private ProductProxy productProxy;

    @Autowired
    private InventoryProxy inventoryProxy;

    @Autowired
    private AuthorizationProxy authorizationProxy;

    public OrderDTO createOrder(String id, int quantity, String token) {
        isEnoughQuantity(id, quantity, token);
        Order newOrder = new Order();
        double productPrice = productProxy.viewProductByPrice(id);

        String orderNumber;
        do {
            int randomNumber = new Random().nextInt(99999);
            orderNumber = String.valueOf(randomNumber);
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());

        newOrder.setOrderNumber(orderNumber);
        newOrder.setPrice(productPrice * quantity);

        return mapearDTO(orderRepository.save(newOrder));
    }

    public void createOrderLineItem(String orderNumber, String skuCode, int quantity, double price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow();

        orderLineItem.setSkuCode(skuCode);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);

        orderLineItem.setOrder(order);
        order.getOrderLineItems().add(orderLineItem);

        orderLineItemRepository.save(orderLineItem);
        orderRepository.save(order);
    }

    public OrderDTO addProductToOrder(String orderNumber, String skuCode, int quantity, String token) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow();
        isEnoughQuantity(skuCode, quantity, token);
        if (isProductInOrder(order, skuCode)) {
            for (OrderLineItem orderLineItem: order.getOrderLineItems()) {
                if (orderLineItem.getSkuCode().equals(skuCode)) {
                    order.setPrice(order.getPrice() - (orderLineItem.getPrice() * orderLineItem.getQuantity()));
                    orderLineItem.setQuantity(quantity);
                    order.setPrice(order.getPrice() + (orderLineItem.getPrice() * quantity));
                }
            }
        } else {
            double productPrice = productProxy.viewProductByPrice(skuCode);
            createOrderLineItem(order.getOrderNumber(), skuCode, quantity, productPrice);
            order.setPrice(order.getPrice() + (productPrice * quantity));
        }

        return mapearDTO(orderRepository.save(order));
    }

    public OrderDTO removeProductFromOrder(String orderNumber, String skuCode) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow();
        OrderLineItem currentOrderLineItem = new OrderLineItem();
        if (isProductInOrder(order, skuCode)) {
            for (OrderLineItem orderLineItem: order.getOrderLineItems()) {
                if (orderLineItem.getSkuCode().equals(skuCode)) {
                    currentOrderLineItem = orderLineItem;
                    order.setPrice(order.getPrice() - (orderLineItem.getPrice() * orderLineItem.getQuantity()));
                }
            }
            order.getOrderLineItems().remove(currentOrderLineItem);
        } else {
            throw new RuntimeException("Item: " + skuCode + "not found in order: " + orderNumber);
        }

        return mapearDTO(orderRepository.save(order));
    }



    public void isEnoughQuantity (String skuCode, int quantity, String token) {
        int currentQuantity = inventoryProxy.retrieveInventoryQuantity(skuCode, token);
        if (currentQuantity < quantity) {
            throw new RuntimeException("Not enough stock in the inventory for the product: " + skuCode);
        }
    }

    public boolean isProductInOrder (Order order, String skuCode) {
        for (OrderLineItem orderLineItem: order.getOrderLineItems()) {
            if (orderLineItem.getSkuCode().equals(skuCode)) return true;
        }
        return false;
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(this::mapearDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrderById(long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        return mapearDTO(order);
    }


    public void deleteOrder(long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        orderRepository.delete(order);
    }

    // Convierte Entidad a DTO
    private OrderDTO mapearDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        orderDTO.setOrderLineItemList(mapearItemList(order.getOrderLineItems()));
        return orderDTO;
    }

    private List<OrderLineItemDTO> mapearItemList(List<OrderLineItem> orderLineItems) {
        List<OrderLineItemDTO> orderLineItemDTOList = new ArrayList<>();
        for (OrderLineItem orderLineItem : orderLineItems) {
            OrderLineItemDTO orderLineItemDTO = new OrderLineItemDTO();
            BeanUtils.copyProperties(orderLineItem, orderLineItemDTO);
            orderLineItemDTOList.add(orderLineItemDTO);
        }
        return orderLineItemDTOList;
    }

    public boolean validateToken(String token){
        return Boolean.TRUE.equals(authorizationProxy.validateToken(token).getBody());
    }

    public boolean isUserAdmin(String token) {
        return Boolean.TRUE.equals(authorizationProxy.isUserAdmin(token).getBody());
    }
}