package com.backend.backenddbp.Security.Auth.DTOS;

import jakarta.persistence.Lob;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AuthRegisterRequest {
    private String userName;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private Integer edad;
    private String email;
    private String password;
    private String role;
    private String genero;
    private String ciudad;
    private String pais;
   private String telefono;
}
