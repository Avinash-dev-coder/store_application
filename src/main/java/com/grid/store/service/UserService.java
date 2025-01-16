package com.grid.store.service;

import com.grid.store.dto.UserRequest;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    void registerUser(UserRequest userRequest);


    String logIn(UserRequest userRequest, HttpSession httpSession);
}
