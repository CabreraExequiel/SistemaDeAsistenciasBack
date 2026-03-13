package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoCreateRequest {

    private String codigo;
    private String descripcion;
    private String usuarioAlta;
}