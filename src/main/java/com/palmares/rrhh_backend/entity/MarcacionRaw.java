package com.palmares.rrhh_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MarcacionRaw", schema = "dbo")
@Getter
@Setter
public class MarcacionRaw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raw")
    private Long idRaw;

    @Column(name = "id_batch", nullable = false)
    private Long idBatch;

    @Column(name = "legajo", length = 20, nullable = false)
    private String legajo;

    @Column(name = "legajo_normalizado", length = 20)
    private String legajoNormalizado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "mchn", length = 20, nullable = false)
    private String mchn;

    @Column(name = "mode", length = 10)
    private String mode;

    @Column(name = "io_mode", length = 10)
    private String ioMode;

    @Column(name = "nombre_en_archivo", length = 150)
    private String nombreEnArchivo;

    @Column(name = "es_duplicada", nullable = false)
    private Boolean esDuplicada;

    @Column(name = "es_observada", nullable = false)
    private Boolean esObservada;

    @Column(name = "motivo_observada", length = 80)
    private String motivoObservada;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;
}