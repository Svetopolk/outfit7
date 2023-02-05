package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.dto.StateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.TimeZone;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class UserLocationServiceTest {

    @Autowired
    StateService stateService;

    @Test
    void getLastLocation() {

        var userId = UUID.randomUUID().toString();
        var timeZone = TimeZone.getTimeZone("Europe/Madrid");
        var countryCode = "US";
        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.ENABLED, Status.ENABLED, Status.ENABLED)));
    }


}