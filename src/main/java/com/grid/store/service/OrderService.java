package com.grid.store.service;

import com.grid.store.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(Long userId);

    void cancelOrder(Long userId, Long orderId);

    List<OrderDto> getAllOrdersByUserId(Long userId);


}
