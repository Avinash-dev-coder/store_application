package com.grid.store.mapper;

import com.grid.store.dto.UserRequest;
import com.grid.store.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRequest userToUserRequest(User user);

    User userRequestToUser(UserRequest userRequest);
}
