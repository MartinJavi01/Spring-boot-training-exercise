package org.Plexus.productservice.service;

import org.Plexus.productservice.dto.ProductDTO;
import org.Plexus.productservice.proxy.AuthorizationProxy;
import org.Plexus.productservice.proxy.InventoryProxy;
import org.Plexus.productservice.repository.ProductRepository;
import org.Plexus.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private InventoryProxy inventoryProxy;
    @Autowired
    private AuthorizationProxy authorizationProxy;

    public List<ProductDTO> getAllProducts() {
        return productMapper.toDTOList(productRepository.findAll());
    }

    public ProductDTO getProductById(String id) {
        return productMapper.toDTO(productRepository.findById(id).orElseThrow());
    }

    public Double getPriceById(String id) {
        return productRepository.findById(id).orElseThrow().getPrice();
    }

    public Product getProductByIdNoDTO(String id) {
        return productRepository.findById(id).orElseThrow();
    }

    public List<ProductDTO> getProductsByName(String name) {
        return productMapper.toDTOList(productRepository.findByNameRegexIgnoreCase(name));
    }
    public ProductDTO createProduct(Product p, String id, int quantity, String token) {
        inventoryProxy.createInventoryForItem(id, quantity, token);
        return productMapper.toDTO(productRepository.save(p));
    }

    public ProductDTO updateProduct(Product p) {
        return productMapper.toDTO(productRepository.save(p));
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public String generateId() {
        String skuCode;
        do {
            skuCode = "sku" + new Random().nextLong();
        }while(productRepository.findById(skuCode).isPresent());
        return skuCode;
    }

    public boolean validateToken(String token){
        return Boolean.TRUE.equals(authorizationProxy.validateToken(token).getBody());
    }

    public boolean isUserAdmin(String token) {
        return Boolean.TRUE.equals(authorizationProxy.isUserAdmin(token).getBody());
    }
}
