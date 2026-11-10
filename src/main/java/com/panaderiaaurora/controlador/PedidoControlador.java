
package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.PedidoDAO;
import com.panaderiaaurora.modelo.Pedido;
import com.panaderiaaurora.vista.PedidoVista;

import javax.swing.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PedidoControlador {

    private final PedidoDAO dao;
    private final PedidoVista vista;

    public PedidoControlador(PedidoDAO dao, PedidoVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && vista.tabla.getSelectedRow() >= 0) {
                seleccionarPedido();
            }
        });

        consultar();
    }

    private void nuevo() {
        vista.txtId.setText("");
        vista.txtCliente.setText("");
        vista.txtEmpleado.setText("");
        vista.txtFechaPedido.setText("");
        vista.txtFechaEntrega.setText("");
        vista.cmbTipo.setSelectedItem("MOSTRADOR");
        vista.cmbEstado.setSelectedItem("PENDIENTE");
        vista.tabla.clearSelection();
        vista.txtCliente.requestFocus();
    }

    private void guardar() {
        try {
            Pedido pedido = leerFormulario();

            dao.crear(pedido);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Pedido registrado correctamente. ID: "
                    + pedido.getIdPedido());

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError("guardar el pedido", ex);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Selecciona un pedido de la tabla.");
                return;
            }

            Pedido pedido = leerFormulario();
            pedido.setIdPedido(Long.parseLong(vista.txtId.getText()));

            dao.actualizar(pedido);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Pedido actualizado correctamente.");

            consultar();
            nuevo();

        } catch (Exception ex) {
            mostrarError("actualizar el pedido", ex);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Selecciona un pedido de la tabla.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar este pedido?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            Long id = Long.parseLong(vista.txtId.getText());

            dao.eliminar(id);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Pedido eliminado correctamente.");

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError("eliminar el pedido", ex);
        }
    }

    private void consultar() {
        try {
            List<Pedido> pedidos = dao.consultar(null);
            vista.modeloTabla.setRowCount(0);

            for (Pedido p : pedidos) {
                vista.modeloTabla.addRow(new Object[]{
                    p.getIdPedido(),
                    p.getIdCliente(),
                    p.getIdEmpleado(),
                    p.getFechaPedido(),
                    p.getFechaEntrega(),
                    p.getTipo(),
                    p.getEstado()
                });
            }

        } catch (Exception ex) {
            mostrarError("consultar los pedidos", ex);
        }
    }

    private void seleccionarPedido() {
        int fila = vista.tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int modelo = vista.tabla.convertRowIndexToModel(fila);

        vista.txtId.setText(String.valueOf(
                vista.modeloTabla.getValueAt(modelo, 0)));

        vista.txtCliente.setText(String.valueOf(
                vista.modeloTabla.getValueAt(modelo, 1)));

        vista.txtEmpleado.setText(String.valueOf(
                vista.modeloTabla.getValueAt(modelo, 2)));

        Object fechaPedido = vista.modeloTabla.getValueAt(modelo, 3);
        vista.txtFechaPedido.setText(
                fechaPedido == null ? "" : fechaPedido.toString());

        Object fechaEntrega = vista.modeloTabla.getValueAt(modelo, 4);
        vista.txtFechaEntrega.setText(
                fechaEntrega == null ? "" : fechaEntrega.toString());

        vista.cmbTipo.setSelectedItem(
                vista.modeloTabla.getValueAt(modelo, 5));

        vista.cmbEstado.setSelectedItem(
                vista.modeloTabla.getValueAt(modelo, 6));
    }

    private Pedido leerFormulario() {
        String cliente = vista.txtCliente.getText().trim();
        String empleado = vista.txtEmpleado.getText().trim();

        if (cliente.isEmpty() || empleado.isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del cliente y del empleado son obligatorios.");
        }

        Long idCliente = Long.parseLong(cliente);
        Long idEmpleado = Long.parseLong(empleado);

        if (idCliente <= 0 || idEmpleado <= 0) {
            throw new IllegalArgumentException(
                    "Los ID deben ser números mayores que cero.");
        }

        String textoFecha = vista.txtFechaEntrega.getText().trim();
        Date fechaEntrega = null;

        if (!textoFecha.isEmpty()) {
            try {
                fechaEntrega = Date.valueOf(LocalDate.parse(textoFecha));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        "La fecha debe tener formato AAAA-MM-DD.");
            }
        }

        String tipo = (String) vista.cmbTipo.getSelectedItem();
        String estado = (String) vista.cmbEstado.getSelectedItem();

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar el tipo.");
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);
        pedido.setIdEmpleado(idEmpleado);
        pedido.setFechaEntrega(fechaEntrega);
        pedido.setTipo(tipo);
        pedido.setEstado(estado);

        return pedido;
    }

    private void mostrarError(String operacion, Exception ex) {
        try {
            dao.revertirCambios();
        } catch (SQLException error) {
            ex.addSuppressed(error);
        }

        JOptionPane.showMessageDialog(
                vista,
                "No pudimos " + operacion + ".\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
