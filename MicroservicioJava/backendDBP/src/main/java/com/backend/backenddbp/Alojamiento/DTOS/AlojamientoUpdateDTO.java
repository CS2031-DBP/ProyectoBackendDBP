package com.backend.backenddbp.Alojamiento.DTOS;

import com.backend.backenddbp.Alojamiento.Domain.Estado;
import com.backend.backenddbp.Tipo;
import com.backend.backenddbp.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;


@Data
public class AlojamientoUpdateDTO {
    @NotNull
    private Long propietarioId;
    private Double latitude;
    private Double longitude;
    @NotNull
    private String ubicacion;
    @NotNull
    @Size(min = 1, max = 255)
    private String descripcion;
    @NotNull
    private double precio;
    @NotNull
    private Estado estado;
    @NotNull
    private TipoMoneda tipoMoneda;
    private List<MultipartFile> multimedia = new ArrayList<>();
}
