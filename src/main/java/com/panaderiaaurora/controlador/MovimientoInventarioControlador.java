package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.MovimientoInventarioDAO;
import com.panaderiaaurora.modelo.MovimientoInventario;
import com.panaderiaaurora.vista.MovimientoInventarioVista;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class MovimientoInventarioControlador {

    private final MovimientoInventarioDAO dao;
    private final MovimientoInventarioVista vista;

    public MovimientoInventarioControlador(
            MovimientoInventarioDAO dao,
            MovimientoInventarioVista vista) {

        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> limpiar());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());
        vista.btnExistencia.addActionListener(e -> verExistencia());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRegistro();
            }
        });

        consultar();
    }

    private Long obtenerIdOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(texto.trim());
    }

    private double obtenerCantidad() {
        double cantidad = Double.parseDouble(
                vista.txtCantidad.getText().trim());

        if (!Double.isFinite(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        return cantidad;
    }

    private void guardar() {
        try {
            MovimientoInventario m = new MovimientoInventario();

            m.setIdIngrediente(Long.parseLong(
                    vista.txtIdIngrediente.getText().trim()));

            m.setIdEmpleado(Long.parseLong(
                    vista.txtIdEmpleado.getText().trim()));

            m.setIdProveedor(obtenerIdOpcional(
                    vista.txtIdProveedor.getText()));

            m.setIdProduccion(obtenerIdOpcional(
                    vista.txtIdProduccion.getText()));

            m.setTipo(vista.cmbTipo.getSelectedItem().toString());
            m.setCantidad(obtenerCantidad());

            Long id = dao.crear(m);

            JOptionPane.showMessageDialog(vista,
                    "Movimiento registrado correctamente. ID: " + id);

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            mostrarAviso("Verifica los IDs y la cantidad ingresada.");
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtIdMovimiento.getText().trim().isEmpty()) {
                mostrarAviso("Selecciona un movimiento de la tabla.");
                return;
            }

            Long id = Long.parseLong(
                    vista.txtIdMovimiento.getText().trim());

            String tipo = vista.cmbTipo.getSelectedItem().toString();
            Double cantidad = obtenerCantidad();

            dao.actualizar(id, tipo, cantidad);

            JOptionPane.showMessageDialog(vista,
                    "Movimiento actualizado correctamente.");

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            mostrarAviso("Verifica el ID y la cantidad.");
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtIdMovimiento.getText().trim().isEmpty()) {
                mostrarAviso("Selecciona un movimiento de la tabla.");
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar este movimiento de inventario?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            Long id = Long.parseLong(
                    vista.txtIdMovimiento.getText().trim());

            dao.eliminar(id);

            JOptionPane.showMessageDialog(vista,
                    "Movimiento eliminado correctamente.");

            limpiar();
            consultar();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void consultar() {
        try {
            List<MovimientoInventario> lista = dao.consultar(null);

            vista.modelo.setRowCount(0);

            for (MovimientoInventario m : lista) {
                vista.modelo.addRow(new Object[]{
                    m.getIdMovimiento(),
                    m.getIdIngrediente(),
                    m.getIdEmpleado(),
                    m.getIdProveedor(),
                    m.getIdProduccion(),
                    m.getTipo(),
                    m.getCantidad(),
                    m.getFechaMovimiento()
                });
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void verExistencia() {
        try {
            String texto = vista.txtIdIngrediente.getText().trim();

            if (texto.isEmpty()) {
                mostrarAviso("Ingresa el ID del ingrediente.");
                return;
            }

            Long idIngrediente = Long.parseLong(texto);
            Double existencia = dao.obtenerExistencia(idIngrediente);

            JOptionPane.showMessageDialog(vista,
                    "Existencia del ingrediente " + idIngrediente
                    + ": " + existencia);

        } catch (NumberFormatException e) {
            mostrarAviso("Ingresa un ID de ingrediente válido.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void seleccionarRegistro() {
        int fila = vista.tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        fila = vista.tabla.convertRowIndexToModel(fila);

        vista.txtIdMovimiento.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 0)));

        vista.txtIdIngrediente.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 1)));

        vista.txtIdEmpleado.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 2)));

        Object proveedor = vista.modelo.getValueAt(fila, 3);
        vista.txtIdProveedor.setText(
                proveedor == null ? "" : proveedor.toString());

        Object produccion = vista.modelo.getValueAt(fila, 4);
        vista.txtIdProduccion.setText(
                produccion == null ? "" : produccion.toString());

        vista.cmbTipo.setSelectedItem(
                vista.modelo.getValueAt(fila, 5).toString());

        vista.txtCantidad.setText(
                vista.modelo.getValueAt(fila, 6).toString());
    }

    private void limpiar() {
        vista.tabla.clearSelection();

        vista.txtIdMovimiento.setText("");
        vista.txtIdIngrediente.setText("");
        vista.txtIdEmpleado.setText("");
        vista.txtIdProveedor.setText("");
        vista.txtIdProduccion.setText("");
        vista.txtCantidad.setText("");

        vista.cmbTipo.setSelectedItem("ENTRADA");
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje);
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(
                vista,
                "Error de base de datos:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}