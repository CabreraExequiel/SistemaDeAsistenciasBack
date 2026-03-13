package com.palmares.rrhh_backend.controller;

import com.palmares.rrhh_backend.dto.TurnoCreateRequest;
import com.palmares.rrhh_backend.dto.TurnoResponse;
import com.palmares.rrhh_backend.dto.TurnoUpdateRequest;
import com.palmares.rrhh_backend.dto.TurnoVentanaCreateRequest;
import com.palmares.rrhh_backend.dto.TurnoVentanaResponse;
import com.palmares.rrhh_backend.dto.TurnoVentanaUpdateRequest;
import com.palmares.rrhh_backend.service.TurnoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public List<TurnoResponse> listarTurnos(@RequestParam(required = false) Boolean activo) {
        return turnoService.listarTurnos(activo);
    }

    @GetMapping("/{idTurno}")
    public TurnoResponse buscarTurno(@PathVariable Integer idTurno) {
        return turnoService.buscarTurno(idTurno);
    }

    @PostMapping
    public TurnoResponse crearTurno(@RequestBody TurnoCreateRequest request) {
        return turnoService.crearTurno(request);
    }

    @PutMapping("/{idTurno}")
    public TurnoResponse editarTurno(
            @PathVariable Integer idTurno,
            @RequestBody TurnoUpdateRequest request
    ) {
        return turnoService.editarTurno(idTurno, request);
    }

    @PatchMapping("/{idTurno}/activar")
    public Map<String, String> activarTurno(@PathVariable Integer idTurno) {
        turnoService.activarTurno(idTurno);
        return Map.of("mensaje", "Turno activado correctamente");
    }

    @PatchMapping("/{idTurno}/desactivar")
    public Map<String, String> desactivarTurno(@PathVariable Integer idTurno) {
        turnoService.desactivarTurno(idTurno);
        return Map.of("mensaje", "Turno desactivado correctamente");
    }

    @GetMapping("/{idTurno}/ventanas")
    public List<TurnoVentanaResponse> listarVentanas(
            @PathVariable Integer idTurno,
            @RequestParam(required = false) Boolean activo
    ) {
        return turnoService.listarVentanas(idTurno, activo);
    }

    @PostMapping("/{idTurno}/ventanas")
    public TurnoVentanaResponse crearVentana(
            @PathVariable Integer idTurno,
            @RequestBody TurnoVentanaCreateRequest request
    ) {
        return turnoService.crearVentana(idTurno, request);
    }

    @PutMapping("/ventanas/{idVentana}")
    public TurnoVentanaResponse editarVentana(
            @PathVariable Long idVentana,
            @RequestBody TurnoVentanaUpdateRequest request
    ) {
        return turnoService.editarVentana(idVentana, request);
    }

    @PatchMapping("/ventanas/{idVentana}/activar")
    public Map<String, String> activarVentana(@PathVariable Long idVentana) {
        turnoService.activarVentana(idVentana);
        return Map.of("mensaje", "Ventana activada correctamente");
    }

    @PatchMapping("/ventanas/{idVentana}/desactivar")
    public Map<String, String> desactivarVentana(@PathVariable Long idVentana) {
        turnoService.desactivarVentana(idVentana);
        return Map.of("mensaje", "Ventana desactivada correctamente");
    }
}