package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.User;
import com.svetopolk.demo.domain.Status;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.exception.UserNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

@Service
@Slf4j
public class StateService {

    private final AdsService adsService;
    private final SupportService supportService;
    private final UserService userService;
    private final int advancedThreshold;

    public StateService(
            AdsService adsService,
            SupportService supportService,
            UserService userService,
            @Value("${user.advancedThreshold}") int advancedThreshold
    ) {
        this.adsService = adsService;
        this.supportService = supportService;
        this.userService = userService;
        this.advancedThreshold = advancedThreshold;
    }

    public StateResponse getState(@NonNull String userId, @NonNull TimeZone timezone, @NonNull String countryCode) {

        Status multiplayerStatus;
        try {
            User user = userService.increaseSkill(userId);
            multiplayerStatus = Status.of(user.getSkill() > advancedThreshold);
        } catch (UserNotFoundException e) {
            log.error("user not found=" + userId);
            multiplayerStatus = Status.UNDEFINED;
        }
        Status userSupportStatus = supportService.getStatus();
        Status adsStatus = adsService.getStatus(countryCode);

        return new StateResponse(multiplayerStatus, userSupportStatus, adsStatus);
    }
}
