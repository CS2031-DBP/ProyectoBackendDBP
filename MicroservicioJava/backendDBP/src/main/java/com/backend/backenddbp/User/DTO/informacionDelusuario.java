package com.backend.backenddbp.User.DTO;

import com.backend.backenddbp.User.Domain.Rol;
import lombok.Data;

@Data
public class informacionDelusuario {
    private Long id;
    private String userName;
    private String fotoPerfil;
    private String email;
    private Rol rol;
}
