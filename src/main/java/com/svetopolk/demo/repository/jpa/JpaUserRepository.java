package com.svetopolk.demo.repository.jpa;

import com.svetopolk.demo.repository.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends CrudRepository<UserEntity, String> {

}

