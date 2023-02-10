package com.svetopolk.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private int skill;

    public static User defaultUser(@NonNull String userId) {
        return new User (userId, "unknown", 0);
    }
}
