package com.svetopolk.demo.rest;

import com.svetopolk.demo.dto.StateRequest;
import com.svetopolk.demo.dto.StateResponse;
import com.svetopolk.demo.service.StateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

@RestController
@RequiredArgsConstructor
@RequestMapping("services")
public class StateController {

    private final StateService stateService;

    @PostMapping(value = "status", produces = {MediaType.APPLICATION_JSON_VALUE})
    public StateResponse getState(@RequestBody @Valid StateRequest request) {
        return stateService.getState(request.userId(), TimeZone.getTimeZone(request.timezone()), request.cc());
    }
}
