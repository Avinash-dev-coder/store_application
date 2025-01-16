package com.grid.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Column(name = "total_price")
    private BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO);

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


    @Column(name = "create_timestamp")
    private Timestamp createTimestamp;
}

