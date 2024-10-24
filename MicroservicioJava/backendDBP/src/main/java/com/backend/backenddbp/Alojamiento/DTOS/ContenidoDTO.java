package com.backend.backenddbp.Alojamiento.DTOS;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class ContenidoDTO {
    @NotNull
    @Size(min = 1, max = 255)
    private String descripcion;
}
