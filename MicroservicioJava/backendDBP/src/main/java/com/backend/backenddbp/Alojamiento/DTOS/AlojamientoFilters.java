package com.backend.backenddbp.Alojamiento.DTOS;

import com.backend.backenddbp.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlojamientoFilters {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private Double MaxDistance;
    @NotNull
    private Double MaxPrecio;
    @NotNull
    private Double MinPrecio;
    @NotNull
    private TipoMoneda tipoMoneda;
}
