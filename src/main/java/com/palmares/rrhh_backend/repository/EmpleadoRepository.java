package com.palmares.rrhh_backend.repository;

import com.palmares.rrhh_backend.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    List<Empleado> findByActivo(Boolean activo);

    List<Empleado> findByApellidoNombreContainingIgnoreCase(String apellidoNombre);
}