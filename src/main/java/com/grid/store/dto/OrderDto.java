package com.grid.store.dto;

import com.grid.store.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Getter
@Setter
public class OrderDto {

    private Long orderId;
    private BigDecimal totalPrice;
    private Status status;
    private Timestamp createTimestamp;
}

