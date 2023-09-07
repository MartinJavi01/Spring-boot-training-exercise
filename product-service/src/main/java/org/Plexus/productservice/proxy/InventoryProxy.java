package org.Plexus.productservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {

    @PostMapping("/inventory/new/{skuCode}")
    public ResponseEntity<Object> createInventoryForItem
            (@PathVariable String skuCode, @RequestParam(name = "quantity", defaultValue = "10") int quantity,
             @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
