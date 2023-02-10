package com.svetopolk.demo.repository;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * assumed for test purpose
 */

@Repository
public class MemUserRepository implements UserRepository {

    private final long limit;
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public MemUserRepository(@Value("${users.list.limit}") long limit) {
        this.limit = limit;
    }

    @Override
    public User create(String id, String name) {
        var user = new User(id, name, 0);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User save(User user) {
        return user;
    }

    @Override
    public User get(String id) {
        var user = userMap.get(id);
        if (user == null) {
            throw new UserNotFoundException("user not found=" + id);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return userMap.values().stream().limit(limit).toList();
    }

    @Override
    public int delete(String id) {
        var user = userMap.get(id);
        if (user == null) {
            return 0;
        }
        var key = userMap.remove(id);
        if (key == null) {
            return 0;
        }
        return 1;
    }
}
