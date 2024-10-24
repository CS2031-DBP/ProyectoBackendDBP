package com.backend.backenddbp.Alojamiento.Infrastructure;

import com.backend.backenddbp.Alojamiento.Domain.Alojamiento;
import com.backend.backenddbp.Alojamiento.Domain.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlojamientoRepositorio extends JpaRepository<Alojamiento, Long> {
    Page<Alojamiento> findByPropietarioId(Long propietarioId, Pageable pageable);

    Page<Alojamiento> findByPropietarioIdAndEstado(Long propietarioId, Estado estado, Pageable pageable);

    Page<Alojamiento> findAllByEstado(Estado estado, Pageable pageable);
}
