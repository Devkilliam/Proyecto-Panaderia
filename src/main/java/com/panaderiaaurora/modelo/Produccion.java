package com.panaderiaaurora.modelo;

import java.sql.Date;

public class Produccion {

    private Long idProduccion;
    private Long idReceta;
    private Long idEmpleado;
    private Date fechaProduccion;
    private Integer lotesProducidos;
    private String estado;

    public Produccion() {
    }

    public Produccion(Long idProduccion, Long idReceta,
            Long idEmpleado, Date fechaProduccion,
            Integer lotesProducidos, String estado) {

        this.idProduccion = idProduccion;
        this.idReceta = idReceta;
        this.idEmpleado = idEmpleado;
        this.fechaProduccion = fechaProduccion;
        this.lotesProducidos = lotesProducidos;
        this.estado = estado;
    }

    public Long getIdProduccion() {
        return idProduccion;
    }

    public void setIdProduccion(Long idProduccion) {
        this.idProduccion = idProduccion;
    }

    public Long getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Long idReceta) {
        this.idReceta = idReceta;
    }

    public Long getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Date getFechaProduccion() {
        return fechaProduccion;
    }

    public void setFechaProduccion(Date fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }

    public Integer getLotesProducidos() {
        return lotesProducidos;
    }

    public void setLotesProducidos(Integer lotesProducidos) {
        this.lotesProducidos = lotesProducidos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}