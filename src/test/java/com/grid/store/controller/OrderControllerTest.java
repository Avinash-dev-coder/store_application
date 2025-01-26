package com.grid.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grid.store.dto.OrderDto;
import com.grid.store.entity.Status;
import com.grid.store.service.OrderService;
import com.grid.store.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private MockHttpSession mockHttpSession;
    private Long userId;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        orderDto = new OrderDto();
        orderDto.setOrderId(11L);
        orderDto.setTotalPrice(new BigDecimal("100.00"));
        orderDto.setStatus(Status.SUCCESS);
        MockitoAnnotations.openMocks(this);
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", 1L);
    }


    @Test
    public void testPlaceOrder() throws Exception {
        when(orderService.placeOrder(userId)).thenReturn(orderDto);

        mockMvc.perform(post("/order/place-order")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(11L))
                .andExpect(jsonPath("$.totalPrice").value(100))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testCancelOrder() throws Exception {
        Long orderId = 11L;
        doNothing().when(orderService).cancelOrder(userId, orderId);

        mockMvc.perform(post("/order/cancel-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderId))
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.ORDER_CANCELLED));
    }

    @Test
    public void testGetAllOrderByUserId() throws Exception {
        when(orderService.getAllOrdersByUserId(userId)).thenReturn(List.of(orderDto));

        mockMvc.perform(get("/order/get-all-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(11L))
                .andExpect(jsonPath("$[0].totalPrice").value(100))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));

    }


}