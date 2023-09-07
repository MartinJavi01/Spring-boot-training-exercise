package org.plexus.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.plexus.orderservice.dto.OrderDTO;
import org.plexus.orderservice.model.Order;
import org.plexus.orderservice.model.OrderLineItem;
import org.plexus.orderservice.proxy.AuthorizationProxy;
import org.plexus.orderservice.proxy.InventoryProxy;
import org.plexus.orderservice.proxy.ProductProxy;
import org.plexus.orderservice.repository.OrderLineItemRepository;
import org.plexus.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineItemRepository orderLineItemRepository;

    @Mock
    private ProductProxy productProxy;
    @Mock
    private InventoryProxy inventoryProxy;

    @Mock
    private AuthorizationProxy authorizationProxy;
    @Autowired
    private ModelMapper modelMapper; // Inyectar ModelMapper

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    void createOrderLineItem() {
        // Configura un objeto Order simulado
        Order simulatedOrder = new Order();
        simulatedOrder.setOrderNumber("123");

        // Configura el comportamiento del mock de OrderRepository para devolver el objeto simulado
        when(orderRepository.findByOrderNumber("123")).thenReturn(java.util.Optional.of(simulatedOrder));

        // Llama al método que deseas probar
        orderService.createOrderLineItem("123", "SKU123", 3, 15.0);

        // Verifica que se haya guardado el OrderLineItem en el repositorio adecuado
        verify(orderLineItemRepository).save(any(OrderLineItem.class));

        // Verifica que el Order se haya actualizado correctamente
        assertEquals(1, simulatedOrder.getOrderLineItems().size());
    }

    @Test
    void isEnoughQuantity() {
        // Configurar datos de prueba
        String skuCode = "SKU001";
        int requestedQuantity = 5;
        int availableQuantity = 10;
        String token = "validToken";

        // Mockear el comportamiento del inventoryProxy
        when(inventoryProxy.retrieveInventoryQuantity(skuCode, token)).thenReturn(availableQuantity);

        // Llamar al método isEnoughQuantity
        orderService.isEnoughQuantity(skuCode, requestedQuantity, token);

        // Verificar que el inventoryProxy se llamó para obtener la cantidad del producto
        verify(inventoryProxy, times(1)).retrieveInventoryQuantity(skuCode, token);

    }

    @Test
    void isProductInOrder() {
        // Configurar datos de prueba
        String skuCode = "SKU001";
        Order order = new Order();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(skuCode);
        orderLineItems.add(orderLineItem);
        order.setOrderLineItems(orderLineItems);

        // Llamar al método isProductInOrder
        boolean result = orderService.isProductInOrder(order, skuCode);

        // Verificar que el producto esté en la orden
        assertTrue(result);
    }

    @Test
    void getAllOrders() {
        // Configurar datos de prueba
        List<Order> mockOrders = new ArrayList<>();
        mockOrders.add(new Order());
        mockOrders.add(new Order());
        mockOrders.add(new Order());

        // Mockear el comportamiento del orderRepository para devolver las órdenes simuladas
        when(orderRepository.findAll()).thenReturn(mockOrders);

        // Llamar al método getAllOrders
        List<OrderDTO> resultOrders = orderService.getAllOrders();

        // Verificar que el orderRepository se llamó para obtener todas las órdenes
        verify(orderRepository, times(1)).findAll();

        // Verificar que el resultado del método contiene la misma cantidad de órdenes simuladas
        assertEquals(mockOrders.size(), resultOrders.size());
    }

    @Test
    void deleteOrder() {
        // Configurar datos de prueba
        long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);

        // Mockear el comportamiento del orderRepository para devolver la orden simulada
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Llamar al método deleteOrder
        orderService.deleteOrder(orderId);

        // Verificar que el orderRepository se llamó para obtener la orden por su ID
        verify(orderRepository, times(1)).findById(orderId);

        // Verificar que el orderRepository se llamó para eliminar la orden
        verify(orderRepository, times(1)).delete(mockOrder);

    }

    @Test
    void validateToken() {
        // Simular un token válido utilizando Mockito
        when(authorizationProxy.validateToken("token_valido")).thenReturn(ResponseEntity.ok(true));

        boolean result = orderService.validateToken("token_valido");

        assertTrue(result);
    }

    @Test
    void isUserAdmin() {
        // Simular un usuario administrador utilizando Mockito
        when(authorizationProxy.isUserAdmin("token_admin")).thenReturn(ResponseEntity.ok(true));

        boolean result = orderService.isUserAdmin("token_admin");

        assertTrue(result);
    }
}