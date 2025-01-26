package com.grid.store.serviceImpl;

import com.grid.store.dto.UserRequest;
import com.grid.store.entity.User;
import com.grid.store.exception.ConflictException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.exception.UnauthorizedException;
import com.grid.store.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private HttpSession httpSession;

    @Autowired
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setEmailId("test@example.com");
        userRequest.setPassword("password123");


        user = new User();
        user.setUserId(1L);
        user.setEmailId("test@example.com");
        user.setPassword("encodedPassword123");
    }

    @Test
    void testRegisterUser_Success() {

        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword123");

        when(userRepository.existsByEmailId(userRequest.getEmailId())).thenReturn(false);

        userService.registerUser(userRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_ThrowsConflictException() {

        when(userRepository.existsByEmailId(userRequest.getEmailId())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.registerUser(userRequest));
        assertEquals("Email ID is already registered.", exception.getMessage());
    }

    @Test
    void testLogIn_Success() {

        when(userRepository.findByEmailId(userRequest.getEmailId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(httpSession.getId()).thenReturn("mockSessionId");
        String sessionId = userService.logIn(userRequest, httpSession);

        verify(httpSession, times(1)).setAttribute("userId", user.getUserId());
        assertNotNull(sessionId);
    }

    @Test
    void testLogIn_InvalidPassword() {

        when(userRepository.findByEmailId(userRequest.getEmailId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.logIn(userRequest, httpSession));
        assertEquals("Invalid password.", exception.getMessage());
    }

    @Test
    void testLogIn_UserNotFound() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmailId("unknown@example.com");
        userRequest.setPassword("password123");

        when(userRepository.findByEmailId(userRequest.getEmailId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.logIn(userRequest, httpSession));
        assertEquals("No user found with email: unknown@example.com", exception.getMessage());
    }

    @Test
    void testGetUserById_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("No user found with UserId: 1", exception.getMessage());
    }
}
