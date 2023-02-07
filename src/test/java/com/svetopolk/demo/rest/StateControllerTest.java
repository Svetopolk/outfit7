package com.svetopolk.demo.rest;

import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.service.StateService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StateControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @Test
    void getStateSuccess() throws Exception {
        var response2 = new StateResponse(Status.of(true), Status.of(true), Status.of(true));
        Mockito.when(stateService.getState(any(), any(), any())).thenReturn(response2);

        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "userId": "2e3b11b0-07a4-4873-8de5-d2ae2eab26b2",
                        "timezone": "Europe/Madrid",
                        "cc": "US"
                        }
                        """);

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"multiplayer":"enabled","user-support":"enabled","ads":"enabled"}
                        """));
    }

    @Test
    void getStateBadTimezone() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "userId": "2e3b11b0-07a4-4873-8de5-d2ae2eab26b2",
                        "timezone": "unknown TimeZone",
                        "cc": "US"
                        }
                        """);

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("""
                        {"timezone":"invalid field"}
                        """));
    }

    @Test
    void getStateMissedUserId() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "timezone": "Europe/Madrid",
                        "cc": "US"
                        }
                        """);

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {"userId":"mandatory field"}
                        """));
    }

    @Test
    void getStateMissedCountryCode() throws Exception {
        var response2 = new StateResponse(Status.of(true), Status.of(true), Status.of(true));
        Mockito.when(stateService.getState(any(), any(), any())).thenReturn(response2);

        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "userId": "2e3b11b0-07a4-4873-8de5-d2ae2eab26b2",
                        "timezone": "Europe/Madrid",
                        "cc": null
                        }
                        """);

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                         {"cc":"mandatory field"}
                        """));
    }

    @Test
    void getStateEmptyRequest() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {"cc":"mandatory field","timezone":"invalid field","userId":"mandatory field"}
                        """));
    }

    @Test
    void getStateNotJson() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/services/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not json");

        mockMvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {"body":"expect JSON"}
                        """));
    }

    @Test
    void getStateNoMediaType() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/services/status")
                .content("not json");

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

}