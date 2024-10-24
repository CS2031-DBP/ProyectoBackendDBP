package com.backend.backenddbp.AlojamientoMultimedia.Domain;

import com.backend.backenddbp.Alojamiento.Domain.Alojamiento;
import com.backend.backenddbp.Alojamiento.Infrastructure.AlojamientoRepositorio;
import com.backend.backenddbp.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.backenddbp.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.Utils.AuthorizationUtils;
import com.backend.backenddbp.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AlojamientoMultimediaServicio {
    @Autowired
    private StorageService storageService;
    private static Long idCounter =0L;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;
    @Autowired
    private AlojamientoRepositorio alojamientoRepositorio;
    @Autowired
    private AuthorizationUtils authorizationUtils;

    public AlojamientoMultimedia guardarArchivo(MultipartFile archivo) {
        try {
            AlojamientoMultimedia archivoMultimedia = new AlojamientoMultimedia();
            archivoMultimedia.setId(serializarId(generationId()));

            if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));
            String key = storageService.subiralS3File(archivo, archivoMultimedia.getId());
            archivoMultimedia.setUrlContenido(storageService.obtenerURL(key));
            return archivoMultimedia;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarArchivo(Long alojamientoId, String imagenId) throws AccessDeniedException {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento aloj= alojamientoOptional.get();
            authorizationUtils.verifyUserAuthorization(aloj.getPropietario().getEmail(), aloj.getPropietario().getId());
            for(AlojamientoMultimedia multimedia: aloj.getAlojamientoMultimedia()){
                if(multimedia.getId().equals(imagenId)){
                    storageService.deleteFile(multimedia.getId());
                    aloj.getAlojamientoMultimedia().remove(multimedia);
                    alojamientoMultimediaRepositorio.delete(multimedia);
                    alojamientoRepositorio.save(aloj);
                }
            }
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }


    public void modificarArchivo(Long alojamientoId, String imagenId, MultipartFile archivo) throws Exception {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento aloj= alojamientoOptional.get();
            authorizationUtils.verifyUserAuthorization(aloj.getPropietario().getEmail(), aloj.getPropietario().getId());
            for(AlojamientoMultimedia multimedia: aloj.getAlojamientoMultimedia()){
                if(multimedia.getId().equals(imagenId)){
                    if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                        multimedia.setTipo(Tipo.FOTO);
                    } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                        multimedia.setTipo(Tipo.VIDEO);
                    } else {
                        throw new IllegalArgumentException("Tipo de archivo no soportado");
                    }
                    String key = storageService.subiralS3File(archivo, multimedia.getId());
                    multimedia.setUrlContenido(storageService.obtenerURL(key));
                    multimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));
                    alojamientoMultimediaRepositorio.save(multimedia);
                    alojamientoRepositorio.save(aloj);
                }
            }
        }
        else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public Page<ResponseMultimediaDTO> obtenerMultimediaPaginacion(Long alojamientoId, int page, int size) throws AccessDeniedException {
        Pageable pageable = PageRequest.of(page, size);
        Alojamiento alojamiento = alojamientoRepositorio.findById(alojamientoId).orElseThrow(
                ()->new EntityNotFoundException("No se encontro el alojamiento"));
        authorizationUtils.verifyUserAuthorization(alojamiento.getPropietario().getEmail(), alojamiento.getPropietario().getId());
        Page <AlojamientoMultimedia> multimediaPage = alojamientoMultimediaRepositorio.findByAlojamiento_Id(alojamientoId, pageable);

        if (multimediaPage.isEmpty()){
            throw new EntityNotFoundException("No se encontraron imagenes para el alojamiento con id: "+alojamientoId);
        }
        List<ResponseMultimediaDTO> multimediaDTOList = multimediaPage.getContent().stream()
                .map(multimedia -> mapResponseMultimediaDTO(multimedia))
                .toList();
        return new PageImpl<>(multimediaDTOList, pageable, multimediaPage.getTotalElements());
    }

    public ResponseMultimediaDTO obtenerMultimedia(Long alojamientoId, String imagenId) throws AccessDeniedException {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            authorizationUtils.verifyUserAuthorization(alojamiento.getPropietario().getEmail(), alojamiento.getPropietario().getId());
            ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
            for (AlojamientoMultimedia multimedia : alojamiento.getAlojamientoMultimedia()) {
                if (multimedia.getId().equals((imagenId))) {
                    multimediaDTO.setId(multimedia.getId());
                    multimediaDTO.setTipo(multimedia.getTipo());
                    multimediaDTO.setUrl_contenido(multimedia.getUrlContenido());
                    return multimediaDTO;
                }
            }
            throw new EntityNotFoundException("No se encontró la imagen con id: " + imagenId);
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }
    private ResponseMultimediaDTO mapResponseMultimediaDTO(AlojamientoMultimedia multimedia){
        ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
        multimediaDTO.setId(multimedia.getId());
        multimediaDTO.setTipo(multimedia.getTipo());
        multimediaDTO.setUrl_contenido(multimedia.getUrlContenido());
        return multimediaDTO;
    }

    private String serializarId(Long imagenId){
        return "imagen-" + imagenId;
    }
    public Long generationId() {
        return ++idCounter;
    }
}
