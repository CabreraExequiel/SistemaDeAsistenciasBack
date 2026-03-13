package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.*;

import java.util.List;

public interface TurnoService {

    List<TurnoResponse> listarTurnos(Boolean activo);

    TurnoResponse buscarTurno(Integer idTurno);

    TurnoResponse crearTurno(TurnoCreateRequest request);

    TurnoResponse editarTurno(Integer idTurno, TurnoUpdateRequest request);

    void activarTurno(Integer idTurno);

    void desactivarTurno(Integer idTurno);

    TurnoVentanaResponse crearVentana(Integer idTurno, TurnoVentanaCreateRequest request);

    TurnoVentanaResponse editarVentana(Long idVentana, TurnoVentanaUpdateRequest request);

    List<TurnoVentanaResponse> listarVentanas(Integer idTurno, Boolean activo);

    void activarVentana(Long idVentana);

    void desactivarVentana(Long idVentana);
}