package org.plexus.inventoryservice.service;

import org.modelmapper.ModelMapper;
import org.plexus.inventoryservice.InventoryServiceApplication;
import org.plexus.inventoryservice.dto.InventoryDTO;
import org.plexus.inventoryservice.excepciones.ResourceNotFoundException;
import org.plexus.inventoryservice.model.Inventory;
import org.plexus.inventoryservice.proxy.AuthorizationProxy;
import org.plexus.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthorizationProxy authorizationProxy;

    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryBySkuCode(String skuCode) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Inventory not found");
        }

        Inventory inventory = inventoryOptional.get();

        if (inventory.getQuantity() <= 0) {
            throw new ResourceNotFoundException("Out of stock");
        }

        return inventory;
    }

    public InventoryDTO create(InventoryDTO inventoryDTO) {
        Inventory inventory = mapearModel(inventoryDTO);
        if ( inventory.getQuantity() <= 0){
            throw new ResourceNotFoundException("Error, quantity can not be less than 1");
        }
        Inventory newInventory = inventoryRepository.save(inventory);

        return mapearDTO(newInventory);
    }

    public InventoryDTO updateById(InventoryDTO inventoryDTO, int quantity) {
        Inventory inventory = mapearModel(inventoryDTO);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
        return mapearDTO(inventory);
    }

    public void deleteBySkuCode(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).orElseThrow();
        inventoryRepository.deleteById(inventory.getId());
    }

    public void modifyQuantity(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).orElseThrow();
        int newQuantity = inventory.getQuantity()+quantity;
        if ( newQuantity < 0){
            throw new ResourceNotFoundException("Error, not enought inventory");
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }


    public InventoryDTO mapearDTO(Inventory inventory) {
        InventoryDTO inventoryDTO = modelMapper.map(inventory, InventoryDTO.class);
        return inventoryDTO;
    }

    public Inventory mapearModel(InventoryDTO inventoryDTO) {
        Inventory inventory = modelMapper.map(inventoryDTO, Inventory.class);
        return inventory;
    }

    public boolean validateToken(String token){
        return Boolean.TRUE.equals(authorizationProxy.validateToken(token).getBody());
    }

    public boolean isUserAdmin(String token) {
        return Boolean.TRUE.equals(authorizationProxy.isUserAdmin(token).getBody());
    }
}

