package com.backend.backenddbp.User.DTO;

import dbp.connect.User.Domain.Rol;
import lombok.Data;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String pNombre;
    private String sNombre;
    private String pApellido;
    private String sApellido;
    private String email;
    private String fotoUrl;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String descripcion;
    private LocalDate fechaNacimiento;
    private String genero;
    private String pais;
    private Rol rol;
    private ZonedDateTime fechaCreacion;

}
