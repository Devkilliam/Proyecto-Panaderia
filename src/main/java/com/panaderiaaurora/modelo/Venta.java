
package com.panaderiaaurora.modelo;

import java.sql.Date;

public class Venta {

    private Long idVenta;
    private Long idPedido;
    private Date fechaVenta;
    private String medioPago;
    private Double total;

    public Venta() {
    }

    public Venta(Long idVenta, Long idPedido, Date fechaVenta,
                 String medioPago, Double total) {
        this.idVenta = idVenta;
        this.idPedido = idPedido;
        this.fechaVenta = fechaVenta;
        this.medioPago = medioPago;
        this.total = total;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
