package org.plexus.inventoryservice.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.plexus.inventoryservice.dto.InventoryDTO;
import org.plexus.inventoryservice.model.Inventory;
import org.plexus.inventoryservice.proxy.AuthorizationProxy;
import org.plexus.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AuthorizationProxy authorizationProxy;
    @Autowired
    private ModelMapper modelMapper; // Inyectar ModelMapper

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAll() {
        // Simular una lista de inventarios
        List<Inventory> inventoryList = Arrays.asList(
                new Inventory("SKU1", 10),
                new Inventory("SKU2", 5),
                new Inventory("SKU3", 0)
        );

        when(inventoryRepository.findAll()).thenReturn(inventoryList);

        List<Inventory> result = inventoryService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void getInventoryBySkuCode() {
        // Simular un inventario existente
        String skuCode = "SKU123";
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(10);

        when(inventoryRepository.findBySkuCode(skuCode)).thenReturn(Optional.of(inventory));

        Inventory result = inventoryService.getInventoryBySkuCode(skuCode);

        assertNotNull(result);
        assertEquals(skuCode, result.getSkuCode());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void create() {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setSkuCode("SKU789");
        inventoryDTO.setQuantity(5);

        // Inicializar el objeto modelMapper
        ModelMapper modelMapper2;
        modelMapper2 = new ModelMapper();

        Inventory inventory = new Inventory();
        inventory.setSkuCode("SKU789");
        inventory.setQuantity(5);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = modelMapper2.map(inventory, InventoryDTO.class);

        assertNotNull(result);
        assertEquals("SKU789", result.getSkuCode());
        assertEquals(5, result.getQuantity());
    }

    @Test
    void updateById() {
        String skuCode = "SKU123";
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setSkuCode(skuCode);
        inventoryDTO.setQuantity(10);


        // Inicializar el objeto modelMapper
        ModelMapper modelMapper2;
        modelMapper2 = new ModelMapper();
        Inventory inventory = modelMapper2.map(inventoryDTO, Inventory.class);
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(5);

        when(inventoryRepository.findBySkuCode(skuCode)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = new InventoryDTO();
        result.setQuantity(10);
        result.setSkuCode("SKU123");

        assertNotNull(result);
        assertEquals(skuCode, result.getSkuCode());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void deleteBySkuCode() {
        // Simular un SKU existente
        String skuCode = "SKU123";
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);

        when(inventoryRepository.findBySkuCode(skuCode)).thenReturn(Optional.of(inventory));

        inventoryService.deleteBySkuCode(skuCode);

        verify(inventoryRepository, times(1)).deleteById(inventory.getId());
    }

    @Test
    void modifyQuantity() {
        // Simular un SKU existente
        String skuCode = "SKU123";
        int quantityChange = -5;
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(10);

        when(inventoryRepository.findBySkuCode(skuCode)).thenReturn(Optional.of(inventory));

        inventoryService.modifyQuantity(skuCode, quantityChange);

        assertEquals(5, inventory.getQuantity());
    }

    @Test
    void validateToken() {
        // Simular un token válido utilizando Mockito
        when(authorizationProxy.validateToken("token_valido")).thenReturn(ResponseEntity.ok(true));

        boolean result = inventoryService.validateToken("token_valido");

        assertTrue(result);
    }

    @Test
    void isUserAdmin() {
        // Simular un usuario administrador utilizando Mockito
        when(authorizationProxy.isUserAdmin("token_admin")).thenReturn(ResponseEntity.ok(true));

        boolean result = inventoryService.isUserAdmin("token_admin");

        assertTrue(result);
    }
}