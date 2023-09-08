package org.plexus.orderservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {
    @GetMapping("/inventory/{skuCode}/quantity")
    int retrieveInventoryQuantity(@PathVariable String skuCode,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PutMapping("/inventory/{skuCode}")
    void editInventoryStock(@PathVariable String skuCode,
                                              @RequestParam(name = "quantity") String quantity,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}