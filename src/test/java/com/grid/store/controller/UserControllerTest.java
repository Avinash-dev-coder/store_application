package com.grid.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grid.store.dto.UserRequest;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Constants;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequest = new UserRequest();
        userRequest.setEmailId("testUser@gmail.com");
        userRequest.setPassword("password123");
    }

    @Test
    public void testRegisterUser() throws Exception {
        doNothing().when(userService).registerUser(userRequest);


        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())  // HTTP 201 Created
                .andExpect(content().string(Constants.USER_CREATED));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String mockedSessionId = "session123";
        MockHttpSession mockSession = new MockHttpSession();
        when(userService.logIn(any(UserRequest.class), any(HttpSession.class))).thenReturn(mockedSessionId);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.SESSION_CREATED + mockedSessionId));
    }

    @Test
    public void testLoginFailure() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();
        when(userService.logIn(any(UserRequest.class), any(HttpSession.class))).thenReturn(null);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .session(mockSession))
                .andExpect(status().isUnauthorized())  // HTTP 401 Unauthorized
                .andExpect(content().string(Constants.INVALID_CREDENTIALS));
    }
}
