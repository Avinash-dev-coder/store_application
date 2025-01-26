package com.grid.store.serviceImpl;

import com.grid.store.dto.UserRequest;
import com.grid.store.entity.User;
import com.grid.store.exception.BadRequestException;
import com.grid.store.exception.ConflictException;
import com.grid.store.exception.NotFoundException;
import com.grid.store.exception.UnauthorizedException;
import com.grid.store.repository.UserRepository;
import com.grid.store.service.UserService;
import com.grid.store.utilities.Validator;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(UserRequest userRequest) {
        //validate for user object
        validateUserDetails(userRequest);
        //check if email id already exists
        checkForExistingUser(userRequest.getEmailId());

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);
        User user =  convertDtoToEntity(userRequest);
        userRepository.save(user);
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
            throw new BadRequestException("User cannot be null.");
        }
        if (Validator.isBlank(userRequest.getEmailId())) {
            throw new BadRequestException("Email ID cannot be blank.");
        }
        if (Validator.isBlank(userRequest.getPassword())) {
            throw new BadRequestException("Password cannot be blank.");
        }
    }

    private void checkForExistingUser(String emailId){
        if (userRepository.existsByEmailId(emailId)) {
            throw new ConflictException("Email ID is already registered.");
        }
    }


    private User getUserByEmail(String emailId) {
        return userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new NotFoundException("No user found with email: " + emailId));
    }

    @Override
    public  User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("No user found with UserId: " + userId));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }


    private void validatePassword(String enteredPassword, User storedUser) {
        boolean passwordMatches = passwordEncoder.matches(enteredPassword, storedUser.getPassword());
        if (!passwordMatches) {
            throw new UnauthorizedException("Invalid password.");
        }
    }

    private User convertDtoToEntity(UserRequest userRequest){
        User user = new User();
        user.setEmailId(userRequest.getEmailId());
        user.setPassword(userRequest.getPassword());
        return user;
    }

    private UserRequest convertEntityToDto(User user){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmailId(user.getEmailId());
        return userRequest;
    }



}
