package com.palmares.rrhh_backend.controller;

import com.palmares.rrhh_backend.dto.EmpleadoCreateRequest;
import com.palmares.rrhh_backend.dto.EmpleadoResponse;
import com.palmares.rrhh_backend.dto.EmpleadoUpdateRequest;
import com.palmares.rrhh_backend.service.EmpleadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<EmpleadoResponse> listar(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String nombre
    ) {
        return empleadoService.listar(activo, nombre);
    }

    @GetMapping("/{legajo}")
    public EmpleadoResponse buscarPorLegajo(@PathVariable String legajo) {
        return empleadoService.buscarPorLegajo(legajo);
    }

    @PostMapping
    public EmpleadoResponse crear(@RequestBody EmpleadoCreateRequest request) {
        return empleadoService.crear(request);
    }

    @PutMapping("/{legajo}")
    public EmpleadoResponse editar(
            @PathVariable String legajo,
            @RequestBody EmpleadoUpdateRequest request
    ) {
        return empleadoService.editar(legajo, request);
    }

    @PatchMapping("/{legajo}/activar")
    public Map<String, String> activar(
            @PathVariable String legajo,
            @RequestParam String usuario
    ) {
        empleadoService.activar(legajo, usuario);
        return Map.of("mensaje", "Empleado activado correctamente");
    }

    @PatchMapping("/{legajo}/desactivar")
    public Map<String, String> desactivar(
            @PathVariable String legajo,
            @RequestParam String usuario
    ) {
        empleadoService.desactivar(legajo, usuario);
        return Map.of("mensaje", "Empleado desactivado correctamente");
    }
}