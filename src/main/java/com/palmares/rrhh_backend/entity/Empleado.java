package com.palmares.rrhh_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "Empleado")
public class Empleado {

    @Id
    @Column(name = "legajo", length = 20, nullable = false)
    private String legajo;

    @Column(name = "apellido_nombre", length = 150, nullable = false)
    private String apellidoNombre;

    @Column(name = "depto", length = 80)
    private String depto;

    @Column(name = "sucursal", length = 80)
    private String sucursal;

    @Column(name = "id_turno")
    private Integer idTurno;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_alta", length = 60, nullable = false)
    private String usuarioAlta;

    @Column(name = "usuario_modificacion", length = 60)
    private String usuarioModificacion;

    public Empleado() {
    }

}