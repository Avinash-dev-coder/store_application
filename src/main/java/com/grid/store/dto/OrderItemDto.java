package com.grid.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrderItemDto {
    private ProductDto productDto;
    private int quantity;
    private String status;
    private Timestamp createTimestamp;
}

