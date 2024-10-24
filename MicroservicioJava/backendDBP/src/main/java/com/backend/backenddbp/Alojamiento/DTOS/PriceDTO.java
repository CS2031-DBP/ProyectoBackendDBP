package com.backend.backenddbp.Alojamiento.DTOS;

import com.backend.backenddbp.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
public class PriceDTO {
    @NotNull
    private double precio;
    @NotNull
    private TipoMoneda tipoMoneda;
}
