package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmpleadoResponse {

    private String legajo;
    private String apellidoNombre;
    private String depto;
    private String sucursal;
    private Integer idTurno;
    private Boolean activo;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaModificacion;
}