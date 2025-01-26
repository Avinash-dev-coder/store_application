package com.grid.store.serviceImpl;

import com.grid.store.converter.CartConverter;
import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartRequest;
import com.grid.store.entity.Cart;
import com.grid.store.entity.CartItem;
import com.grid.store.entity.Product;
import com.grid.store.entity.User;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.repository.CartRepository;
import com.grid.store.repository.ProductRepository;
import com.grid.store.repository.UserRepository;
import com.grid.store.service.CartService;
import com.grid.store.service.ProductService;
import com.grid.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private  CartRepository cartRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;


    @Override
    public CartDto addItem(Long userId, CartRequest cartRequest) {
        User user = getUser(userId);

        Product product = productService.getProduct(cartRequest.getProductId());
        Cart cart = user.getCart();

        // If no cart exists, create a new one and associate with the user
        if (cart == null) {
            cart = new Cart();
        }

        // Check if the product already exists in the cart (by product ID)
        Optional<CartItem> existingItemOpt = cart.getCartItemList().stream()
                .filter(item -> Objects.equals(item.getProduct().getProductId(), cartRequest.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            if(!productService.checkProductStocks(existingItem.getProduct(), cartRequest.getQuantity())){
                throw new BadRequestException("Insufficient stock for product ID " +
                        product.getProductId() + ". Available: " + product.getAvailable() +
                        ", Requested: " + cartRequest.getQuantity());
            }
            existingItem.setQuantity(cartRequest.getQuantity());
            return CartConverter.convertEntityToDto(cartRepository.save(cart));
        }
        // Add a new CartItem if it doesn't already exist
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(cartRequest.getQuantity());
        cart.getCartItemList().add(cartItem);
        user.setCart(cart);
        return CartConverter.convertEntityToDto(userService.saveUser(user).getCart());

    }




    @Override
    public CartDto removeItem(Long userId, CartRequest cartRequest) {
        User user = getUser(userId);
        //get requested product
        Product product = productService.getProduct(cartRequest.getProductId());
        Cart cart = user.getCart();
        // Check if the product already exists in the cart (by product ID)
        Optional<CartItem> existingItemOpt = cart.getCartItemList().stream()
                .filter(item -> Objects.equals(item.getProduct().getProductId(), cartRequest.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Update the quantity of the existing cart item
            CartItem existingItem = existingItemOpt.get();
            if(existingItem.getQuantity() > cartRequest.getQuantity()){
                existingItem.setQuantity(existingItem.getQuantity() - cartRequest.getQuantity());
            }else {
                cart.getCartItemList().remove(existingItem);
            }
        } else {
            throw new NotFoundException("No Product found with ID: " + cartRequest.getProductId() + " in the cart");
        }
        return CartConverter.convertEntityToDto(cartRepository.save(cart));

    }

    @Override
    public CartDto getAllItem(Long userId) {
        User user = getUser(userId);
        Cart cart = user.getCart();

        // If no cart exists, create a new one and associate with the user
        if (cart == null || cart.getCartItemList().isEmpty()) {
            throw new NotFoundException("No product found in the cart");
        }
        return CartConverter.convertEntityToDto(cart);
    }

    @Override
    public void removeAllItem(Long userId) {
        User user = getUser(userId);
        Cart cart = user.getCart();
        // If no cart exists, create a new one and associate with the user
        if (cart == null || cart.getCartItemList().isEmpty()) {
            throw new NotFoundException("No product found in the cart");
        }
        cartRepository.delete(cart);

    }

    @Override
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("No cart found for the user"));
    }

    @Override
    public void saveCart(Cart cart){
        cartRepository.save(cart);
    }


    private User getUser(Long userId){
        return userService.getUserById(userId);
    }




}
