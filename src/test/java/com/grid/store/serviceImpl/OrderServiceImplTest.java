package com.grid.store.serviceImpl;

import com.grid.store.dto.OrderDto;
import com.grid.store.entity.*;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.mapper.OrderMapper;
import com.grid.store.repository.OrderRepository;
import com.grid.store.service.CartService;
import com.grid.store.service.ProductService;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private OrderItem orderItem;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setProductId(1L);
        product.setTitle("Test Product");
        product.setPrice(new BigDecimal("100"));
        product.setAvailable(10);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setCartId(1L);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItemList(cartItems);

        user = new User();
        user.setUserId(1L);
        user.setCart(cart);
        user.setOrdersList(new ArrayList<>());

        orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);

        order = new Order();
        order.setOrderId(1L);
        order.setOrderItemList(Collections.singletonList(orderItem));
        order.setTotalPrice(new BigDecimal("200"));
        order.setStatus(Status.SUCCESS);
        order.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));

        orderDto = new OrderDto();
        orderDto.setOrderId(1L);
        orderDto.setTotalPrice(new BigDecimal("200"));
        orderDto.setStatus(Status.SUCCESS);
        orderDto.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));

        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

    }

    @Test
    void testPlaceOrder_Success() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.checkProductStocks(product, 2)).thenReturn(true);

        doNothing().when(cartService).saveCart(cart);

        when(userService.saveUser(user)).thenReturn(user);

        OrderDto expectedOrderDto = new OrderDto();
        expectedOrderDto.setTotalPrice(new BigDecimal("200"));
        expectedOrderDto.setStatus(Status.SUCCESS);
        when(orderMapper.orderToOrderDto(any(Order.class))).thenReturn(expectedOrderDto);

        OrderDto resultOrderDto = orderService.placeOrder(1L);

        assertNotNull(resultOrderDto);
        assertEquals(new BigDecimal("200"), resultOrderDto.getTotalPrice());
        assertEquals(Status.SUCCESS, resultOrderDto.getStatus());

        verify(cartService).saveCart(cart);
        verify(productService).updateProductAvailability(1L, 2);
        verify(userService).saveUser(user);
    }




    @Test
    void testPlaceOrder_NoItemsInCart() {
        cart.setCartItemList(Collections.emptyList());
        when(userService.getUserById(1L)).thenReturn(user);
        when(cartService.getCart(1L)).thenReturn(cart);

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> orderService.placeOrder(1L)
        );

        assertEquals(Constants.PLEASE_ADD_ITEMS_TO_PLACE_ORDER, exception.getMessage());
        verify(productService, never()).updateProductAvailability(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.checkProductStocks(product, 2)).thenReturn(false);

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> orderService.placeOrder(1L)
        );

        assertEquals(
                "Insufficient stock for product ID 1. Available: 10, Requested: 2",
                exception.getMessage()
        );
        verify(productService, never()).updateProductAvailability(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        order.setStatus(Status.SUCCESS);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, 1L);

        assertEquals(Status.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
        verify(productService).updateProductAvailability(1L, -2);
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        order.setStatus(Status.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.cancelOrder(1L, 1L)
        );

        assertEquals("400 BAD_REQUEST \"Order has already been: CANCELLED\"", exception.getMessage());
        verify(productService, never()).updateProductAvailability(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> orderService.cancelOrder(1L, 1L)
        );

        assertEquals("No order found by this Id: 1", exception.getMessage());
        verify(productService, never()).updateProductAvailability(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetAllOrdersByUserId() {
        user.setOrdersList(Arrays.asList(order));
        when(userService.getUserById(1L)).thenReturn(user);

        List<OrderDto> orders = orderService.getAllOrdersByUserId(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("200"), orders.get(0).getTotalPrice());
        verify(userService).getUserById(1L);
    }
}
