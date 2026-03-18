package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.EmpleadoCreateRequest;
import com.palmares.rrhh_backend.dto.EmpleadoResponse;
import com.palmares.rrhh_backend.dto.EmpleadoUpdateRequest;
import com.palmares.rrhh_backend.entity.Empleado;
import com.palmares.rrhh_backend.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public List<EmpleadoResponse> listar(Boolean activo, String nombre) {
        List<Empleado> empleados;

        boolean filtraActivo = activo != null;
        boolean filtraNombre = nombre != null && !nombre.trim().isEmpty();

        if (filtraActivo) {
            empleados = empleadoRepository.findByActivo(activo);
        } else if (filtraNombre) {
            empleados = empleadoRepository.findByApellidoNombreContainingIgnoreCase(nombre.trim());
        } else {
            empleados = empleadoRepository.findAll();
        }

        return empleados.stream().map(this::toResponse).toList();
    }

    @Override
    public EmpleadoResponse buscarPorLegajo(String legajo) {
        Empleado empleado = empleadoRepository.findById(legajo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        return toResponse(empleado);
    }

    @Override
    public EmpleadoResponse crear(EmpleadoCreateRequest request) {
        if (request.getLegajo() == null || request.getLegajo().trim().isEmpty()) {
            throw new RuntimeException("El legajo es obligatorio");
        }

        if (request.getApellidoNombre() == null || request.getApellidoNombre().trim().isEmpty()) {
            throw new RuntimeException("El apellido y nombre es obligatorio");
        }

        if (request.getUsuarioAlta() == null || request.getUsuarioAlta().trim().isEmpty()) {
            throw new RuntimeException("El usuario de alta es obligatorio");
        }

        String legajo = request.getLegajo().trim();

        if (empleadoRepository.existsById(legajo)) {
            throw new RuntimeException("Ya existe un empleado con ese legajo");
        }

        Empleado empleado = new Empleado();
        empleado.setLegajo(legajo);
        empleado.setApellidoNombre(request.getApellidoNombre().trim());
        empleado.setDepto(request.getDepto());
        empleado.setSucursal(request.getSucursal());
        empleado.setIdTurno(request.getIdTurno());
        empleado.setActivo(true);
        empleado.setFechaAlta(LocalDateTime.now());
        empleado.setUsuarioAlta(request.getUsuarioAlta().trim());

        empleadoRepository.save(empleado);

        return toResponse(empleado);
    }

    @Override
    public EmpleadoResponse editar(String legajo, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findById(legajo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (request.getApellidoNombre() == null || request.getApellidoNombre().trim().isEmpty()) {
            throw new RuntimeException("El apellido y nombre es obligatorio");
        }

        if (request.getUsuarioModificacion() == null || request.getUsuarioModificacion().trim().isEmpty()) {
            throw new RuntimeException("El usuario de modificación es obligatorio");
        }

        empleado.setApellidoNombre(request.getApellidoNombre().trim());
        empleado.setDepto(request.getDepto());
        empleado.setSucursal(request.getSucursal());
        empleado.setIdTurno(request.getIdTurno());
        empleado.setFechaModificacion(LocalDateTime.now());
        empleado.setUsuarioModificacion(request.getUsuarioModificacion().trim());

        empleadoRepository.save(empleado);

        return toResponse(empleado);
    }

    @Override
    public void activar(String legajo, String usuario) {
        Empleado empleado = empleadoRepository.findById(legajo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        empleado.setActivo(true);
        empleado.setFechaModificacion(LocalDateTime.now());
        empleado.setUsuarioModificacion(usuario);

        empleadoRepository.save(empleado);
    }

    @Override
    public void desactivar(String legajo, String usuario) {
        Empleado empleado = empleadoRepository.findById(legajo)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        empleado.setActivo(false);
        empleado.setFechaModificacion(LocalDateTime.now());
        empleado.setUsuarioModificacion(usuario);

        empleadoRepository.save(empleado);
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setLegajo(empleado.getLegajo());
        response.setApellidoNombre(empleado.getApellidoNombre());
        response.setDepto(empleado.getDepto());
        response.setSucursal(empleado.getSucursal());
        response.setIdTurno(empleado.getIdTurno());
        response.setActivo(empleado.getActivo());
        response.setFechaAlta(empleado.getFechaAlta());
        response.setFechaModificacion(empleado.getFechaModificacion());
        return response;
    }
}