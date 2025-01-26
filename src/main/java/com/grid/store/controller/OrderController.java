package com.grid.store.controller;

import com.grid.store.dto.OrderDto;
import com.grid.store.service.OrderService;
import com.grid.store.utilities.Constants;
import com.grid.store.utilities.Validator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public ResponseEntity<OrderDto> placeOrder(HttpSession httpSession){
        Long userId = Validator.getUserId(httpSession);
        OrderDto orderDto = orderService.placeOrder(userId);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(HttpSession httpSession,
                                              @RequestParam("orderId") Long orderId){
        Long userId = Validator.getUserId(httpSession);
        orderService.cancelOrder(userId, orderId);
        return new ResponseEntity<>(Constants.ORDER_CANCELLED, HttpStatus.OK);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderDto>> getAllOrdersByUserId(HttpSession httpSession){
        Long userId = Validator.getUserId(httpSession);
        List<OrderDto> orderDtoList = orderService.getAllOrdersByUserId(userId);
        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
    }
}
