package org.Plexus.productservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "gateway-service")
public interface AuthorizationProxy {

    @GetMapping("user/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
    @GetMapping("user/isAdmin")
    public ResponseEntity<Boolean> isUserAdmin(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
