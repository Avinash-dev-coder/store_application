package com.grid.store.service;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartRequest;
import com.grid.store.entity.Cart;

public interface CartService {

    CartDto addItem(Long userId, CartRequest cartRequest);

    CartDto removeItem(Long userId, CartRequest cartRequest);

    CartDto getAllItem(Long userId);

    void removeAllItem(Long userId);

    Cart getCart(Long cartId);

    void saveCart(Cart cart);


}
