package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.JornadaDiariaResponse;
import com.palmares.rrhh_backend.dto.TramoJornadaResponse;
import com.palmares.rrhh_backend.entity.Empleado;
import com.palmares.rrhh_backend.entity.MarcacionRaw;
import com.palmares.rrhh_backend.entity.Turno;
import com.palmares.rrhh_backend.entity.TurnoVentana;
import com.palmares.rrhh_backend.repository.EmpleadoRepository;
import com.palmares.rrhh_backend.repository.MarcacionRawRepository;
import com.palmares.rrhh_backend.repository.TurnoRepository;
import com.palmares.rrhh_backend.repository.TurnoVentanaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JornadaDiariaServiceImpl implements JornadaDiariaService {

    private final EmpleadoRepository empleadoRepository;
    private final MarcacionRawRepository marcacionRawRepository;
    private final TurnoRepository turnoRepository;
    private final TurnoVentanaRepository turnoVentanaRepository;

    public JornadaDiariaServiceImpl(
            EmpleadoRepository empleadoRepository,
            MarcacionRawRepository marcacionRawRepository,
            TurnoRepository turnoRepository,
            TurnoVentanaRepository turnoVentanaRepository
    ) {
        this.empleadoRepository = empleadoRepository;
        this.marcacionRawRepository = marcacionRawRepository;
        this.turnoRepository = turnoRepository;
        this.turnoVentanaRepository = turnoVentanaRepository;
    }

    @Override
    public JornadaDiariaResponse calcularJornada(String legajo, LocalDate fecha) {
        String legajoNormalizado = normalizarLegajo(legajo);

        JornadaDiariaResponse response = new JornadaDiariaResponse();
        response.setLegajo(legajoNormalizado);
        response.setFecha(fecha);
        response.setTieneInconsistencias(false);
        response.setTieneMarcasIntermedias(false);
        response.setCierreAutomaticoPermitido(false);
        response.setCantidadMarcas(0);
        response.setCantidadTramos(0);
        response.setMinutosTrabajados(0);

        if (legajoNormalizado == null || legajoNormalizado.isBlank()) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("El legajo es obligatorio.");
            return response;
        }

        Empleado empleado = empleadoRepository.findById(legajoNormalizado).orElse(null);

        if (empleado == null) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("El empleado no existe.");
            return response;
        }

        if (empleado.getIdTurno() == null) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("El empleado no tiene turno asignado.");
            return response;
        }

        Turno turno = turnoRepository.findById(empleado.getIdTurno()).orElse(null);

        if (turno == null) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("No se encontró el turno del empleado.");
            return response;
        }

        List<TurnoVentana> ventanas = turnoVentanaRepository
                .findByIdTurnoAndActivoOrderByDiaSemanaAscOrdenAsc(empleado.getIdTurno(), true);

        if (ventanas == null || ventanas.isEmpty()) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("El turno no tiene ventanas activas configuradas.");
            return response;
        }

        TurnoVentana ventanaBase = seleccionarVentanaParaFecha(ventanas, fecha);
        if (ventanaBase == null) {
            ventanaBase = ventanas.get(0);
            response.getObservaciones().add("No se encontró una ventana exacta para el día. Se usó la primera ventana activa.");
        }

        LocalTime horaEntrada = obtenerHoraEntrada(ventanaBase);
        LocalTime horaSalida = obtenerHoraSalida(ventanaBase);
        int toleranciaIngresoMin = obtenerToleranciaIngreso(ventanaBase);
        int toleranciaEgresoMin = obtenerToleranciaEgreso(ventanaBase);
        int redondeoMin = obtenerRedondeo(ventanaBase);

        if (horaEntrada == null || horaSalida == null) {
            response.setEstado("OBSERVADO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("No se pudieron interpretar las horas de la ventana del turno.");
            return response;
        }

        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

        List<MarcacionRaw> todasLasMarcaciones = marcacionRawRepository.findAll();

        List<LocalDateTime> marcasOrdenadas = todasLasMarcaciones.stream()
                .filter(Objects::nonNull)
                .filter(m -> legajoCoincide(m, legajoNormalizado))
                .map(this::obtenerFechaHoraMarcacion)
                .filter(Objects::nonNull)
                .filter(fh -> !fh.isBefore(desde) && fh.isBefore(hasta))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        response.setCantidadMarcas(marcasOrdenadas.size());

        if (marcasOrdenadas.isEmpty()) {
            response.setEstado("INCOMPLETO");
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("No hay marcaciones para la fecha indicada.");
            return response;
        }

        List<LocalDateTime> marcasLimpias = limpiarDuplicadosCercanos(marcasOrdenadas, 1);
        response.setCantidadMarcas(marcasLimpias.size());

        List<TramoJornadaResponse> tramos = new ArrayList<>();

        for (int i = 0; i + 1 < marcasLimpias.size(); i += 2) {
            LocalDateTime entrada = marcasLimpias.get(i);
            LocalDateTime salida = marcasLimpias.get(i + 1);

            if (!salida.isAfter(entrada)) {
                response.setTieneInconsistencias(true);
                response.getObservaciones().add("Se detectó un tramo inválido donde la salida no es posterior a la entrada.");
                continue;
            }

            TramoJornadaResponse tramo = new TramoJornadaResponse();
            tramo.setEntradaReal(entrada);
            tramo.setSalidaReal(salida);
            tramos.add(tramo);
        }

        if (marcasLimpias.size() % 2 != 0) {
            response.setTieneInconsistencias(true);
            response.getObservaciones().add("Cantidad impar de marcaciones. Quedó una marca sin par.");
        }

        if (tramos.isEmpty()) {
            response.setEstado("INCOMPLETO");
            response.setCierreAutomaticoPermitido(false);
            return response;
        }

        response.setCantidadTramos(tramos.size());
        response.setTieneMarcasIntermedias(tramos.size() > 1);

        TramoJornadaResponse primerTramo = tramos.get(0);
        TramoJornadaResponse ultimoTramo = tramos.get(tramos.size() - 1);

        response.setPrimeraEntradaReal(primerTramo.getEntradaReal());
        response.setUltimaSalidaReal(ultimoTramo.getSalidaReal());

        LocalDateTime primeraEntradaAjustada = ajustarEntrada(
                primerTramo.getEntradaReal(),
                horaEntrada,
                toleranciaIngresoMin,
                redondeoMin
        );

        LocalDateTime ultimaSalidaAjustada = ajustarSalida(
                ultimoTramo.getSalidaReal(),
                horaSalida,
                toleranciaEgresoMin,
                redondeoMin
        );

        response.setPrimeraEntradaAjustada(primeraEntradaAjustada);
        response.setUltimaSalidaAjustada(ultimaSalidaAjustada);

        for (int i = 0; i < tramos.size(); i++) {
            TramoJornadaResponse tramo = tramos.get(i);

            tramo.setEntradaAjustada(i == 0 ? primeraEntradaAjustada : tramo.getEntradaReal());
            tramo.setSalidaAjustada(i == tramos.size() - 1 ? ultimaSalidaAjustada : tramo.getSalidaReal());

            long minutos = Duration.between(tramo.getEntradaAjustada(), tramo.getSalidaAjustada()).toMinutes();

            if (minutos < 0) {
                response.setTieneInconsistencias(true);
                response.getObservaciones().add("Se detectó un tramo ajustado con minutos negativos.");
                tramo.setMinutosTrabajados(0);
            } else {
                tramo.setMinutosTrabajados((int) minutos);
            }
        }

        response.setTramos(tramos);

        int totalMinutos = tramos.stream()
                .map(TramoJornadaResponse::getMinutosTrabajados)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        response.setMinutosTrabajados(totalMinutos);

        boolean huboAjustes =
                !Objects.equals(response.getPrimeraEntradaReal(), response.getPrimeraEntradaAjustada()) ||
                        !Objects.equals(response.getUltimaSalidaReal(), response.getUltimaSalidaAjustada());

        if (Boolean.TRUE.equals(response.getTieneInconsistencias())) {
            response.setEstado("OBSERVADO");
            response.setCierreAutomaticoPermitido(false);
        } else if (huboAjustes) {
            response.setEstado("AJUSTADO");
            response.setCierreAutomaticoPermitido(true);
        } else {
            response.setEstado("COMPLETO");
            response.setCierreAutomaticoPermitido(true);
        }

        return response;
    }

    private TurnoVentana seleccionarVentanaParaFecha(List<TurnoVentana> ventanas, LocalDate fecha) {
        int diaJava = fecha.getDayOfWeek().getValue();
        Integer diaBuscado1 = diaJava;
        Integer diaBuscado2 = convertirDiaSemanaADominioLocal(diaJava);

        for (TurnoVentana v : ventanas) {
            Integer dia = obtenerDiaSemana(v);
            if (dia != null && (dia.equals(diaBuscado1) || dia.equals(diaBuscado2))) {
                return v;
            }
        }

        return null;
    }

    private Integer convertirDiaSemanaADominioLocal(int diaJava) {
        return diaJava == 7 ? 1 : diaJava + 1;
    }

    private List<LocalDateTime> limpiarDuplicadosCercanos(List<LocalDateTime> marcas, int ventanaMinutos) {
        if (marcas == null || marcas.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocalDateTime> limpias = new ArrayList<>();
        limpias.add(marcas.get(0));

        for (int i = 1; i < marcas.size(); i++) {
            LocalDateTime anterior = limpias.get(limpias.size() - 1);
            LocalDateTime actual = marcas.get(i);

            long diferencia = Duration.between(anterior, actual).toMinutes();

            if (diferencia > ventanaMinutos) {
                limpias.add(actual);
            }
        }

        return limpias;
    }

    private LocalDateTime ajustarEntrada(LocalDateTime entradaReal, LocalTime horaEntradaTurno, int toleranciaIngresoMin, int redondeoMin) {
        LocalDateTime horarioTurno = LocalDateTime.of(entradaReal.toLocalDate(), horaEntradaTurno);
        long diferencia = Duration.between(horarioTurno, entradaReal).toMinutes();

        if (Math.abs(diferencia) <= toleranciaIngresoMin) {
            return horarioTurno;
        }

        return redondear(entradaReal, redondeoMin);
    }

    private LocalDateTime ajustarSalida(LocalDateTime salidaReal, LocalTime horaSalidaTurno, int toleranciaEgresoMin, int redondeoMin) {
        LocalDateTime horarioTurno = LocalDateTime.of(salidaReal.toLocalDate(), horaSalidaTurno);
        long diferencia = Duration.between(horarioTurno, salidaReal).toMinutes();

        if (Math.abs(diferencia) <= toleranciaEgresoMin) {
            return horarioTurno;
        }

        return redondear(salidaReal, redondeoMin);
    }

    private LocalDateTime redondear(LocalDateTime fechaHora, int bloqueMinutos) {
        if (bloqueMinutos <= 0) {
            return fechaHora.withSecond(0).withNano(0);
        }

        int minutosTotales = fechaHora.getHour() * 60 + fechaHora.getMinute();
        int resto = minutosTotales % bloqueMinutos;
        int abajo = minutosTotales - resto;
        int arriba = abajo + bloqueMinutos;
        int elegido = (minutosTotales - abajo) < (arriba - minutosTotales) ? abajo : arriba;

        return fechaHora.toLocalDate()
                .atStartOfDay()
                .plusMinutes(elegido)
                .withSecond(0)
                .withNano(0);
    }

    private boolean legajoCoincide(MarcacionRaw marcacion, String legajoBuscado) {
        String legajoMarcacion = normalizarLegajo(obtenerLegajoMarcacion(marcacion));
        String legajoEntrada = normalizarLegajo(legajoBuscado);

        return legajoMarcacion != null && legajoMarcacion.equalsIgnoreCase(legajoEntrada);
    }

    private String normalizarLegajo(String legajo) {
        if (legajo == null) {
            return null;
        }

        String limpio = legajo.trim().replaceFirst("^0+", "");

        if (limpio.isEmpty()) {
            return "0";
        }

        return limpio;
    }

    private String obtenerLegajoMarcacion(MarcacionRaw marcacion) {
        Object valor = leerValor(marcacion,
                "getLegajo", "getIdEmpleado", "getEmpleadoId", "getEnNo", "getNroLegajo",
                "legajo", "idEmpleado", "empleadoId", "enNo", "nroLegajo");

        return valor != null ? String.valueOf(valor) : null;
    }

    private LocalDateTime obtenerFechaHoraMarcacion(MarcacionRaw marcacion) {
        Object valor = leerValor(marcacion,
                "getFechaHora", "getFechaHoraMarca", "getFechaMarcacion", "getFechaHoraRegistro",
                "fechaHora", "fechaHoraMarca", "fechaMarcacion", "fechaHoraRegistro");

        return convertirALocalDateTime(valor);
    }

    private LocalTime obtenerHoraEntrada(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getHoraEntrada", "getHoraDesde", "getDesde", "getHoraIngreso",
                "horaEntrada", "horaDesde", "desde", "horaIngreso");

        return convertirALocalTime(valor);
    }

    private LocalTime obtenerHoraSalida(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getHoraSalida", "getHoraHasta", "getHasta", "getHoraEgreso",
                "horaSalida", "horaHasta", "hasta", "horaEgreso");

        return convertirALocalTime(valor);
    }

    private int obtenerToleranciaIngreso(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getToleranciaIngresoMin", "getToleranciaEntrada", "getToleranciaIngreso", "getIngresoToleranciaMin",
                "toleranciaIngresoMin", "toleranciaEntrada", "toleranciaIngreso", "ingresoToleranciaMin");

        return convertirAEntero(valor, 0);
    }

    private int obtenerToleranciaEgreso(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getToleranciaEgresoMin", "getToleranciaSalida", "getToleranciaEgreso", "getSalidaToleranciaMin",
                "toleranciaEgresoMin", "toleranciaSalida", "toleranciaEgreso", "salidaToleranciaMin");

        return convertirAEntero(valor, 0);
    }

    private int obtenerRedondeo(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getRedondeoMin", "getRedondeoMinutos", "getMinutosRedondeo", "getRedondeo",
                "redondeoMin", "redondeoMinutos", "minutosRedondeo", "redondeo");

        return convertirAEntero(valor, 0);
    }

    private Integer obtenerDiaSemana(TurnoVentana ventana) {
        Object valor = leerValor(ventana,
                "getDiaSemana", "getDia", "getDiaSem",
                "diaSemana", "dia", "diaSem");

        return convertirAEnteroNullable(valor);
    }

    private Object leerValor(Object objeto, String... posiblesNombres) {
        if (objeto == null || posiblesNombres == null) {
            return null;
        }

        Class<?> clazz = objeto.getClass();

        for (String nombre : posiblesNombres) {
            try {
                Method m = clazz.getMethod(nombre);
                return m.invoke(objeto);
            } catch (Exception ignored) {
            }
        }

        for (String nombre : posiblesNombres) {
            try {
                Field f = clazz.getDeclaredField(nombre);
                f.setAccessible(true);
                return f.get(objeto);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private LocalDateTime convertirALocalDateTime(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof LocalDateTime ldt) {
            return ldt;
        }

        if (valor instanceof LocalDate ld) {
            return ld.atStartOfDay();
        }

        if (valor instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        }

        if (valor instanceof java.util.Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        if (valor instanceof String s) {
            try {
                return LocalDateTime.parse(s);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private LocalTime convertirALocalTime(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof LocalTime lt) {
            return lt;
        }

        if (valor instanceof LocalDateTime ldt) {
            return ldt.toLocalTime();
        }

        if (valor instanceof java.sql.Time t) {
            return t.toLocalTime();
        }

        if (valor instanceof String s) {
            try {
                return LocalTime.parse(s);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private int convertirAEntero(Object valor, int porDefecto) {
        Integer n = convertirAEnteroNullable(valor);
        return n != null ? n : porDefecto;
    }

    private Integer convertirAEnteroNullable(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof Integer i) {
            return i;
        }

        if (valor instanceof Long l) {
            return l.intValue();
        }

        if (valor instanceof Short s) {
            return (int) s;
        }

        if (valor instanceof Byte b) {
            return (int) b;
        }

        if (valor instanceof Number n) {
            return n.intValue();
        }

        if (valor instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}