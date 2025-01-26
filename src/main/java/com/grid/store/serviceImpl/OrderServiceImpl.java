package com.grid.store.serviceImpl;

import com.grid.store.dto.OrderDto;
import com.grid.store.entity.*;
import com.grid.store.exception.*;
import com.grid.store.repository.*;
import com.grid.store.service.CartService;
import com.grid.store.service.OrderService;
import com.grid.store.service.ProductService;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;


    @Override
    public OrderDto placeOrder(Long userId) {
        User user = getUser(userId);

        Cart cart = cartService.getCart(user.getCart().getCartId());

        if (cart.getCartItemList() == null || cart.getCartItemList().isEmpty()) {
            throw new NotFoundException(Constants.PLEASE_ADD_ITEMS_TO_PLACE_ORDER);
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        cart.getCartItemList().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            if (!productService.checkProductStocks(cartItem.getProduct(), cartItem.getQuantity())) {
                Product product = cartItem.getProduct();
                throw new BadRequestException("Insufficient stock for product ID " +
                        product.getProductId() + ". Available: " + product.getAvailable() +
                        ", Requested: " + cartItem.getQuantity());
            }
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemList.add(orderItem);
        });

//        // Convert CartItems into OrderItems
//        List<OrderItem> orderItemList = cart.getCartItemList().stream()
//                .map(cartItem -> {
//                    OrderItem orderItem = new OrderItem();
//                    if(!productService.checkProductStocks(cartItem.getProduct(), cartItem.getQuantity())){
//                        Product product = cartItem.getProduct();
//                        throw new BadRequestException(Constants.INSUFFICIENT_STOCK_FOR_PRODUCT_ID +
//                                product.getProductId() + ". Available: " + product.getAvailable() +
//                                ", Requested: " + cartItem.getQuantity());
//                    }
//                    orderItem.setProduct(cartItem.getProduct());
//                    orderItem.setQuantity(cartItem.getQuantity());
//                    return orderItem;
//                })
//                .collect(Collectors.toList());


        Order order = new Order();
        order.setOrderItemList(orderItemList);
        order.setTotalPrice(calculateTotalPrice(orderItemList));
        order.setStatus(Status.SUCCESS);
        order.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
        updateProductQuantity(order.getOrderItemList(), 1);
        user.getOrdersList().add(order);
        userService.saveUser(user);
        cart.getCartItemList().clear();
        cartService.saveCart(cart);

        return convertEntityToDto(order);
    }

    //to calculate the total price of the order
    private BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new NotFoundException(Constants.NO_ORDER_FOUND_BY_THIS_ID + orderId));
        if(order.getStatus().equals(Status.SUCCESS)){
            // Proceed to cancel the order
            order.setStatus(Status.CANCELLED);  // Change the order status to CANCELLED
            // re-updating  stocks
            updateProductQuantity(order.getOrderItemList(), -1);
            // Save the updated order
            orderRepository.save(order);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.ORDER_HAS_ALREADY_BEEN + order.getStatus());
        }
    }

    private void updateProductQuantity(List<OrderItem> orderItemList, int addOrRemove){
        orderItemList.forEach(orderItem -> {
            productService.updateProductAvailability(orderItem.getProduct().getProductId(),addOrRemove * orderItem.getQuantity());
        });
    }
    @Override
    public List<OrderDto> getAllOrdersByUserId(Long userId) {
        User user = getUser(userId);
        return user.getOrdersList().stream().map(this :: convertEntityToDto).toList();

    }

    private OrderDto convertEntityToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setCreateTimestamp(order.getCreateTimestamp());
        return orderDto;
    }

    private User getUser(Long userId){
        return userService.getUserById(userId);
    }

}
