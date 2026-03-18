package com.palmares.rrhh_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JornadaDiariaResponse {

    private String legajo;
    private LocalDate fecha;

    private LocalDateTime primeraEntradaReal;
    private LocalDateTime primeraEntradaAjustada;

    private LocalDateTime ultimaSalidaReal;
    private LocalDateTime ultimaSalidaAjustada;

    private Integer minutosTrabajados;
    private Integer cantidadMarcas;
    private Integer cantidadTramos;

    private Boolean tieneMarcasIntermedias;
    private Boolean tieneInconsistencias;
    private Boolean cierreAutomaticoPermitido;

    private String estado;

    private List<String> observaciones = new ArrayList<>();
    private List<TramoJornadaResponse> tramos = new ArrayList<>();

    public JornadaDiariaResponse() {
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getPrimeraEntradaReal() {
        return primeraEntradaReal;
    }

    public void setPrimeraEntradaReal(LocalDateTime primeraEntradaReal) {
        this.primeraEntradaReal = primeraEntradaReal;
    }

    public LocalDateTime getPrimeraEntradaAjustada() {
        return primeraEntradaAjustada;
    }

    public void setPrimeraEntradaAjustada(LocalDateTime primeraEntradaAjustada) {
        this.primeraEntradaAjustada = primeraEntradaAjustada;
    }

    public LocalDateTime getUltimaSalidaReal() {
        return ultimaSalidaReal;
    }

    public void setUltimaSalidaReal(LocalDateTime ultimaSalidaReal) {
        this.ultimaSalidaReal = ultimaSalidaReal;
    }

    public LocalDateTime getUltimaSalidaAjustada() {
        return ultimaSalidaAjustada;
    }

    public void setUltimaSalidaAjustada(LocalDateTime ultimaSalidaAjustada) {
        this.ultimaSalidaAjustada = ultimaSalidaAjustada;
    }

    public Integer getMinutosTrabajados() {
        return minutosTrabajados;
    }

    public void setMinutosTrabajados(Integer minutosTrabajados) {
        this.minutosTrabajados = minutosTrabajados;
    }

    public Integer getCantidadMarcas() {
        return cantidadMarcas;
    }

    public void setCantidadMarcas(Integer cantidadMarcas) {
        this.cantidadMarcas = cantidadMarcas;
    }

    public Integer getCantidadTramos() {
        return cantidadTramos;
    }

    public void setCantidadTramos(Integer cantidadTramos) {
        this.cantidadTramos = cantidadTramos;
    }

    public Boolean getTieneMarcasIntermedias() {
        return tieneMarcasIntermedias;
    }

    public void setTieneMarcasIntermedias(Boolean tieneMarcasIntermedias) {
        this.tieneMarcasIntermedias = tieneMarcasIntermedias;
    }

    public Boolean getTieneInconsistencias() {
        return tieneInconsistencias;
    }

    public void setTieneInconsistencias(Boolean tieneInconsistencias) {
        this.tieneInconsistencias = tieneInconsistencias;
    }

    public Boolean getCierreAutomaticoPermitido() {
        return cierreAutomaticoPermitido;
    }

    public void setCierreAutomaticoPermitido(Boolean cierreAutomaticoPermitido) {
        this.cierreAutomaticoPermitido = cierreAutomaticoPermitido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<String> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(List<String> observaciones) {
        this.observaciones = observaciones;
    }

    public List<TramoJornadaResponse> getTramos() {
        return tramos;
    }

    public void setTramos(List<TramoJornadaResponse> tramos) {
        this.tramos = tramos;
    }
}