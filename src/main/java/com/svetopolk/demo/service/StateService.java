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

    public StateResponse getState(String userId, TimeZone timezone, String countryCode) {
        log.info("getState({}, {}, {})", userId, timezone.getID(), countryCode);

        Status multiplayerStatus = Status.of(true);
        Status userSupportStatus = Status.of(true);
        Status adsStatus = Status.of(true);

        return new StateResponse(multiplayerStatus, userSupportStatus, adsStatus);
    }

}
