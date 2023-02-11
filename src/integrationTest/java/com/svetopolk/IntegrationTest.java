package com.svetopolk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svetopolk.demo.Application;
import com.svetopolk.demo.dto.StateRequest;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static com.svetopolk.demo.domain.Status.DISABLED;
import static com.svetopolk.demo.domain.Status.ENABLED;
import static com.svetopolk.demo.domain.Status.UNDEFINED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("int")
public class IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void test() {
        var user = userService.createUser("Tom");
        assertThat(user.getName(), is("Tom"));
    }

    @Test
    void getUser() throws Exception {
        var user = userService.createUser("Tom");
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"" + user.getId() + "\",\"name\":\"Tom\"}"));
    }

    @Test
    void getUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/1234"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {"user":"user not found"}
                        """));
    }

    @Test
    void getUsers() throws Exception {
        userService.createUser("Mia");
        userService.createUser("Lara");
        mockMvc.perform(get("/api/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mia")))
                .andExpect(content().string(containsString("Lara")));
    }

    @Test
    void deleteUser() throws Exception {
        var user = userService.createUser("Mia");
        mockMvc.perform(get("/api/users/" + user.getId())).andExpect(status().isOk());
        mockMvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/api/users/" + user.getId())).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isNoContent());
    }

    @Test
    void getState() throws Exception {
        var user = userService.createUser("Ema");
        var stateRequest = new StateRequest(user.getId(), "Europe/Madrid", "UK");
        var content = mockMvc.perform(post("/admin/services/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(stateRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var response = mapper.readValue(content, StateResponse.class);
        assertThat(response.multiplayer(), is(DISABLED));
        assertThat(response.userSupport(), anyOf(is(ENABLED), is(DISABLED)));
        assertThat(response.ads(), anyOf(is(ENABLED), is(DISABLED), is(UNDEFINED)));
    }

    @Test
    void getStateManyTimes() {
        var user = userService.createUser("Luka");
        var stateRequest = new StateRequest(user.getId(), "Europe/Madrid", "US");

        List<StateResponse> responses = new CopyOnWriteArrayList<>();
        IntStream.range(0, 7).forEach(x -> {
            try {
                var content = mockMvc.perform(post("/admin/services/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(stateRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
                responses.add(mapper.readValue(content, StateResponse.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertThat(responses.stream().filter(x -> x.multiplayer() == ENABLED).count(), is(2L));
        assertThat(responses.stream().filter(x -> x.ads() == ENABLED).count(), greaterThanOrEqualTo(1L));
        assertThat(responses.stream().filter(x -> x.userSupport() == ENABLED).count(), anyOf(is(0L), is(7L)));
    }
}
