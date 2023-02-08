package com.svetopolk.demo.repository;

import com.svetopolk.demo.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * not thread safe
 * for test purpose only
 */

@Repository
public class MemUserRepository implements UserRepository {

    private final long limit;
    private final Map<String, User> users = new HashMap<>();

    public MemUserRepository(@Value("${users.list.limit}") long limit) {
        this.limit = limit;
    }

    @Override
    public User create(String id, String name) {
        var user = new User(id, name, 0);
        users.put(id, user);
        return user;
    }

    @Override
    public User get(String id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().limit(limit).toList();
    }

    @Override
    public int delete(String id) {
        return 0;
    }
}
