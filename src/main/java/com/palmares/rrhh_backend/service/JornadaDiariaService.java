package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.JornadaDiariaResponse;

import java.time.LocalDate;

public interface JornadaDiariaService {
    JornadaDiariaResponse calcularJornada(String legajo, LocalDate fecha);
}