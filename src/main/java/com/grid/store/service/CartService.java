package com.grid.store.service;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartRequest;

public interface CartService {

    CartDto addItem(long userId, CartRequest cartRequest);

    CartDto removeItem(long userId, CartRequest cartRequest);

    CartDto getAllItem(long userId);

    void removeAllItem(long userId);


}
