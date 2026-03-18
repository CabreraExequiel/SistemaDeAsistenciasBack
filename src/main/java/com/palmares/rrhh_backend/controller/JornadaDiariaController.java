package com.palmares.rrhh_backend.controller;

import com.palmares.rrhh_backend.dto.JornadaDiariaResponse;
import com.palmares.rrhh_backend.service.JornadaDiariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/jornadas")
@CrossOrigin(origins = "*")
public class JornadaDiariaController {

    private final JornadaDiariaService jornadaDiariaService;

    public JornadaDiariaController(JornadaDiariaService jornadaDiariaService) {
        this.jornadaDiariaService = jornadaDiariaService;
    }

    @GetMapping("/calcular")
    public ResponseEntity<JornadaDiariaResponse> calcular(
            @RequestParam String legajo,
            @RequestParam String fecha
    ) {
        JornadaDiariaResponse response = jornadaDiariaService.calcularJornada(
                legajo,
                LocalDate.parse(fecha)
        );

        return ResponseEntity.ok(response);
    }
}