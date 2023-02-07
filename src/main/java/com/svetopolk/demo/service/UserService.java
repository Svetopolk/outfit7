package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Getter //TODO
    private final long limit;
    private final UserRepository userRepository;

    public UserService(
            @Value("${users.list.limit}") long limit,
            UserRepository userRepository
    ) {
        this.limit = limit;
        this.userRepository = userRepository;
    }

    public User createUser(String userId, String name) {
        return userRepository.create(userId, name);
    }

    public User getUser(String userId) {
        return userRepository.get(userId);
    }

    public List<User> getUsers() {
        return userRepository.getAll().stream().limit(limit).toList();
    }

    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }

}
