package com.grid.store.controller;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartRequest;
import com.grid.store.service.CartService;
import com.grid.store.utilities.Validator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add-cart-item")
    public ResponseEntity<CartDto> addItem(HttpSession session, @RequestBody  CartRequest cartRequest) {
        Long userId = Validator.getUserId(session);
        CartDto cartDto = cartService.addItem(userId, cartRequest);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @PostMapping("/remove-cart-item")
    public ResponseEntity<CartDto> removeItemFromCart(HttpSession session, @RequestBody  CartRequest cartRequest){
        Long userId = Validator.getUserId(session);
        CartDto cartDto = cartService.removeItem(userId, cartRequest);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @GetMapping("/get-all-cart-item")
    public ResponseEntity<CartDto> removeItemFromCart(HttpSession session){
        Long userId = Validator.getUserId(session);
        CartDto cartDto = cartService.getAllItem(userId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/remove-all-cart-items")
    public ResponseEntity<String> removeAllCartItems(HttpSession session){
        Long userId = Validator.getUserId(session);
        cartService.removeAllItem(userId);
        return new ResponseEntity<>("All items removed from the cart", HttpStatus.OK);
    }
}
