package com.palmares.rrhh_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "TurnoVentana")
public class TurnoVentana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ventana")
    private Long idVentana;

    @Column(name = "id_turno", nullable = false)
    private Integer idTurno;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_desde", nullable = false)
    private LocalTime horaDesde;

    @Column(name = "hora_hasta", nullable = false)
    private LocalTime horaHasta;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "tipo", length = 10, nullable = false)
    private String tipo;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}