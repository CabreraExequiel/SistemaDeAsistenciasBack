package com.palmares.rrhh_backend.repository;

import com.palmares.rrhh_backend.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Integer> {

    Optional<Turno> findByCodigo(String codigo);

    List<Turno> findByActivo(Boolean activo);

}