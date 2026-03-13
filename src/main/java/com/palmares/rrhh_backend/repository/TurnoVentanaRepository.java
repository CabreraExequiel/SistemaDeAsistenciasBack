package com.palmares.rrhh_backend.repository;

import com.palmares.rrhh_backend.entity.TurnoVentana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoVentanaRepository extends JpaRepository<TurnoVentana, Long> {

    List<TurnoVentana> findByIdTurnoOrderByDiaSemanaAscOrdenAsc(Integer idTurno);

    List<TurnoVentana> findByIdTurnoAndActivoOrderByDiaSemanaAscOrdenAsc(Integer idTurno, Boolean activo);
}