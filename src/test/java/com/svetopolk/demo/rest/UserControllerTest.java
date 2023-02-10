package com.svetopolk.demo.rest;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static final User user = new User("123456", "Janja", 3);

    @Test
    void getUser() throws Exception {
        Mockito.when(userService.getUser("123456")).thenReturn(user);

        mockMvc.perform(get("/api/users/123456"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":"123456","name":"Janja"}
                        """));
    }

    @Test
    void getUsers() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of(user, user));

        mockMvc.perform(get("/api/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":"123456","name":"Janja"},{"id":"123456","name":"Janja"}]
                         """));
    }

    @Test
    void deleteUser() throws Exception {
        Mockito.when(userService.deleteUser("successfulDeletedId")).thenReturn(1);
        Mockito.when(userService.deleteUser("alreadyDeletedId")).thenReturn(0);
        Mockito.when(userService.deleteUser("unexpectedBehavior")).thenReturn(2);

        mockMvc.perform(delete("/api/users/successfulDeletedId"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/users/alreadyDeletedId"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/users/unexpectedBehavior"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                        {"unexpected":"we are really sorry"}
                         """));

    }


}