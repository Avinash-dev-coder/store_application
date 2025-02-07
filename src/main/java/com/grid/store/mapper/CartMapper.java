package com.grid.store.mapper;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartItemDto;
import com.grid.store.entity.Cart;
import com.grid.store.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CartMapper {


    public CartItemDto cartItemToCartItemDto(CartItem cartItem){
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductName(cartItem.getProduct().getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setSubTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return cartItemDto;
    }


    public CartDto cartToCartDto(Cart cart){
        CartDto cartDto = new CartDto();
        List<CartItemDto> cartItemDtoList = cart.getCartItemList().stream()
                .map(this::cartItemToCartItemDto)
                .toList();

        cartDto.setCartItemDtoList(cartItemDtoList);
        cartDto.setTotalPrice(cart.getTotalPrice());

        return cartDto;
    }
}
