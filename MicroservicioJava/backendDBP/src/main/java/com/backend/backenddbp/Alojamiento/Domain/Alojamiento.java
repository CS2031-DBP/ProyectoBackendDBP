package com.backend.backenddbp.Alojamiento.Domain;

import com.backend.backenddbp.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import com.backend.backenddbp.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import TipoMoneda;
import com.backend.backenddbp.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "estado")
    private Estado estado;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitud")
    private Double longitude;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "fechaPublicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "tipoMoneda")
    private TipoMoneda tipoMoneda;

    @Column(name = "cantidad_habitaciones")
    private int cantidadHabitaciones = 0;

    @Column(name = "cantidad_camas")
    private int cantidadCamas = 0;

    @Column(name = "cantidad_banios")
    private int cantidadBanios = 0;

    @OneToMany(mappedBy = "alojamiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlojamientoMultimedia> alojamientoMultimedia = new ArrayList<>();

    @OneToOne(mappedBy = "alojamientoP")
    private PublicacionAlojamiento publicacionAlojamiento;

    @ManyToOne
    @JoinColumn(name = "propietario_id", referencedColumnName = "id")
    private User propietario;
}
