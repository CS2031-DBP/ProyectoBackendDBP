package com.backend.backenddbp.User.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSearchDTO {
    @NotNull
    private Long id;
    @NotNull
    private String fullName;
    @NotNull
    private String fotoUrl;
    @NotNull
    private String username;
}
