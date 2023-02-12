package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.exception.UserNotFoundException;
import com.svetopolk.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final long getUsersLimit;
    private final UserRepository userRepository;

    public UserService(
            @Value("${users.list.limit}") long getUsersLimit,
            UserRepository userRepository
    ) {
        this.getUsersLimit = getUsersLimit;
        this.userRepository = userRepository;
    }

    public User createUser(String name) {
        var userId = UUID.randomUUID().toString();
        return createUser(userId, name);
    }

    public User createUser(String userId, String name) {
        return userRepository.create(userId, name);
    }

    public User getUser(String userId) {
        return userRepository.get(userId);
    }

    public List<User> getUsers() {
        return userRepository.getAll().stream().limit(getUsersLimit).toList();
    }

    public int deleteUser(String userId) {
        return userRepository.delete(userId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 15)
    public User increaseSkill(String userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("user not found=" + userId);
        }
        user.setSkill(user.getSkill() + 1);
        return userRepository.save(user);
    }
}
