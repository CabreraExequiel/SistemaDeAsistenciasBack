package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MarcacionArchivoDto {

    private String legajo;
    private LocalDateTime fechaHora;
    private String legajoNormalizado;
    private String mchn;
    private String mode;
    private String ioMode;
    private String nombreEnArchivo;
}