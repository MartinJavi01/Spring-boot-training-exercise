package org.Plexus.productservice.service;

import org.Plexus.productservice.dto.ProductDTO;
import org.Plexus.productservice.model.Product;
import org.Plexus.productservice.proxy.AuthorizationProxy;
import org.Plexus.productservice.proxy.InventoryProxy;
import org.Plexus.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryProxy inventoryProxy;

    @Mock
    private AuthorizationProxy authorizationProxy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllProducts() {
        // Mock data
        List<Product> productList = new ArrayList<>();
        when(productRepository.findAll()).thenReturn(productList);

        // Test
        List<ProductDTO> result = productService.getAllProducts();

        // Assertions
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductById() {
        // Arrange
        String productId = "123";
        Product product = new Product();
        ProductDTO productDTO = new ProductDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO, result);
    }

    @Test
    void getPriceById() {
        // Mock data
        String productId = "1";
        Double expectedPrice = 100.0;
        Product product = new Product();
        product.setPrice(expectedPrice);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Test
        Double result = productService.getPriceById(productId);

        // Assertions
        assertNotNull(result);
        assertEquals(expectedPrice, result);
    }

    @Test
    void getProductByIdNoDTO() {
        // Mock data
        String productId = "1";
        Product expectedProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // Test
        Product result = productService.getProductByIdNoDTO(productId);

        // Assertions
        assertNotNull(result);
        assertEquals(expectedProduct, result);
    }

    @Test
    void getProductsByName() {
        // Mock data
        String productName = "TestProduct";
        List<Product> productList = new ArrayList<>();
        productList.add(new Product());
        when(productRepository.findByNameRegexIgnoreCase(productName)).thenReturn(productList);

        List<ProductDTO> expectedDTOList = new ArrayList<>();
        expectedDTOList.add(new ProductDTO());

        when(productMapper.toDTOList(productList)).thenReturn(expectedDTOList);

        // Test
        List<ProductDTO> result = productService.getProductsByName(productName);

        // Assertions
        assertNotNull(result);
        assertEquals(expectedDTOList, result);
    }

    @Test
    void createProduct() {
        // Datos de ejemplo
        Product p = new Product();
        String id = "123";
        int quantity = 10;
        String token = "token";

        // Llamamos al método bajo prueba
        Product savedProduct = new Product(); // Producto simulado guardado
        when(productRepository.save(p)).thenReturn(savedProduct);

        ProductDTO productDTO = new ProductDTO(); // DTO simulado
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTO);

        // Verificamos que el método inventoryProxy.createInventoryForItem se llama con los argumentos correctos
        productService.createProduct(p, id, quantity, token);
        verify(inventoryProxy, times(1)).createInventoryForItem(id, quantity, token);

        // Verificamos que el método productRepository.save se llama con el producto adecuado
        verify(productRepository, times(1)).save(p);

        // Verificamos que el método productMapper.toDTO se llama con el producto simulado guardado
        verify(productMapper, times(1)).toDTO(savedProduct);

        // Verificamos que el resultado sea el esperado
        ProductDTO result = productService.createProduct(p, id, quantity, token);
        assertEquals(productDTO, result);
    }

    @Test
    void updateProduct() {
        // Arrange
        Product inputProduct = new Product();
        Product savedProduct = new Product();
        ProductDTO savedProductDTO = new ProductDTO();

        when(productRepository.save(inputProduct)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(savedProductDTO);

        // Act
        ProductDTO result = productService.updateProduct(inputProduct);

        // Assert
        assertNotNull(result);
        assertEquals(savedProductDTO, result);
    }

    @Test
    void deleteProduct() {
        // Mock data
        String productId = "1";

        // Test
        productService.deleteProduct(productId);

        // Verify that deleteById was called with the correct argument
        verify(productRepository).deleteById(productId);
    }

    @Test
    void generateId() {
        // Mock data
        when(productRepository.findById(anyString())).thenReturn(Optional.empty());

        // Test
        String generatedId = productService.generateId();

        // Assertions
        assertNotNull(generatedId);
        assertTrue(generatedId.startsWith("sku"));
    }

    @Test
    void validateToken() {
        // Simular un token válido utilizando Mockito
        when(authorizationProxy.validateToken("token_valido")).thenReturn(ResponseEntity.ok(true));

        boolean result = productService.validateToken("token_valido");

        assertTrue(result);
    }

    @Test
    void isUserAdmin() {
        // Simular un usuario administrador utilizando Mockito
        when(authorizationProxy.isUserAdmin("token_admin")).thenReturn(ResponseEntity.ok(true));

        boolean result = productService.isUserAdmin("token_admin");


    }
}