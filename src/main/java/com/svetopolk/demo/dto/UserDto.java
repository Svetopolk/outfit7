package com.svetopolk.demo.dto;

import com.svetopolk.demo.domain.User;

public record UserDto(String id, String name) {

    public static UserDto from(User user) {
        return new UserDto(user.id(), user.name());
    }
}
