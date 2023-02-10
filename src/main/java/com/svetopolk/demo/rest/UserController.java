package com.svetopolk.demo.rest;

import com.svetopolk.demo.dto.UserDto;
import com.svetopolk.demo.exception.LogicException;
import com.svetopolk.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<UserDto> getUsers() {
        return userService.getUsers().stream().map(UserDto::from).toList();
    }

    @GetMapping(value = "users/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserDto getUser(@PathVariable String id) {
        return UserDto.from(userService.getUser(id));
    }

    @DeleteMapping(value = "users/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        int deleted = userService.deleteUser(id);
        if (deleted == 1) {
            return ResponseEntity.ok().build();
        } else if (deleted == 0) {
            return ResponseEntity.noContent().build();
        }
        throw new LogicException("deleteUser method deleted more then one record: " + deleted);
    }
}
