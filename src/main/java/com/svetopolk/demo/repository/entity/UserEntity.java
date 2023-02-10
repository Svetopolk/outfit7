package com.svetopolk.demo.repository.entity;

import com.svetopolk.demo.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @NotNull
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "skill")
    private int skill;

    public User toUser() {
        return new User(id, name, skill);
    }

    public static UserEntity of(User user) {
        return new UserEntity(user.getId(), user.getName(), user.getSkill());
    }
}
