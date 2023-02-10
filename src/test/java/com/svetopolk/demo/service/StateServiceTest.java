package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.domain.Status;
import com.svetopolk.demo.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.TimeZone;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class StateServiceTest {

    @Mock
    AdsService adsService;
    @Mock
    SupportService supportService;
    @Mock
    UserService userService;
    @Value("${user.advancedThreshold}")
    int advancedThreshold;

    private StateService stateService;

    private static final String userId = UUID.randomUUID().toString();
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Madrid");
    private static final String countryCode = "US";

    @BeforeEach
    public void before() {
        stateService = new StateService(adsService, supportService, userService, advancedThreshold);
    }


    @Test
    void getStateLowSkill() {
        when(adsService.getStatus(countryCode)).thenReturn(Status.DISABLED);
        when(supportService.getStatus()).thenReturn(Status.DISABLED);
        when(userService.increaseSkill(userId)).thenReturn(new User(userId, "Tom", 5));

        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.DISABLED, Status.DISABLED, Status.DISABLED)));
    }

    @Test
    void getStateHighSkill() {
        when(adsService.getStatus(countryCode)).thenReturn(Status.ENABLED);
        when(supportService.getStatus()).thenReturn(Status.ENABLED);
        when(userService.increaseSkill(userId)).thenReturn(new User(userId, "Tom", 6));

        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.ENABLED, Status.ENABLED, Status.ENABLED)));
    }

    @Test
    void getStateUserNotFound() {
        when(adsService.getStatus(countryCode)).thenReturn(Status.DISABLED);
        when(supportService.getStatus()).thenReturn(Status.DISABLED);
        when(userService.increaseSkill(userId)).thenThrow(new UserNotFoundException("user not found=123"));

        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.UNDEFINED, Status.DISABLED, Status.DISABLED)));
    }

}