package com.svetopolk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svetopolk.demo.domain.Status;

public record StateResponse(
        @JsonProperty("multiplayer")
        Status multiplayer,
        @JsonProperty("user-support")
        Status userSupport,
        @JsonProperty("ads")
        Status ads
) {
}
