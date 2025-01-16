package com.grid.store.converter;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartItemDto;
import com.grid.store.entity.Cart;
import com.grid.store.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class CartConverter {
    public static CartItemDto convertEntityToDto(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductName(cartItem.getProduct().getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setSubTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return cartItemDto;
    }

    public static CartDto convertEntityToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        List<CartItemDto> cartItemDtoList = cart.getCartItemList().stream()
                .map(CartConverter::convertEntityToDto)
                .toList();

        cartDto.setCartItemDtoList(cartItemDtoList);
        cartDto.setTotalPrice(cart.getTotalPrice());

        return cartDto;
    }


}
