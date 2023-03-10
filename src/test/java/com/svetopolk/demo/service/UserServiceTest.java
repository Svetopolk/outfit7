package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.exception.UserNotFoundException;
import com.svetopolk.demo.repository.MemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

class UserServiceTest {

    UserService userService;

    @BeforeEach
    void before() {
        userService = new UserService(3, new MemUserRepository(5));
    }

    @Test
    void createUser() {
        var userId = UUID.randomUUID().toString();
        var name = "Jure";
        var user = userService.createUser(userId, name);
        assertThat(user, is(new User(userId, name, 0)));
    }

    @Test
    void increaseUserSkill() {
        var user = userService.createUser("Jure");
        assertThat(user.getSkill(), is(0));
        user = userService.increaseSkill(user.getId());
        assertThat(user.getSkill(), is(1));
    }

    @Test
    void getUsers() {
        var user1 = userService.createUser("Jure");
        var user2 = userService.createUser("Maria");

        var users = userService.getUsers();

        assertThat(users, hasSize(2));
        assertThat(users, containsInAnyOrder(user1, user2));
    }

    @Test
    void checkGetUsersLimit() {
        IntStream.range(0, 10).forEach(x -> userService.createUser("Jure"));
        var users = userService.getUsers();
        assertThat(users, hasSize(3));
    }

    @Test
    void deleteUser() {
        var user = userService.createUser("Jure");
        assertThat(userService.getUsers(), hasSize(1));
        userService.deleteUser(user.getId());
        assertThat(userService.getUsers(), hasSize(0));
    }

    @Test
    void getUser() {
        var userId = UUID.randomUUID().toString();
        var name = "Jure";
        userService.createUser(userId, name);
        var user = userService.getUser(userId);
        assertThat(user, is(new User(userId, name, 0)));
    }

    @Test
    void getUserNotFound() {
        var userId = UUID.randomUUID().toString();
        var name = "Jure";
        var e = assertThrows(UserNotFoundException.class, () -> userService.getUser("1234"));
        assertThat(e.getLocalizedMessage(), is("user not found=1234"));
    }

    @Test
    void concurrentAccess() {
        userService = new UserService(2000, new MemUserRepository(2000));
        IntStream.range(0, 1000).parallel().forEach(x -> userService.createUser("Jure"));
        var users = userService.getUsers();
        assertThat(users, hasSize(1000));

        users.stream().parallel().forEach(x -> {
            userService.createUser("Maria");
            userService.deleteUser(x.getId());
        });
        users = userService.getUsers();

        assertThat(users, hasSize(1000));
        assertThat(users.stream().filter(x -> x.getName().equals("Maria")).count(), is(1000L));

        users.stream().parallel().forEach(x -> userService.increaseSkill(x.getId()));
        users = userService.getUsers();

        assertThat(users, hasSize(1000));
        assertThat(users.stream().filter(x -> x.getSkill() == 1).count(), is(1000L));

        users.stream().parallel().forEach(x -> userService.deleteUser(x.getId()));
        users = userService.getUsers();
        assertThat(users, hasSize(0));
    }
}
