package com.svetopolk.demo.repository;

import com.svetopolk.demo.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * assumed for test purpose
 */

@Repository
public class MemUserRepository implements UserRepository {

    private final long limit;
    private final Map<String, User> userMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public MemUserRepository(@Value("${users.list.limit}") long limit) {
        this.limit = limit;
    }

    @Override
    public User create(String id, String name) {
        var user = new User(id, name, 0);
        lock.lock();
        userMap.put(id, user);
        lock.unlock();
        return user;
    }

    @Override
    public User save(User user) {
        return user;
    }

    @Override
    public User get(String id) {
        lock.lock();
        var user = userMap.get(id);
        lock.unlock();
        return user;
    }

    @Override
    public List<User> getAll() {
        lock.lock();
        var users = userMap.values().stream().limit(limit).toList();
        lock.unlock();
        return users;
    }

    @Override
    public int delete(String id) {
        lock.lock();
        var user = userMap.get(id);
        if (user == null) {
            return 0;
        }
        userMap.remove(id);
        lock.unlock();
        return 1;
    }
}
