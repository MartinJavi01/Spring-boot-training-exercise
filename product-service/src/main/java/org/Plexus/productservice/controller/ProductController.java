package org.Plexus.productservice.controller;

import org.Plexus.productservice.dto.ProductDTO;
import org.Plexus.productservice.model.Product;
import org.Plexus.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Object> getAllProducts(@RequestParam(name = "name", required = false) String name) {
        List<ProductDTO> products;

        if(name == null) {
            products = productService.getAllProducts();
        } else {
            products = productService.getProductsByName(name);
        }

        if(products.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable String id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping(value = "/{id}/price")
    public ResponseEntity<Object> getPriceById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getPriceById(id));
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Object> createProduct(@Validated @RequestBody Product product,
                                                 @RequestParam(name = "quantity", defaultValue = "10")int quantity,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(productService.validateToken(token) && productService.isUserAdmin(token)) {
            String skuCode = productService.generateId();
            product.setId(skuCode);
            productService.createProduct(product,skuCode, quantity, token);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping(value = "/{id}",  consumes = {"application/json"})
    public ResponseEntity<Object> updateProduct(@Validated @RequestBody Product product, @PathVariable String id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(productService.validateToken(token) && productService.isUserAdmin(token)) {
            Product dbProduct = productService.getProductByIdNoDTO(id);
            dbProduct.setName(product.getName());
            dbProduct.setDescription(product.getDescription());
            dbProduct.setPrice(product.getPrice());
            productService.updateProduct(dbProduct);
            return ResponseEntity.ok(dbProduct);
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable String id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        if(productService.validateToken(token) && productService.isUserAdmin(token)) {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }
}
