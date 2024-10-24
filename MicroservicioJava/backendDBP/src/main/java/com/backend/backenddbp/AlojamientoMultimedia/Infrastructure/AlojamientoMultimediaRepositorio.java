package com.backend.backenddbp.AlojamientoMultimedia.Infrastructure;

import com.backend.backenddbp.Alojamiento.Domain.Alojamiento;
import com.backend.backenddbp.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlojamientoMultimediaRepositorio extends JpaRepository<AlojamientoMultimedia, String> {
    Page<AlojamientoMultimedia> findByAlojamiento_Id(Long propietarioId, Pageable pageable);
    Optional<AlojamientoMultimedia> findById(String id);
}
