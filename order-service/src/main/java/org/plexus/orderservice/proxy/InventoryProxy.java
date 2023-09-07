package org.plexus.orderservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {
    @GetMapping("/inventory/{skuCode}/quantity")
    int retrieveInventoryQuantity(@PathVariable String skuCode, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}