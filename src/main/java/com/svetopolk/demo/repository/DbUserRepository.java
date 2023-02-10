package com.svetopolk.demo.repository;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.exception.UserNotFoundException;
import com.svetopolk.demo.repository.entity.UserEntity;
import com.svetopolk.demo.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class DbUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User create(String id, String name) {
        var user = new UserEntity(id, name, 0);
        var userEntity = jpaUserRepository.save(user);
        return userEntity.toUser();
    }

    @Override
    public User save(User user) {
        var userEntity = jpaUserRepository.save(UserEntity.of(user));
        return userEntity.toUser();
    }

    @Override
    public User get(String id) {
        var userEntity = jpaUserRepository.findById(id);
        return userEntity.orElseThrow(() -> new UserNotFoundException("user not found=" + id)).toUser();
    }

    @Override
    public List<User> getAll() {
        var userEntities = jpaUserRepository.findAll();
        return StreamSupport.stream(userEntities.spliterator(), false).map(UserEntity::toUser).toList();
    }

    @Override
    public int delete(String id) {
        try {
            jpaUserRepository.deleteById(id);
            return 1;
        } catch (EmptyResultDataAccessException e) {
            log.warn("user already deleted=" + id);
            return 0;
        }
    }
}
