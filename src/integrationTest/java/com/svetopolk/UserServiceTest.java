package com.svetopolk;

import com.svetopolk.demo.Application;
import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("int")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        var userId = UUID.randomUUID().toString();
        var name = "Jure";
        var user = userService.createUser(userId, name);
        assertThat(user, is(new User(userId, name, 0)));
    }

    @Test
    void parallelCreateDeleteAddSkill() {
        //delete from previous run just in case
        userService.getUsers().stream()
                .filter(x -> x.getName().equals("Maja") || x.getName().equals("Nina"))
                .forEach(x -> userService.deleteUser(x.getId()));

        IntStream.range(0, 100).forEach(x -> userService.createUser("Maja"));
        var users = userService.getUsers();
        users = users.stream().filter(x -> x.getName().equals("Maja")).toList();
        assertThat(users, hasSize(100));

        users.stream().parallel().forEach(x -> {
            userService.createUser("Nina");
            userService.deleteUser(x.getId());
        });
        users = userService.getUsers();

        assertThat(users.stream().filter(x -> x.getName().equals("Maja")).count(), is(0L));
        assertThat(users.stream().filter(x -> x.getName().equals("Nina")).count(), is(100L));

        users.stream()
                .filter(x -> x.getName().equals("Nina"))
                .parallel()
                .forEach(x -> userService.increaseSkill(x.getId()));

        users = userService.getUsers().stream().filter(x -> x.getName().equals("Nina")).toList();
        assertThat(users, hasSize(100));
        assertThat(users.stream().filter(x -> x.getSkill() == 1).count(), is(100L));

        userService.getUsers().stream()
                .filter(x -> x.getName().equals("Nina"))
                .forEach(x -> userService.deleteUser(x.getId()));
    }

    @Test
    void concurrentIncreaseSkill() {
        var user = userService.createUser("Barbara");
        assertThat(user.getSkill(), is(0));
        IntStream.range(0, 20).parallel().forEach(x -> userService.increaseSkill(user.getId()));
        var finalUser = userService.getUser(user.getId());
        assertThat(finalUser.getSkill(), is(20));
    }

}
