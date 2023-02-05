package com.svetopolk.demo.dto;

import com.svetopolk.demo.validator.TimeZone;
import jakarta.validation.constraints.NotNull;

public record StateRequest(
        @NotNull(message = "mandatory field")
        String userId,
        @TimeZone
        String timezone,
        @NotNull(message = "mandatory field")
        String cc
) {
}
