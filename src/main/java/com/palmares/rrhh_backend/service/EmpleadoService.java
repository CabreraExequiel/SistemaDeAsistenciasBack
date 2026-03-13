package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.EmpleadoCreateRequest;
import com.palmares.rrhh_backend.dto.EmpleadoResponse;
import com.palmares.rrhh_backend.dto.EmpleadoUpdateRequest;

import java.util.List;

public interface EmpleadoService {

    List<EmpleadoResponse> listar(Boolean activo, String nombre);

    EmpleadoResponse buscarPorLegajo(String legajo);

    EmpleadoResponse crear(EmpleadoCreateRequest request);

    EmpleadoResponse editar(String legajo, EmpleadoUpdateRequest request);

    void activar(String legajo, String usuario);

    void desactivar(String legajo, String usuario);
}