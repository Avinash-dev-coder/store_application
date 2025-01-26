package com.grid.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grid.store.dto.CartDto;
import com.grid.store.dto.CartItemDto;
import com.grid.store.dto.CartRequest;
import com.grid.store.service.CartService;
import com.grid.store.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;


    private Long userId;
    private CartRequest cartRequest;
    private CartDto cartDto;
    private MockHttpSession mockHttpSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        cartRequest = new CartRequest();
        cartRequest.setProductId(100L);
        cartRequest.setQuantity(2);
        cartDto = new CartDto();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductName("test product");
        cartItemDto.setQuantity(1);
        cartItemDto.setSubTotal(new BigDecimal("100.00"));
        cartDto.setCartItemDtoList(List.of(cartItemDto));
        cartDto.setTotalPrice(new BigDecimal("100.00"));
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", userId);
    }

    @Test
    public void testAddCartItem() throws Exception {

        when(cartService.addItem(eq(userId), any(CartRequest.class))).thenReturn(cartDto);
        mockMvc.perform(post("/cart/add-cart-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemDtoList[0].productName").value("test product"))
                .andExpect(jsonPath("$.cartItemDtoList[0].quantity").value(1))
                .andExpect(jsonPath("$.cartItemDtoList[0].subTotal").value(100.0))
                .andExpect(jsonPath("$.totalPrice").value(100.0));

    }

    @Test
    public void testRemoveCartItem() throws Exception {
        CartDto emptyCartDto = new CartDto();
        emptyCartDto.setCartItemDtoList(new ArrayList<>()); // Empty list
        emptyCartDto.setTotalPrice(BigDecimal.ZERO);

        when(cartService.removeItem(eq(userId), any(CartRequest.class))).thenReturn(emptyCartDto);

        mockMvc.perform(post("/cart/remove-cart-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemDtoList").isEmpty()) // List should be empty
                .andExpect(jsonPath("$.totalPrice").value(0.00));  // Total price should be 0.00
    }


    @Test
    public void testGetAllCartItems()throws Exception{
        when(cartService.getAllItem(eq(userId))).thenReturn(cartDto);

        mockMvc.perform(get("/cart/get-all-cart-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemDtoList[0].productName").value("test product"))
                .andExpect(jsonPath("$.cartItemDtoList[0].quantity").value(1))
                .andExpect(jsonPath("$.cartItemDtoList[0].subTotal").value(100.0))
                .andExpect(jsonPath("$.totalPrice").value(100.0));

    }

    @Test
    public void testRemoveAllCartItems() throws Exception {
        doNothing().when(cartService).removeAllItem(eq(userId));


        mockMvc.perform(delete("/cart/remove-all-cart-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Constants.ALL_ITEM_REMOVED));
    }



}
