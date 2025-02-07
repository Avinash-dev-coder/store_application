package com.grid.store.mapper;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartItemDto;
import com.grid.store.dto.OrderDto;
import com.grid.store.entity.Cart;
import com.grid.store.entity.CartItem;
import com.grid.store.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto orderToOrderDto(Order order);
}
