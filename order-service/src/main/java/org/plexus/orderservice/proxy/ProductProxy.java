package org.plexus.orderservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductProxy {
    @GetMapping("/products/{id}/price")
    Double viewProductByPrice(@PathVariable String id);
}