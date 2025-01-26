package com.grid.store.service;

import com.grid.store.dto.ProductDto;
import com.grid.store.entity.Product;

import java.util.List;

public interface ProductService {

    public List<ProductDto> getAllProduct();

    public ProductDto getProductById(Long productId);

    public Product getProduct(Long productId);

    public void updateProductAvailability(Long productId, int quantity);

    public ProductDto addProduct(ProductDto productDto);

    public void deleteProductById(Long id);

    public boolean checkProductStocks(Product product, int quantity);
}
