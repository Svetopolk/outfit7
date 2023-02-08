package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.dto.StateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class StateService {

    AdsService adsService;
    SupportService supportService;
    UserService userService;

    public StateResponse getState(String userId, TimeZone timezone, String countryCode) {
        log.info("getState({}, {}, {})", userId, timezone.getID(), countryCode);

        var user = userService.getUser(userId);
        Status multiplayerStatus = Status.of(user.skill() >= 5);
        Status userSupportStatus = supportService.getStatus();
        Status adsStatus = adsService.execute(countryCode);

        return new StateResponse(multiplayerStatus, userSupportStatus, adsStatus);
    }
}
