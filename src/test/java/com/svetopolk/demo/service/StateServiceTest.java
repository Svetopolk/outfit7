package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.dto.Status;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.TimeZone;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("test")
class StateServiceTest {

    @Mock
    AdsService adsService;
    @Mock
    SupportService supportService;
    @Mock
    UserService userService;

    @InjectMocks
    StateService stateService;

    private static final String userId = UUID.randomUUID().toString();
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Madrid");
    private static final String countryCode = "US";

    @Test
    void getStateLowSkill() {
        Mockito.when(adsService.execute(countryCode)).thenReturn(Status.DISABLED);
        Mockito.when(supportService.getStatus()).thenReturn(Status.DISABLED);
        Mockito.when(userService.getUser(userId)).thenReturn(new User(userId, "Tom", 4));

        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.DISABLED, Status.DISABLED, Status.DISABLED)));
    }

    @Test
    void getStateHighSkill() {
        Mockito.when(adsService.execute(countryCode)).thenReturn(Status.ENABLED);
        Mockito.when(supportService.getStatus()).thenReturn(Status.ENABLED);
        Mockito.when(userService.getUser(userId)).thenReturn(new User(userId, "Tom", 5));

        var userState = stateService.getState(userId, timeZone, countryCode);

        assertThat(userState, is(new StateResponse(Status.ENABLED, Status.ENABLED, Status.ENABLED)));
    }

}