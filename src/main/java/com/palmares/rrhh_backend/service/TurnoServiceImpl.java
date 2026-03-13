package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.*;
import com.palmares.rrhh_backend.entity.Turno;
import com.palmares.rrhh_backend.entity.TurnoVentana;
import com.palmares.rrhh_backend.repository.TurnoRepository;
import com.palmares.rrhh_backend.repository.TurnoVentanaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final TurnoVentanaRepository turnoVentanaRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository, TurnoVentanaRepository turnoVentanaRepository) {
        this.turnoRepository = turnoRepository;
        this.turnoVentanaRepository = turnoVentanaRepository;
    }

    @Override
    public List<TurnoResponse> listarTurnos(Boolean activo) {
        List<Turno> turnos = (activo != null)
                ? turnoRepository.findByActivo(activo)
                : turnoRepository.findAll();

        return turnos.stream().map(this::toTurnoResponse).toList();
    }

    @Override
    public TurnoResponse buscarTurno(Integer idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        return toTurnoResponse(turno);
    }

    @Override
    public TurnoResponse crearTurno(TurnoCreateRequest request) {
        if (request.getCodigo() == null || request.getCodigo().trim().isEmpty()) {
            throw new RuntimeException("El código es obligatorio");
        }

        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        if (request.getUsuarioAlta() == null || request.getUsuarioAlta().trim().isEmpty()) {
            throw new RuntimeException("El usuarioAlta es obligatorio");
        }

        if (turnoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new RuntimeException("Ya existe un turno con ese código");
        }

        Turno turno = new Turno();
        turno.setCodigo(request.getCodigo());
        turno.setDescripcion(request.getDescripcion());
        turno.setActivo(true);
        turno.setFechaAlta(LocalDateTime.now());
        turno.setUsuarioAlta(request.getUsuarioAlta());

        turnoRepository.save(turno);

        return toTurnoResponse(turno);
    }

    @Override
    public TurnoResponse editarTurno(Integer idTurno, TurnoUpdateRequest request) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setDescripcion(request.getDescripcion());
        turnoRepository.save(turno);

        return toTurnoResponse(turno);
    }

    @Override
    public void activarTurno(Integer idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setActivo(true);
        turnoRepository.save(turno);
    }

    @Override
    public void desactivarTurno(Integer idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setActivo(false);
        turnoRepository.save(turno);
    }

    @Override
    public TurnoVentanaResponse crearVentana(Integer idTurno, TurnoVentanaCreateRequest request) {
        if (!turnoRepository.existsById(idTurno)) {
            throw new RuntimeException("El turno no existe");
        }

        validarVentana(request.getDiaSemana(), request.getHoraDesde(), request.getHoraHasta(), request.getTipo(), request.getOrden());

        TurnoVentana ventana = new TurnoVentana();
        ventana.setIdTurno(idTurno);
        ventana.setDiaSemana(request.getDiaSemana());
        ventana.setHoraDesde(LocalTime.parse(request.getHoraDesde()));
        ventana.setHoraHasta(LocalTime.parse(request.getHoraHasta()));
        ventana.setOrden(request.getOrden());
        ventana.setTipo(request.getTipo());
        ventana.setActivo(true);

        turnoVentanaRepository.save(ventana);

        return toVentanaResponse(ventana);
    }

    @Override
    public TurnoVentanaResponse editarVentana(Long idVentana, TurnoVentanaUpdateRequest request) {
        TurnoVentana ventana = turnoVentanaRepository.findById(idVentana)
                .orElseThrow(() -> new RuntimeException("Ventana no encontrada"));

        validarVentana(request.getDiaSemana(), request.getHoraDesde(), request.getHoraHasta(), request.getTipo(), request.getOrden());

        ventana.setDiaSemana(request.getDiaSemana());
        ventana.setHoraDesde(LocalTime.parse(request.getHoraDesde()));
        ventana.setHoraHasta(LocalTime.parse(request.getHoraHasta()));
        ventana.setOrden(request.getOrden());
        ventana.setTipo(request.getTipo());
        ventana.setActivo(request.getActivo());

        turnoVentanaRepository.save(ventana);

        return toVentanaResponse(ventana);
    }

    @Override
    public List<TurnoVentanaResponse> listarVentanas(Integer idTurno, Boolean activo) {
        if (!turnoRepository.existsById(idTurno)) {
            throw new RuntimeException("El turno no existe");
        }

        List<TurnoVentana> ventanas = (activo != null)
                ? turnoVentanaRepository.findByIdTurnoAndActivoOrderByDiaSemanaAscOrdenAsc(idTurno, activo)
                : turnoVentanaRepository.findByIdTurnoOrderByDiaSemanaAscOrdenAsc(idTurno);

        return ventanas.stream().map(this::toVentanaResponse).toList();
    }

    @Override
    public void activarVentana(Long idVentana) {
        TurnoVentana ventana = turnoVentanaRepository.findById(idVentana)
                .orElseThrow(() -> new RuntimeException("Ventana no encontrada"));

        ventana.setActivo(true);
        turnoVentanaRepository.save(ventana);
    }

    @Override
    public void desactivarVentana(Long idVentana) {
        TurnoVentana ventana = turnoVentanaRepository.findById(idVentana)
                .orElseThrow(() -> new RuntimeException("Ventana no encontrada"));

        ventana.setActivo(false);
        turnoVentanaRepository.save(ventana);
    }

    private void validarVentana(Integer diaSemana, String horaDesde, String horaHasta, String tipo, Integer orden) {
        if (diaSemana == null || diaSemana < 1 || diaSemana > 7) {
            throw new RuntimeException("diaSemana debe estar entre 1 y 7");
        }

        if (horaDesde == null || horaHasta == null) {
            throw new RuntimeException("horaDesde y horaHasta son obligatorias");
        }

        LocalTime desde = LocalTime.parse(horaDesde);
        LocalTime hasta = LocalTime.parse(horaHasta);

        if (!desde.isBefore(hasta)) {
            throw new RuntimeException("horaDesde debe ser menor que horaHasta");
        }

        if (tipo == null || (!tipo.equals("IN") && !tipo.equals("OUT") && !tipo.equals("TRAMO"))) {
            throw new RuntimeException("tipo debe ser IN, OUT o TRAMO");
        }

        if (orden == null || orden < 1) {
            throw new RuntimeException("orden debe ser mayor a 0");
        }
    }

    private TurnoResponse toTurnoResponse(Turno turno) {
        TurnoResponse r = new TurnoResponse();
        r.setIdTurno(turno.getIdTurno());
        r.setCodigo(turno.getCodigo());
        r.setDescripcion(turno.getDescripcion());
        r.setActivo(turno.getActivo());
        return r;
    }

    private TurnoVentanaResponse toVentanaResponse(TurnoVentana v) {
        TurnoVentanaResponse r = new TurnoVentanaResponse();
        r.setIdVentana(v.getIdVentana());
        r.setIdTurno(v.getIdTurno());
        r.setDiaSemana(v.getDiaSemana());
        r.setHoraDesde(v.getHoraDesde().toString());
        r.setHoraHasta(v.getHoraHasta().toString());
        r.setOrden(v.getOrden());
        r.setTipo(v.getTipo());
        r.setActivo(v.getActivo());
        return r;
    }
}