package com.grid.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {
    private Long productId;
    private int quantity;
}
