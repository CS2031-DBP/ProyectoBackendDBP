package com.backend.backenddbp.Security.Utils;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ErrorDetail {
    private String error;
    private String message;
    private ZonedDateTime timestamp;
}
