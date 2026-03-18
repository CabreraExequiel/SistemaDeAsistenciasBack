package com.palmares.rrhh_backend.dto;

import java.time.LocalDateTime;

public class TramoJornadaResponse {

    private LocalDateTime entradaReal;
    private LocalDateTime salidaReal;
    private LocalDateTime entradaAjustada;
    private LocalDateTime salidaAjustada;
    private Integer minutosTrabajados;

    public TramoJornadaResponse() {
    }

    public LocalDateTime getEntradaReal() {
        return entradaReal;
    }

    public void setEntradaReal(LocalDateTime entradaReal) {
        this.entradaReal = entradaReal;
    }

    public LocalDateTime getSalidaReal() {
        return salidaReal;
    }

    public void setSalidaReal(LocalDateTime salidaReal) {
        this.salidaReal = salidaReal;
    }

    public LocalDateTime getEntradaAjustada() {
        return entradaAjustada;
    }

    public void setEntradaAjustada(LocalDateTime entradaAjustada) {
        this.entradaAjustada = entradaAjustada;
    }

    public LocalDateTime getSalidaAjustada() {
        return salidaAjustada;
    }

    public void setSalidaAjustada(LocalDateTime salidaAjustada) {
        this.salidaAjustada = salidaAjustada;
    }

    public Integer getMinutosTrabajados() {
        return minutosTrabajados;
    }

    public void setMinutosTrabajados(Integer minutosTrabajados) {
        this.minutosTrabajados = minutosTrabajados;
    }
}