package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoUpdateRequest {

    private String apellidoNombre;
    private String depto;
    private String sucursal;
    private Integer idTurno;
    private String usuarioModificacion;
}