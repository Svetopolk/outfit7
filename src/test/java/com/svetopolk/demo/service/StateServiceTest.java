package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.dto.StateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class StateServiceTest {

    @Autowired
    StateService stateService;

    @Test
    void getLastLocation() {
        StateResponse value = stateService.getState("", TimeZone.getTimeZone("aa"), "");
        assertThat(value, is(new StateResponse(Status.ENABLED, Status.ENABLED, Status.ENABLED)));
    }

}