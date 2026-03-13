

package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoResponse {

    private Integer idTurno;
    private String codigo;
    private String descripcion;
    private Boolean activo;
}