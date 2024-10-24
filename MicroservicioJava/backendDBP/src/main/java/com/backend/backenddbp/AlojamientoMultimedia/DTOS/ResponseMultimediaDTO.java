package com.backend.backenddbp.AlojamientoMultimedia.DTOS;

import com.backend.backenddbp.Tipo;
import lombok.Data;

@Data
public class ResponseMultimediaDTO {
    private String id;
    private String url_contenido;
    private Tipo tipo;
}
