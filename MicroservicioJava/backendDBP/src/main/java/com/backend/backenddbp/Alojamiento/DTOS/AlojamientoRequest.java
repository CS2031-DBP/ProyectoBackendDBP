package com.backend.backenddbp.Alojamiento.DTOS;

import com.backend.backenddbp.TipoMoneda;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Data
public class AlojamientoRequest {
    @NotNull
    private Long propietarioId;
    private Double latitude;
    private Double longitude;
    private String ubicacion;

    @Size(min = 1, max = 255)
    private String descripcion;
    @NotNull
    private double precio;
    @NotNull
    private TipoMoneda tipoMoneda;
    private int cantidadHabitaciones;
    private int cantidadCamas;
    private int cantidadBanios;
}
