package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import com.svetopolk.demo.dto.StateResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
        log.info("getState({}, {}, {})", userId, timezone.getID(), countryCode);

        var user = userService.increaseSkill(userId);
        Status multiplayerStatus = Status.of(user.getSkill() >= advancedThreshold);
        Status userSupportStatus = supportService.getStatus();
        Status adsStatus = adsService.execute(countryCode);

        return new StateResponse(multiplayerStatus, userSupportStatus, adsStatus);
    }
}
