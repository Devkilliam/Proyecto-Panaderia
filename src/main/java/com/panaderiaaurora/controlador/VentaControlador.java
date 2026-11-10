
package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.VentaDAO;
import com.panaderiaaurora.modelo.Venta;
import com.panaderiaaurora.vista.VentaVista;

import javax.swing.*;
import java.sql.Date;
import java.util.List;

public class VentaControlador {

    private final VentaDAO dao;
    private final VentaVista vista;

    public VentaControlador(VentaDAO dao, VentaVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> vista.limpiarFormulario());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarVenta();
            }
        });

        consultar();
    }

    private void guardar() {
        try {
            Long idPedido = Long.parseLong(
                vista.txtIdPedido.getText().trim()
            );

            if (idPedido <= 0) {
                throw new IllegalArgumentException("El ID del pedido debe ser mayor que cero.");
            }

            Venta venta = new Venta();
            venta.setIdPedido(idPedido);
            venta.setMedioPago(
                vista.cmbMedioPago.getSelectedItem().toString()
            );

            dao.crear(venta);

            JOptionPane.showMessageDialog(
                vista,
                "Venta registrada correctamente. ID: " + venta.getIdVenta()
            );

            vista.limpiarFormulario();
            consultar();

        } catch (Exception e) {
            mostrarError("No se pudo guardar la venta.", e);
        }
    }

    private void consultar() {
        try {
            String textoId = vista.txtIdVenta.getText().trim();
            Long id = textoId.isEmpty() ? null : Long.parseLong(textoId);

            List<Venta> ventas = dao.consultar(id);

            vista.modeloTabla.setRowCount(0);

            for (Venta venta : ventas) {
                vista.modeloTabla.addRow(new Object[]{
                    venta.getIdVenta(),
                    venta.getIdPedido(),
                    venta.getFechaVenta(),
                    venta.getMedioPago(),
                    venta.getTotal()
                });
            }

        } catch (Exception e) {
            mostrarError("No se pudieron consultar las ventas.", e);
        }
    }

    private void actualizar() {
        try {
            Long idVenta = Long.parseLong(
                vista.txtIdVenta.getText().trim()
            );

            Venta venta = new Venta();
            venta.setIdVenta(idVenta);
            venta.setMedioPago(
                vista.cmbMedioPago.getSelectedItem().toString()
            );

            dao.actualizar(venta);

            JOptionPane.showMessageDialog(
                vista,
                "Venta actualizada correctamente."
            );

            consultar();

        } catch (Exception e) {
            mostrarError("No se pudo actualizar la venta.", e);
        }
    }

    private void eliminar() {
        try {
            Long idVenta = Long.parseLong(
                vista.txtIdVenta.getText().trim()
            );

            int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Deseas eliminar esta venta?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                dao.eliminar(idVenta);

                JOptionPane.showMessageDialog(
                    vista,
                    "Venta eliminada correctamente."
                );

                vista.limpiarFormulario();
                consultar();
            }

        } catch (Exception e) {
            mostrarError("No se pudo eliminar la venta.", e);
        }
    }

    private void seleccionarVenta() {
        int fila = vista.tabla.getSelectedRow();

        if (fila >= 0) {
            vista.txtIdVenta.setText(
                vista.tabla.getValueAt(fila, 0).toString()
            );

            vista.txtIdPedido.setText(
                vista.tabla.getValueAt(fila, 1).toString()
            );

            vista.cmbMedioPago.setSelectedItem(
                vista.tabla.getValueAt(fila, 3).toString()
            );
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(
            vista,
            mensaje + "\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
