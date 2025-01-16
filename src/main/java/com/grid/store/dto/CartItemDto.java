package com.grid.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDto {

    private String productName;
    private int quantity;
    private BigDecimal subTotal;

}
