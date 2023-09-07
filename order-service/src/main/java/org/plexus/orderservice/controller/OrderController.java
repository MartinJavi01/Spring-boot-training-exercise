package org.plexus.orderservice.controller;

import org.plexus.orderservice.dto.OrderDTO;
import org.plexus.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Object> getAllOrders(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable(name = "id") long id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> createOrder(@PathVariable String id, @RequestParam(name = "quantity", defaultValue = "10") int quantity,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            OrderDTO orderDTO = orderService.createOrder(id, quantity, token);
            orderService.createOrderLineItem(orderDTO.getOrderNumber(), id, quantity, orderDTO.getPrice()/quantity);
            return new ResponseEntity<>(orderService.getOrderById(orderDTO.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{orderNumber}/add/{skuCode}")
    public ResponseEntity<Object> addProductToOrder(@PathVariable String orderNumber, @PathVariable String skuCode,
                                                    @RequestParam(name = "quantity", defaultValue = "10") int quantity,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            return new ResponseEntity<>(orderService.addProductToOrder(orderNumber, skuCode, quantity, token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{orderNumber}/remove/{skuCode}")
    public ResponseEntity<Object> removeProductFromOrder(@PathVariable String orderNumber, @PathVariable String skuCode,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            return new ResponseEntity<>(orderService.removeProductFromOrder(orderNumber, skuCode), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable(name = "id") long id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(orderService.validateToken(token)) {
            orderService.deleteOrder(id);
            return new ResponseEntity<>("Order successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }
}