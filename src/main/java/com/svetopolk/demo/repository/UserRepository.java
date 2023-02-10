package com.svetopolk.demo.repository;

import com.svetopolk.demo.domain.User;

import java.util.List;

public interface UserRepository {
    User create (String id, String name);
    User save (User user);
    User get (String id);
    List<User> getAll ();
    int delete (String id);
}
