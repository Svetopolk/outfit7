package com.svetopolk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("enabled")
    ENABLED,
    @JsonProperty("disabled")
    DISABLED,
    @JsonProperty("undefined")
    UNDEFINED;

    public static Status of(boolean value) {
        if (value) {
            return ENABLED;
        } else {
            return DISABLED;
        }
    }
}
