package com.grid.store.serviceImpl;

import com.grid.store.converter.ProductConverter;
import com.grid.store.dto.ProductDto;
import com.grid.store.entity.Product;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setProductId(1L);
        product.setTitle("Test Product");
        product.setPrice(new BigDecimal("100"));
        product.setAvailable(10);

        productDto = ProductConverter.convertEntityToDto(product);
    }

    @Test
    void testGetAllProduct_NoContent() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> productService.getAllProduct()
        );

        assertEquals("204 NO_CONTENT \"No products found.\"", exception.getMessage());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProduct_Success() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        var products = productService.getAllProduct();

        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto retrievedProduct = productService.getProductById(1L);

        assertNotNull(retrievedProduct);
        assertEquals("Test Product", retrievedProduct.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> productService.getProductById(1L)
        );

        assertEquals("Product with ID 1 not found.", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProductAvailability_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProductAvailability(1L, 5);

        assertEquals(5, product.getAvailable());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProductAvailability_InsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> productService.updateProductAvailability(1L, 15)
        );

        assertEquals(
                "Insufficient stock for product ID 1. Available: 10, Requested: 15",
                exception.getMessage()
        );
        verify(productRepository, never()).save(product);
    }

    @Test
    void testAddProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto savedProduct = productService.addProduct(productDto);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getTitle());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProduct_InvalidProduct() {
        ProductDto invalidProduct = new ProductDto();
        invalidProduct.setTitle("");
        invalidProduct.setPrice(new BigDecimal("-1"));
        invalidProduct.setAvailable(-5);

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> productService.addProduct(invalidProduct)
        );

        assertEquals("Product title must not be empty.", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProductById(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> productService.deleteProductById(1L)
        );

        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }
}
