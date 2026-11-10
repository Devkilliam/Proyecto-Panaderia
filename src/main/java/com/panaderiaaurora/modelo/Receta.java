package com.panaderiaaurora.modelo;

public class Receta {

    private Long idReceta;
    private Long idProducto;
    private Integer versionReceta;
    private Double rendimiento;

    public Receta() {
    }

    public Long getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Long idReceta) {
        this.idReceta = idReceta;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getVersionReceta() {
        return versionReceta;
    }

    public void setVersionReceta(Integer versionReceta) {
        this.versionReceta = versionReceta;
    }

    public Double getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(Double rendimiento) {
        this.rendimiento = rendimiento;
    }
}