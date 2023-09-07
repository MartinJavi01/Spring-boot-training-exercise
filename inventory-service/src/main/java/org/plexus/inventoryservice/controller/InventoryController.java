package org.plexus.inventoryservice.controller;

import org.plexus.inventoryservice.excepciones.ResourceNotFoundException;
import org.plexus.inventoryservice.model.Inventory;
import org.plexus.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Object> getInventory (@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            return ResponseEntity.ok(inventoryService.getAll());
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/{skuCode}")
    public ResponseEntity<Object> getInventoryBySkuCode(@PathVariable String skuCode,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            try {
                Inventory inventory = inventoryService.getInventoryBySkuCode(skuCode);
                return ResponseEntity.ok(inventory);
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/{skuCode}/quantity")
    public ResponseEntity<Object> getQuantityBySkuCode(@PathVariable String skuCode,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            try {
                Inventory inventory = inventoryService.getInventoryBySkuCode(skuCode);
                return ResponseEntity.ok(inventory.getQuantity());
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/new/{skuCode}")
    public ResponseEntity<Object> addNewInventory(@PathVariable String skuCode,
                                                  @RequestParam(value="quantity", defaultValue = "10") int quantity,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            Inventory inventory = new Inventory(skuCode,quantity);
            return ResponseEntity.ok(inventoryService.create(inventoryService.mapearDTO(inventory)));
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping(value = "/{skuCode}")
    public ResponseEntity<Object> deleteQuantityByskuCode(@PathVariable String skuCode,
                                                          @RequestParam (name = "quantity") String quantity,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            inventoryService.modifyQuantity(skuCode,Integer.parseInt(quantity));
            return ResponseEntity.ok("Quantity deleted successfully");
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "/{skuCode}")
    public ResponseEntity<Object> deleteItem (@PathVariable String skuCode,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        if(inventoryService.validateToken(token) && inventoryService.isUserAdmin(token)) {
            inventoryService.deleteBySkuCode(skuCode);
            return ResponseEntity.ok("deleted successfully");
        } else {
            return new ResponseEntity<>("Token not valid or user is not admin", HttpStatus.UNAUTHORIZED);
        }
    }

}
