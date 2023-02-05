package com.svetopolk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StateResponse(
        @JsonProperty("multiplayer")
        Status multiplayer,
        @JsonProperty("user-support")
        Status userSupport,
        @JsonProperty("ads")
        Status ads
) {
}
