package com.grid.store.serviceImpl;

import com.grid.store.converter.ProductConverter;
import com.grid.store.dto.ProductDto;
import com.grid.store.entity.Product;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.repository.ProductRepository;
import com.grid.store.service.ProductService;
import com.grid.store.utilities.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Validated
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> getAllProduct() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No products found.");
        }
        return products.stream()
                .map(ProductConverter :: convertEntityToDto)
                .collect(Collectors.toList());

    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with ID " + id + " not found."));
        return ProductConverter.convertEntityToDto(product);
    }

    @Override
    public void updateProductAvailability(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found."));

        // Check for sufficient stock
        if (quantity > product.getAvailable()) {
            throw new BadRequestException("Insufficient stock for product ID " + productId + ". Available: "
                    + product.getAvailable() + ", Requested: " + quantity);
        }
        product.setAvailable(product.getAvailable() - quantity);
        productRepository.save(product);
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        //validate product for bad request
        validateProduct(productDto);
        //save the product
        Product product = ProductConverter.convertDtoToEntity(productDto);
        return ProductConverter.convertEntityToDto(productRepository.save(product));
    }


    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));

        productRepository.delete(product);
    }


    @Override
    public boolean checkProductStocks(Product product, int quantity){
        //checks for product availability
        return product.getAvailable() != 0 && product.getAvailable() >= quantity;
    }

    private void validateProduct(ProductDto productDto) {
        if (Validator.isNull(productDto)) {
            throw new BadRequestException("Product cannot be null.");
        }

        if(Validator.isBlank(productDto.getTitle())){
            throw new BadRequestException("Product title must not be empty.");
        }

        if (productDto.getPrice() == null || productDto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Price must be greater than zero.");
        }

        if (productDto.getAvailable() < 0) {
            throw new BadRequestException("Quantity cannot be negative.");
        }
    }


}

