
package com.panaderiaaurora.modelo;

import java.math.BigDecimal;

public class Ingrediente {

    private Long idIngrediente;
    private String nombre;
    private String unidadMedida;
    private BigDecimal stockMinimo;

    public Ingrediente() {
    }

    public Ingrediente(Long idIngrediente, String nombre,
            String unidadMedida, BigDecimal stockMinimo) {
        this.idIngrediente = idIngrediente;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    public Long getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Long idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
