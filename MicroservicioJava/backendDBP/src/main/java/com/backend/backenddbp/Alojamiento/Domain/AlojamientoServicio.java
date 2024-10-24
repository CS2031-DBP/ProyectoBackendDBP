package com.backend.backenddbp.Alojamiento.Domain;

import com.backend.backenddbp.Alojamiento.DTOS.*;
import com.backend.backenddbp.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.backenddbp.Alojamiento.Excepciones.DescripcionIgualException;
import com.backend.backenddbp.Alojamiento.Infrastructure.AlojamientoRepositorio;

import com.backend.backenddbp.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.backenddbp.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import com.backend.backenddbp.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import com.backend.backenddbp.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import com.backend.backenddbp.TipoMoneda;
import com.backend.backenddbp.Security.Utils.AuthorizationUtils;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlojamientoServicio {
    @Autowired
    AlojamientoRepositorio alojamientoRepositorio;
    @Autowired
    AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;
    @Autowired
    private AuthorizationUtils authorizationUtils;

    @Transactional
    public ResponseAlojamientoDTO guardarAlojamiento(AlojamientoRequest alojamiento,List<MultipartFile> multimedia) throws AlojamientoNotFound, AccessDeniedException {
        Alojamiento alojamientoAux = new Alojamiento();
        if(alojamiento.getDescripcion()==null ||
        alojamiento.getLongitude()==null || alojamiento.getLatitude()==null  ){
            throw new IllegalArgumentException("Los argumentos no deben ser nulos");
        }
        User currentPropietario = userRepository.findById(alojamiento.getPropietarioId()).
                orElseThrow(()-> new RuntimeException("Propietario no encontrado"));
        authorizationUtils.verifyUserAuthorization(currentPropietario.getEmail(), alojamiento.getPropietarioId());
        alojamientoAux.setPropietario(currentPropietario);
        alojamientoAux.setFechaPublicacion(LocalDateTime.now(ZoneId.systemDefault()));
        alojamientoAux.setDescripcion(alojamiento.getDescripcion());
        alojamientoAux.setLongitude(alojamiento.getLongitude());
        alojamientoAux.setLatitude(alojamiento.getLatitude());
        alojamientoAux.setEstado(Estado.DISPONIBLE);
        alojamientoAux.setPrecio(alojamiento.getPrecio());
        alojamientoAux.setCantidadBanios(alojamiento.getCantidadBanios());
        alojamientoAux.setCantidadCamas(alojamiento.getCantidadCamas());
        alojamientoAux.setCantidadHabitaciones(alojamiento.getCantidadHabitaciones());
        alojamientoRepositorio.save(alojamientoAux);
        if( !multimedia.isEmpty()){
            for (MultipartFile archivo : multimedia) {
                AlojamientoMultimedia multimed = alojamientoMultimediaServicio.guardarArchivo(archivo);
                multimed.setAlojamiento(alojamientoAux);
                alojamientoMultimediaRepositorio.save(multimed);
                alojamientoAux.getAlojamientoMultimedia().add(multimed);
            }
        }
        ResponseAlojamientoDTO responseAlojamientoDTO = mapResponseAlojamientoDTO(alojamientoAux.getId());
        return responseAlojamientoDTO;
    }

    public ResponseAlojamientoDTO obtenerAlojamiento(Long alojamientoId) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            return mapResponseAlojamientoDTO(alojamientoId);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void eliminarById(Long alojamientoId) throws AlojamientoNotFound {
        if (alojamientoRepositorio.existsById(alojamientoId)) {
            alojamientoRepositorio.deleteById(alojamientoId);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void modificarPrecio(Long alojamientoId, PriceDTO precio) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            alojamiento.setPrecio(precio.getPrecio());
            alojamiento.setTipoMoneda(precio.getTipoMoneda());
            alojamientoRepositorio.save(alojamiento);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void actualizarEstadoAlojamiento(Long alojamientoId, String estado) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            if(estado.toUpperCase().equals("DISPONIBLE")){
                alojamiento.setEstado(Estado.DISPONIBLE);
            }else{
                alojamiento.setEstado(Estado.NODISPONIBLE);
            }
            alojamientoRepositorio.save(alojamiento);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }


    public void actualizarDescripcionAlojamiento(Long alojamientoId, ContenidoDTO contenidoDTO) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            if (alojamiento.getDescripcion().equals(contenidoDTO.getDescripcion())) {
                throw new DescripcionIgualException("La descripcion debe de ser diferente a la proporcionada anteriormente.");
            } else {
                alojamiento.setDescripcion(contenidoDTO.getDescripcion());
                alojamientoRepositorio.save(alojamiento);
            }
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void actualizarUbicacionAlojamiento(Long id, UbicacionDTO ubicacionDTO) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(id);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            if (ubicacionDTO.getLatitude() == null || ubicacionDTO.getLongitude() == null) {
                alojamiento.setLatitude(alojamiento.getLatitude());
                alojamiento.setLongitude(alojamiento.getLongitude());

            } else {
                alojamiento.setLatitude(ubicacionDTO.getLatitude());
                alojamiento.setLongitude(ubicacionDTO.getLongitude());
            }
            alojamiento.setUbicacion(ubicacionDTO.getUbicacion());
            alojamientoRepositorio.save(alojamiento);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + id);
        }
    }

    public ResponseAlojamientoDTO actualizarAlojamiento(Long alojamientoId,
                                                        AlojamientoRequest alojamientoRequest, List<MultipartFile> multi) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            alojamiento.setDescripcion(alojamientoRequest.getDescripcion());
            alojamiento.setPrecio(alojamientoRequest.getPrecio());
            alojamiento.setTipoMoneda(alojamientoRequest.getTipoMoneda());
            alojamiento.setLatitude(alojamientoRequest.getLatitude());
            alojamiento.setLongitude(alojamientoRequest.getLongitude());
            alojamiento.setUbicacion(alojamientoRequest.getUbicacion());
            if(!multi.isEmpty()){
                for(AlojamientoMultimedia multimedia: alojamiento.getAlojamientoMultimedia()){
                    alojamientoMultimediaRepositorio.delete(multimedia);
                }
                for (MultipartFile archivo : multi) {
                    AlojamientoMultimedia multimedia = alojamientoMultimediaServicio.guardarArchivo(archivo);
                    multimedia.setAlojamiento(alojamiento);
                    alojamientoMultimediaRepositorio.save(multimedia);
                    alojamiento.getAlojamientoMultimedia().add(multimedia);
                }

            }
            alojamientoRepositorio.save(alojamiento);
            ResponseAlojamientoDTO responseAlojamientoDTO = mapResponseAlojamientoDTO(alojamientoId);
            return responseAlojamientoDTO;
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public Page<ResponseAlojamientoDTO> obtenerAlojamientoPaginacion(Long propietarioId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        User propietario = userRepository.findById(propietarioId).orElseThrow(()-> new RuntimeException("Propietario no encontrado"));
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findByPropietarioId(propietarioId, pageable);
        if(alojamientos.isEmpty()){
            throw new RuntimeException(propietario.getUsername()+ "No tiene alojamientos");
        }
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }


    public Page<ResponseAlojamientoDTO> obtenerAlojamientoPaginacionNoDisponibles(Long propietarioId, int page, int size ){
        Pageable pageable = PageRequest.of(page, size);
        User propietario = userRepository.findById(propietarioId).orElseThrow(()-> new RuntimeException("Propietario no encontrado"));
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findByPropietarioIdAndEstado(propietarioId, Estado.NODISPONIBLE,pageable);
        if(alojamientos.isEmpty()){
            throw new RuntimeException(propietario.getUsername()+ "No tiene alojamientos");
        }
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }

    public Page<ResponseAlojamientoDTO> obtenerAlojamientosPaginacionDisponibles(Long propietarioid, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        User propietario = userRepository.findById(propietarioid).orElseThrow(()-> new RuntimeException("Propietario no encontrado"));
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findByPropietarioIdAndEstado(propietarioid, Estado.DISPONIBLE, pageable);
        if(alojamientos.isEmpty()){
            throw new RuntimeException(propietario.getUsername()+ "No tiene alojamientos");
        }
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }

    private ResponseAlojamientoDTO mapResponseAlojamientoDTO(Long alojamientoid) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoid);
        if (!alojamientoOptional.isPresent()) {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoid);
        }
        Alojamiento alojamiento = alojamientoOptional.get();
        ResponseAlojamientoDTO responseAlojamientoDTO = new ResponseAlojamientoDTO();
        responseAlojamientoDTO.setId(alojamiento.getId());
        responseAlojamientoDTO.setPropietarioId(alojamiento.getPropietario().getId());
        responseAlojamientoDTO.setDescripcion(alojamiento.getDescripcion());
        responseAlojamientoDTO.setPrecio(alojamiento.getPrecio());
        responseAlojamientoDTO.setTipoMoneda(alojamiento.getTipoMoneda());
        responseAlojamientoDTO.setLatitude(alojamiento.getLatitude());
        responseAlojamientoDTO.setLongitude(alojamiento.getLongitude());
        responseAlojamientoDTO.setUbicacion(alojamiento.getUbicacion());

        List<ResponseMultimediaDTO> multimediaDTOList = new ArrayList<>();
        if(!alojamiento.getAlojamientoMultimedia().isEmpty()){
            for(AlojamientoMultimedia multimedia: alojamiento.getAlojamientoMultimedia()){
                ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimedia.setUrlContenido(multimedia.getUrlContenido());
                multimediaDTOList.add(multimediaDTO);
            }
        }
        responseAlojamientoDTO.setMultimedia(multimediaDTOList);
        return responseAlojamientoDTO;
    }



    public Page<ResponseAlojamientoDTO> obtenerAlojamientosDashboard(int page, int size, Double distancia ,
                                                                     Double maxPrecio, Double minPrec,
                                                                     String tipoMoneda,
                                                                     Double latitude, Double longuitude){
        Pageable pageable = PageRequest.of(page, size);
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findAllByEstado(Estado.DISPONIBLE,pageable);
        TipoMoneda realTipoMoneda;
        if (tipoMoneda == "SOLES"){
            realTipoMoneda = TipoMoneda.SOLES;
        }
        else{
            realTipoMoneda = TipoMoneda.DOLARES;
        }

        AlojamientoFilters filters = new AlojamientoFilters(latitude,longuitude,distancia,maxPrecio,minPrec,realTipoMoneda);
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .filter(alojamiento -> checkFilter(filters, alojamiento))
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }


    public Page<ResponseAlojamientoDTO> obtenerTodosAlojamientosDashboard(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findAll(pageable);

        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.stream()
                .map(this::mapBienResponseAlojamientoDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalPages());
    }

    private ResponseAlojamientoDTO mapBienResponseAlojamientoDTO(Alojamiento alojamiento) {
        ResponseAlojamientoDTO responseAlojamientoDTO = new ResponseAlojamientoDTO();
        responseAlojamientoDTO.setId(alojamiento.getId());
        responseAlojamientoDTO.setPropietarioId(alojamiento.getPropietario().getId());
        responseAlojamientoDTO.setDescripcion(alojamiento.getDescripcion());
        responseAlojamientoDTO.setPrecio(alojamiento.getPrecio());
        responseAlojamientoDTO.setTipoMoneda(alojamiento.getTipoMoneda());
        responseAlojamientoDTO.setLatitude(alojamiento.getLatitude());
        responseAlojamientoDTO.setLongitude(alojamiento.getLongitude());
        responseAlojamientoDTO.setUbicacion(alojamiento.getUbicacion());
        responseAlojamientoDTO.setCantidadBanios(alojamiento.getCantidadBanios());
        responseAlojamientoDTO.setCantidadCamas(alojamiento.getCantidadCamas());
        responseAlojamientoDTO.setCantidadHabitaciones(alojamiento.getCantidadHabitaciones());
        List<ResponseMultimediaDTO> multimediaDTOList = new ArrayList<>();
        if(!alojamiento.getAlojamientoMultimedia().isEmpty()){
            for(AlojamientoMultimedia multimedia: alojamiento.getAlojamientoMultimedia()) {
                ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimedia.setUrlContenido(multimedia.getUrlContenido());
                multimediaDTOList.add(multimediaDTO);
        }}

        responseAlojamientoDTO.setMultimedia(multimediaDTOList);
        return responseAlojamientoDTO;
    }

    public boolean checkFilter(AlojamientoFilters filters, Alojamiento a){
            double distance = calculateDistance(filters.getLatitude(), filters.getLongitude(), a.getLatitude(), a.getLongitude());

            return distance <= filters.getMaxDistance()&&
                    a.getPrecio() <= filters.getMaxPrecio() && a.getPrecio() >= filters.getMinPrecio() &&
                    a.getTipoMoneda().equals(filters.getTipoMoneda());
    }

    private static final double R = 6371;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}

