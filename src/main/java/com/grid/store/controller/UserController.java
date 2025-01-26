package com.grid.store.controller;

import com.grid.store.dto.UserRequest;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Constants;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
        userService.registerUser(userRequest);
        return new ResponseEntity<>(Constants.USER_CREATED, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody UserRequest userRequest, HttpSession session) {
        String sessionId = userService.logIn(userRequest, session);
        if (sessionId != null) {
            return new ResponseEntity<>(Constants.SESSION_CREATED + sessionId, HttpStatus.OK);
        }
        return new ResponseEntity<>(Constants.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
}
