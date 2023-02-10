package com.svetopolk.demo.dto;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.exception.UserNotFoundException;

public record UserDto(String id, String name) {

    public static UserDto from(User user) {
        if (user==null) {
            throw new UserNotFoundException("user not found");
        }
        return new UserDto(user.getId(), user.getName());
    }
}
