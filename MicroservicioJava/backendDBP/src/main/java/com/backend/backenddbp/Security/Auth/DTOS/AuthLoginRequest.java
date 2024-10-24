package com.backend.backenddbp.Security.Auth.DTOS;

import lombok.Data;

@Data
public class AuthLoginRequest {
    private String email;
    private String password;
}
