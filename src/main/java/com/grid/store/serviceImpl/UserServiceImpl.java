package com.grid.store.serviceImpl;

import com.grid.store.dto.UserRequest;
import com.grid.store.entity.User;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.ConflictException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.exception.UnauthorizedException;
import com.grid.store.mapper.UserMapper;
import com.grid.store.repository.UserRepository;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Constants;
import com.grid.store.utilities.Validator;

import jakarta.servlet.http.HttpSession;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void registerUser(UserRequest userRequest) {
        //validate for user object
        validateUserDetails(userRequest);
        try {
            // Check if email id already exists
            checkForExistingUser(userRequest.getEmailId());

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            userRequest.setPassword(encodedPassword);
            User user = userMapper.userRequestToUser(userRequest);
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Constants.EMAIL_ID_IS_ALREADY_REGISTERED);
        }

    }


    @Override
    public String logIn(UserRequest userRequest, HttpSession httpSession) {
        //validate for user object
        validateUserDetails(userRequest);
        User loginUser = getUserByEmail(userRequest.getEmailId());
        validatePassword(userRequest.getPassword(), loginUser);

        httpSession.setAttribute("userId", loginUser.getUserId());
        return httpSession.getId();
    }


    private void validateUserDetails(UserRequest userRequest) {
        if (userRequest == null) {
            throw new BadRequestException(Constants.USER_CANNOT_BE_NULL);
        }
        if (Validator.isBlank(userRequest.getEmailId())) {
            throw new BadRequestException(Constants.EMAIL_ID_CANNOT_BE_BLANK);
        }
        if (Validator.isBlank(userRequest.getPassword())) {
            throw new BadRequestException(Constants.PASSWORD_CANNOT_BE_BLANK);
        }
    }

    private void checkForExistingUser(String emailId){
        if (userRepository.existsByEmailId(emailId)) {
            throw new ConflictException(Constants.EMAIL_ID_IS_ALREADY_REGISTERED);
        }
    }


    private User getUserByEmail(String emailId) {
        return userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new NotFoundException(Constants.NO_USER_FOUND_WITH_EMAIL + emailId));
    }

    @Override
    public  User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Constants.NO_USER_FOUND_WITH_USER_ID + userId));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }


    private void validatePassword(String enteredPassword, User storedUser) {
        boolean passwordMatches = passwordEncoder.matches(enteredPassword, storedUser.getPassword());
        if (!passwordMatches) {
            throw new UnauthorizedException(Constants.INVALID_PASSWORD);
        }
    }


}
