package com.grid.store.serviceImpl;

import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartItemDto;
import com.grid.store.dto.CartRequest;
import com.grid.store.entity.*;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.mapper.CartMapper;
import com.grid.store.repository.CartRepository;
import com.grid.store.service.ProductService;
import com.grid.store.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private CartDto cartDto;
    private Product product;
    private CartItem cartItem;
    private CartItemDto cartItemDto;
    private CartRequest cartRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setProductId(1L);
        product.setTitle("Test Product");
        product.setAvailable(10);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cartItemDto = new CartItemDto();
        cartItemDto.setProductName(product.getTitle());
        cartItemDto.setQuantity(2);


        cart = new Cart();
        cart.setCartId(1L);
        cart.setCartItemList(new ArrayList<>(List.of(cartItem)));

        cartDto = new CartDto();
        cartDto.setCartItemDtoList(new ArrayList<>(List.of(cartItemDto)));


        user = new User();
        user.setUserId(1L);


        cartRequest = new CartRequest();
        cartRequest.setProductId(1L);
        cartRequest.setQuantity(3);

//        when(cartMapper.cartToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartToCartDto(any(Cart.class))).thenReturn(cartDto);


    }

    @Test
    void testAddItem_Success_NewItem() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(1L)).thenReturn(product);
        when(productService.checkProductStocks(product, 3)).thenReturn(true);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        when(userService.saveUser(user)).thenReturn(user);

        CartDto expectedCartDto = cartService.addItem(1L, cartRequest);

        assertNotNull(expectedCartDto);

        verify(userService).saveUser(user);
    }


    @Test
    void testAddItem_Success_UpdateExistingItem() {
        user.setCart(cart);
        cartRequest.setQuantity(5);
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(1L)).thenReturn(product);
        when(productService.checkProductStocks(product, 5)).thenReturn(true);
        when(userService.saveUser(user)).thenReturn(user);

        CartDto expectedCartDto = cartService.addItem(1L, cartRequest);

        assertNotNull(expectedCartDto);
    }

    @Test
    void testAddItem_Fail_InsufficientStock() {
        user.setCart(cart);;
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(1L)).thenReturn(product);
        when(productService.checkProductStocks(product, 3)).thenReturn(false);

        Exception exception = assertThrows(BadRequestException.class, () -> cartService.addItem(1L, cartRequest));
        assertEquals("Insufficient stock for product ID 1. Available: 10, Requested: 3", exception.getMessage());
    }

    @Test
    void testRemoveItem_Success_DecreaseQuantity() {
        user.setCart(cart);
        cartRequest.setQuantity(1);
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(1L)).thenReturn(product);
        when(cartRepository.save(cart)).thenReturn(cart);
        CartDto cartDto = cartService.removeItem(1L, cartRequest);

        assertNotNull(cartDto);
        assertEquals(1, cart.getCartItemList().get(0).getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void testRemoveItem_Success_RemoveItem() {
        user.setCart(cart);
        cartRequest.setQuantity(2);
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(1L)).thenReturn(product);
        when(cartRepository.save(cart)).thenReturn(cart);
        CartDto cartDto = cartService.removeItem(1L, cartRequest);

        assertNotNull(cartDto);
        assertTrue(cart.getCartItemList().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void testRemoveItem_Fail_ProductNotFoundInCart() {
        user.setCart(cart);
        cartRequest.setProductId(2L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProduct(2L)).thenReturn(null);

        Exception exception = assertThrows(NotFoundException.class, () -> cartService.removeItem(1L, cartRequest));
        assertEquals("No Product found with ID: 2 in the cart", exception.getMessage());
    }

    @Test
    void testGetAllItem_Success() {
        user.setCart(cart);
        when(userService.getUserById(1L)).thenReturn(user);

        CartDto cartDto = cartService.getAllItem(1L);

        assertNotNull(cartDto);
        verify(userService).getUserById(1L);
    }

    @Test
    void testGetAllItem_Fail_EmptyCart() {
        cart.getCartItemList().clear();
        when(userService.getUserById(1L)).thenReturn(user);

        Exception exception = assertThrows(NotFoundException.class, () -> cartService.getAllItem(1L));
        assertEquals("No product found in the cart", exception.getMessage());
    }

    @Test
    void testRemoveAllItem_Success() {
        user.setCart(cart);
        when(userService.getUserById(1L)).thenReturn(user);

        cartService.removeAllItem(1L);

        verify(cartRepository).delete(cart);
    }

    @Test
    void testRemoveAllItem_Fail_EmptyCart() {
        cart.getCartItemList().clear();
        when(userService.getUserById(1L)).thenReturn(user);

        Exception exception = assertThrows(NotFoundException.class, () -> cartService.removeAllItem(1L));
        assertEquals("No product found in the cart", exception.getMessage());
    }

    @Test
    void testGetCart_Success() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Cart retrievedCart = cartService.getCart(1L);

        assertNotNull(retrievedCart);
        assertEquals(1L, retrievedCart.getCartId());
    }

    @Test
    void testGetCart_Fail_CartNotFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> cartService.getCart(1L));
        assertEquals("No cart found for the user", exception.getMessage());
    }
}
