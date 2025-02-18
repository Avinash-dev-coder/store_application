package com.grid.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
public class ProductDto {
    private Long productId;
    private String title;
    private int available;
    private BigDecimal price;
}
