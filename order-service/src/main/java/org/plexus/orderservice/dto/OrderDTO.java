package org.plexus.orderservice.dto;

import java.util.List;

public class OrderDTO {
    private Long id;
    private String orderNumber;
    private List<OrderLineItemDTO> orderLineItemListDTO;
    private double price;

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderLineItemDTO> getOrderLineItemList() {
        return orderLineItemListDTO;
    }

    public void setOrderLineItemList(List<OrderLineItemDTO> orderLineItemList) {
        this.orderLineItemListDTO = orderLineItemList;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Constructor
    public OrderDTO() {
        super();
    }
}