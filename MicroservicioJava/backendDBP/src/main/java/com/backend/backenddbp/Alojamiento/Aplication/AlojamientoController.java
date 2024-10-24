package com.backend.backenddbp.Alojamiento.Aplication;


import com.backend.backenddbp.Alojamiento.DTOS.*;
import com.backend.backenddbp.Alojamiento.Domain.AlojamientoServicio;
import com.backend.backenddbp.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.backenddbp.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.backenddbp.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import com.backend.backenddbp.TipoMoneda;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/alojamiento")
public class AlojamientoController {
    @Autowired
    AlojamientoServicio alojamientoServicio;
    @Autowired
    AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseAlojamientoDTO> crearAlojamiento(
            @Valid @RequestPart("data") AlojamientoRequest alojamientoRequest,
            @RequestPart("data") List<MultipartFile> multimedia)
            throws AlojamientoNotFound, AccessDeniedException {
         ResponseAlojamientoDTO createdAlojamiento =alojamientoServicio.guardarAlojamiento(alojamientoRequest, multimedia);
        return ResponseEntity.created(URI.create("/alojamiento/"+createdAlojamiento.getId())).body(createdAlojamiento);
    }
    @GetMapping("/{alojamientoId}")
    public ResponseEntity<ResponseAlojamientoDTO> getAlojamiento(@PathVariable Long alojamientoId) throws AlojamientoNotFound {
        ResponseAlojamientoDTO alojamiento= alojamientoServicio.obtenerAlojamiento(alojamientoId);
        return ResponseEntity.ok().body(alojamiento);
    }
    @GetMapping("/multimedia/{alojamientoId}/{imagenId}")
    public ResponseEntity<ResponseMultimediaDTO> getMultimedia(@PathVariable Long alojamientoId, @PathVariable String imagenId) throws AccessDeniedException {
        ResponseMultimediaDTO multimediaDTO= alojamientoMultimediaServicio.obtenerMultimedia(alojamientoId, imagenId);
        return ResponseEntity.ok().body(multimediaDTO);
    }
    @GetMapping("/{alojamientoId}/multimedia")
    public ResponseEntity<Page<ResponseMultimediaDTO>> getMultimedia(@PathVariable Long alojamientoId,
                                                                     @RequestParam int page,
                                                                     @RequestParam int size) throws AccessDeniedException {
        alojamientoMultimediaServicio.obtenerMultimediaPaginacion(alojamientoId, page, size);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alojamientos/{propietarioId}")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@PathVariable Long propietarioId,
                                                                        @RequestParam int page,
                                                                        @RequestParam int size) {
        alojamientoServicio.obtenerAlojamientoPaginacion(propietarioId, page, size);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/imagen/{alojamientoId}/{imagenId}")
    public ResponseEntity<Void> actualizarImagen(@PathVariable Long alojamientoId, @PathVariable String imagenId,
                                                 @RequestPart(value = "file", required = false) MultipartFile imagen)
            throws Exception {
        alojamientoMultimediaServicio.modificarArchivo(alojamientoId, imagenId, imagen);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/eliminar/{alojamientoId}")
    public ResponseEntity<Void> eliminarAlojamiento(@PathVariable Long alojamientoId) throws AlojamientoNotFound {
        alojamientoServicio.eliminarById(alojamientoId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/alojamientos/{alojamientoId}")
    public ResponseEntity<Void> actualizarPrecio(@PathVariable Long alojamientoId,
                                                 @Valid @RequestBody PriceDTO precio) throws AlojamientoNotFound {
        alojamientoServicio.modificarPrecio(alojamientoId,precio);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/disponibilidad/{alojamientoId}")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Long alojamientoId, @RequestBody String estado) throws AlojamientoNotFound {
        alojamientoServicio.actualizarEstadoAlojamiento(alojamientoId, estado);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/alojamientos/descripcion/{alojamientoId}")
    public ResponseEntity<Void> actualizarDescripcion(@PathVariable Long alojamientoId,
                                                      @Valid @RequestBody ContenidoDTO contenidoDTO) throws AlojamientoNotFound {
        alojamientoServicio.actualizarDescripcionAlojamiento(alojamientoId, contenidoDTO);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/alojamientos/ubicacion/{alojamientoId}")
    public ResponseEntity<Void> actualizarUbicacion(@PathVariable Long alojamientoId,
                                                      @Valid @RequestBody UbicacionDTO ubicacionDTO) throws AlojamientoNotFound {
        alojamientoServicio.actualizarUbicacionAlojamiento(alojamientoId, ubicacionDTO);
        return ResponseEntity.ok().build();
    }
    @PutMapping(value = "/{alojamientoId}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseAlojamientoDTO> actualizarAlojamiento(@PathVariable Long alojamientoId,
                                                                        @Valid @RequestPart("data") AlojamientoRequest alojamientoRequest,
                                                                        @RequestPart(value = "file", required = false) List<MultipartFile> multimedia
    ) throws AlojamientoNotFound {
        alojamientoServicio.actualizarAlojamiento(alojamientoId, alojamientoRequest, multimedia);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/eliminar/imagen/{alojamientoId}/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long alojamientoId, @PathVariable String imagenId) throws AccessDeniedException {
        alojamientoMultimediaServicio.eliminarArchivo(alojamientoId, imagenId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/dashboard/all")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@RequestParam int page,
                                                                        @RequestParam int size) {
        return ResponseEntity.ok(alojamientoServicio.obtenerTodosAlojamientosDashboard(page,size));
    }


}
