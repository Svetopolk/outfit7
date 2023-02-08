package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void createUser() {
        var userId = UUID.randomUUID().toString();
        var name = "Petr";
        var user = userService.createUser(userId, name);

        assertThat(user, is(new User(userId, name, 0)));
    }


}