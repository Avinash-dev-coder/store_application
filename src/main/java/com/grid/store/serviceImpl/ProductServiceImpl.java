package com.grid.store.serviceImpl;

import com.grid.store.dto.ProductDto;
import com.grid.store.entity.Product;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.mapper.ProductMapper;
import com.grid.store.repository.ProductRepository;
import com.grid.store.service.ProductService;
import com.grid.store.utilities.Constants;
import com.grid.store.utilities.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;


    @Override
    public List<ProductDto> getAllProduct() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, Constants.NO_PRODUCTS_FOUND);
        }
        return products.stream()
                .map(productMapper :: productToProductDto)
                .collect(Collectors.toList());

    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.PRODUCT_WITH_ID + id + Constants.NOT_FOUND));
        return productMapper.productToProductDto(product);
    }

    @Override
    public Product getProduct(Long id) {
        return productMapper.productDtoToProduct(getProductById(id));
    }

    @Override
    public void updateProductAvailability(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Constants.PRODUCT_WITH_ID + productId + Constants.NOT_FOUND));

        // Check for sufficient stock
        if (quantity > product.getAvailable()) {
            throw new BadRequestException(Constants.INSUFFICIENT_STOCK_FOR_PRODUCT_ID + productId + ". Available: "
                    + product.getAvailable() + ", Requested: " + quantity);
        }
        product.setAvailable(product.getAvailable() - quantity);
        try {
            productRepository.save(product);
        }catch (ObjectOptimisticLockingFailureException e){
            throw new BadRequestException("Invalid quantity");
        }
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        //validate product for bad request
        validateProduct(productDto);
        //save the product
        Product product = productMapper.productDtoToProduct(productDto);
        product.setProductId(null);
        Product savedProduct = productRepository.save(product);
        return productMapper.productToProductDto(savedProduct);
    }


    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.PRODUCT_NOT_FOUND_WITH_ID + id));

        productRepository.delete(product);
    }


    @Override
    public boolean checkProductStocks(Product product, int quantity){
        //checks for product availability
        return product.getAvailable() != 0 && product.getAvailable() >= quantity;
    }

    private void validateProduct(ProductDto productDto) {
        if (Validator.isNull(productDto)) {
            throw new BadRequestException(Constants.PRODUCT_CANNOT_BE_NULL);
        }

        if(Validator.isBlank(productDto.getTitle())){
            throw new BadRequestException(Constants.PRODUCT_TITLE_MUST_NOT_BE_EMPTY);
        }

        if (productDto.getPrice() == null || productDto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(Constants.PRICE_MUST_BE_GREATER_THAN_ZERO);
        }

        if (productDto.getAvailable() < 0) {
            throw new BadRequestException("Quantity cannot be negative.");
        }
    }


}

