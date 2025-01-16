package com.grid.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Setter
@Getter
@Entity
@Table(name = "product")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "title")
    private String title;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "quantity_id") // Assuming this is the foreign key column in the "product" table
    @Column(name = "available")
    private int available;

    @Column(name = "price")
    private BigDecimal price = new BigDecimal(BigInteger.ZERO);

}
