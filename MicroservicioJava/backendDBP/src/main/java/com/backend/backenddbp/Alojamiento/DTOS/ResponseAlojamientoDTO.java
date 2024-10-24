package com.backend.backenddbp.Alojamiento.DTOS;

import com.backend.backenddbp.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.backenddbp.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseAlojamientoDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long propietarioId;
    private double latitude;
    private double longitude;
    @NotNull
    private String ubicacion;
    @NotNull
    private String descripcion;
    @NotNull
    private TipoMoneda tipoMoneda;
    @NotNull
    private double precio;
    private int cantidadHabitaciones;
    private int cantidadCamas;
    private int cantidadBanios;
    private List<ResponseMultimediaDTO> multimedia = new ArrayList<>();
}
