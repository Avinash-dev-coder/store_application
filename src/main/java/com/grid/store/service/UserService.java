package com.grid.store.service;

import com.grid.store.dto.UserRequest;
import com.grid.store.entity.User;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    void registerUser(UserRequest userRequest);

    User getUserById(Long userId);

    User saveUser(User user);

    String logIn(UserRequest userRequest, HttpSession httpSession);
}
