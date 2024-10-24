package com.backend.backenddbp.User.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserDTO {
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
}
