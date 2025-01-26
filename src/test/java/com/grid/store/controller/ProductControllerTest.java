package com.grid.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grid.store.dto.ProductDto;
import com.grid.store.service.ProductService;
import com.grid.store.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setProductId(1L);
        productDto.setTitle("Test product");
        productDto.setPrice(new BigDecimal("100"));
        productDto.setAvailable(10);

    }



    @Test
    void testGetAllProduct() throws Exception {
        when(productService.getAllProduct()).thenReturn(List.of(productDto));
        mockMvc.perform(get("/product/get-all-product")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test product"))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[0].available").value(10));



    }

    @Test
    void testGetProductById() throws Exception {
        Long productId = 1L;
        when(productService.getProductById(eq(productId))).thenReturn(productDto);
        mockMvc.perform(get("/product/get-product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.title").value("Test product"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.available").value(10));

    }

    @Test
    void testAddProduct() throws Exception{
        when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);
        mockMvc.perform(post("/product/add-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.title").value("Test product"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.available").value(10));

    }

    @Test
    void testDeleteProductById() throws Exception {
        Long productId = 1L;

        doNothing().when(productService).deleteProductById(productId);


        mockMvc.perform(delete("/product/delete-product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Constants.PRODUCT_DELETED));
    }

}