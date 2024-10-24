package com.backend.backenddbp.Alojamiento.DTOS;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class UbicacionDTO {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private String ubicacion;
}
