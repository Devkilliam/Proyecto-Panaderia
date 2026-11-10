
package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.IngredienteDAO;
import com.panaderiaaurora.modelo.Ingrediente;
import com.panaderiaaurora.vista.IngredienteVista;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class IngredienteControlador {

    private final IngredienteDAO dao;
    private final IngredienteVista vista;

    public IngredienteControlador(IngredienteDAO dao, IngredienteVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> limpiar());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> cargar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });

        cargar();
    }

    private Ingrediente leerFormulario() {
        String nombre = vista.txtNombre.getText().trim();
        String unidad = vista.txtUnidad.getText().trim();
        BigDecimal stock = new BigDecimal(
            vista.txtStockMinimo.getText().trim()
        );

        if (nombre.isEmpty() || unidad.isEmpty()
                || stock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Completa correctamente todos los campos."
            );
        }

        return new Ingrediente(null, nombre, unidad, stock);
    }

    private void guardar() {
        try {
            Ingrediente ingrediente = leerFormulario();
            long id = dao.crearIngrediente(ingrediente);

            dao.confirmarCambios();

            JOptionPane.showMessageDialog(
                vista, "Ingrediente creado correctamente. ID: " + id
            );

            limpiar();
            cargar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Selecciona un ingrediente."
                );
            }

            Ingrediente ingrediente = leerFormulario();
            ingrediente.setIdIngrediente(
                Long.parseLong(vista.txtId.getText())
            );

            dao.actualizarIngrediente(ingrediente);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(
                vista, "Ingrediente actualizado correctamente."
            );

            limpiar();
            cargar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Selecciona un ingrediente."
                );
            }

            int opcion = JOptionPane.showConfirmDialog(
                vista,
                "¿Deseas eliminar este ingrediente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {
                long id = Long.parseLong(vista.txtId.getText());

                dao.eliminarIngrediente(id);
                dao.confirmarCambios();

                JOptionPane.showMessageDialog(
                    vista, "Ingrediente eliminado correctamente."
                );

                limpiar();
                cargar();
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cargar() {
        try {
            vista.modelo.setRowCount(0);

            for (Ingrediente ingrediente : dao.consultarIngredientes()) {
                vista.modelo.addRow(new Object[]{
                    ingrediente.getIdIngrediente(),
                    ingrediente.getNombre(),
                    ingrediente.getUnidadMedida(),
                    ingrediente.getStockMinimo()
                });
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void seleccionar() {
        int fila = vista.tabla.getSelectedRow();

        if (fila >= 0) {
            int f = vista.tabla.convertRowIndexToModel(fila);

            vista.txtId.setText(
                vista.modelo.getValueAt(f, 0).toString()
            );
            vista.txtNombre.setText(
                vista.modelo.getValueAt(f, 1).toString()
            );
            vista.txtUnidad.setText(
                vista.modelo.getValueAt(f, 2).toString()
            );
            vista.txtStockMinimo.setText(
                vista.modelo.getValueAt(f, 3).toString()
            );
        }
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtNombre.setText("");
        vista.txtUnidad.setText("");
        vista.txtStockMinimo.setText("");
        vista.tabla.clearSelection();
    }

    private void mostrarError(Exception ex) {
        try {
            dao.revertirCambios();
        } catch (SQLException error) {
            ex.addSuppressed(error);
        }

        JOptionPane.showMessageDialog(
            vista,
            ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
