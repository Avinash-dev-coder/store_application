package com.grid.store.converter;

import com.grid.store.dto.ProductDto;
import com.grid.store.entity.Product;

public class ProductConverter {
    public static ProductDto convertEntityToDto(Product product){
        if (product == null) {
            return null;
        }
        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setTitle(product.getTitle());
        productDto.setPrice(product.getPrice());
        productDto.setAvailable(product.getAvailable());

        return productDto;
    }

    public static Product convertDtoToEntity(ProductDto productDto){
        if (productDto == null) {
            return null;
        }

        Product product = new Product();

        product.setTitle(productDto.getTitle());
        product.setPrice(productDto.getPrice());
        product.setAvailable(productDto.getAvailable());
        return product;
    }
}
