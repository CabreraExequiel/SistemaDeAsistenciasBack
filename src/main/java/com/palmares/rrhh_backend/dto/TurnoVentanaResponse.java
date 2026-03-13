package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnoVentanaResponse {

    private Long idVentana;
    private Integer idTurno;
    private Integer diaSemana;
    private String horaDesde;
    private String horaHasta;
    private Integer orden;
    private String tipo;
    private Boolean activo;
}