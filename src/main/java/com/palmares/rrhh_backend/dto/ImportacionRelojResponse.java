package com.palmares.rrhh_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportacionRelojResponse {

    private Long idBatch;
    private String archivoNombre;
    private String estado;
    private Integer totalLeidas;
    private Integer totalInsertadas;
    private Integer totalDuplicadas;
    private Integer totalObservadas;
}