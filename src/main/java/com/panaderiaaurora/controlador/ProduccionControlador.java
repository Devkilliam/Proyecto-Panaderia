package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.ProduccionDAO;
import com.panaderiaaurora.modelo.Produccion;
import com.panaderiaaurora.vista.ProduccionVista;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class ProduccionControlador {

    private final ProduccionDAO dao;
    private final ProduccionVista vista;

    public ProduccionControlador(ProduccionDAO dao, ProduccionVista vista) {
        this.dao = dao;
        this.vista = vista;

        this.vista.btnNuevo.addActionListener(e -> limpiar());
        this.vista.btnGuardar.addActionListener(e -> guardar());
        this.vista.btnActualizar.addActionListener(e -> actualizar());
        this.vista.btnEliminar.addActionListener(e -> eliminar());
        this.vista.btnConsultar.addActionListener(e -> consultar());

        this.vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRegistro();
            }
        });

        consultar();
    }

    private void guardar() {
        try {
            Produccion p = new Produccion();

            p.setIdReceta(Long.parseLong(vista.txtIdReceta.getText().trim()));
            p.setIdEmpleado(Long.parseLong(vista.txtIdEmpleado.getText().trim()));
            p.setLotesProducidos(Integer.parseInt(vista.txtLotes.getText().trim()));

            Long id = dao.crear(p);

            JOptionPane.showMessageDialog(vista,
                    "Producción creada correctamente. ID: " + id);

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "Verifica que los IDs y los lotes sean números válidos.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtIdProduccion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Selecciona una producción de la tabla.");
                return;
            }

            Long id = Long.parseLong(vista.txtIdProduccion.getText().trim());
            Integer lotes = Integer.parseInt(vista.txtLotes.getText().trim());
            String estado = vista.cmbEstado.getSelectedItem().toString();

            dao.actualizar(id, lotes, estado);

            JOptionPane.showMessageDialog(vista,
                    "Producción actualizada correctamente.");

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "El número de lotes debe ser válido.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtIdProduccion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Selecciona una producción de la tabla.");
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar esta producción?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            Long id = Long.parseLong(vista.txtIdProduccion.getText().trim());

            dao.eliminar(id);

            JOptionPane.showMessageDialog(vista,
                    "Producción eliminada correctamente.");

            limpiar();
            consultar();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void consultar() {
        try {
            List<Produccion> lista = dao.consultar(null);

            vista.modelo.setRowCount(0);

            for (Produccion p : lista) {
                vista.modelo.addRow(new Object[]{
                    p.getIdProduccion(),
                    p.getIdReceta(),
                    p.getIdEmpleado(),
                    p.getFechaProduccion(),
                    p.getLotesProducidos(),
                    p.getEstado()
                });
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void seleccionarRegistro() {
        int fila = vista.tabla.getSelectedRow();

        if (fila >= 0) {
            vista.txtIdProduccion.setText(
                    vista.modelo.getValueAt(fila, 0).toString());

            vista.txtIdReceta.setText(
                    vista.modelo.getValueAt(fila, 1).toString());

            vista.txtIdEmpleado.setText(
                    vista.modelo.getValueAt(fila, 2).toString());

            vista.txtLotes.setText(
                    vista.modelo.getValueAt(fila, 4).toString());

            vista.cmbEstado.setSelectedItem(
                    vista.modelo.getValueAt(fila, 5).toString());
        }
    }

    private void limpiar() {
        vista.txtIdProduccion.setText("");
        vista.txtIdReceta.setText("");
        vista.txtIdEmpleado.setText("");
        vista.txtLotes.setText("");
        vista.cmbEstado.setSelectedItem("PLANIFICADA");
        vista.tabla.clearSelection();
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(vista,
                "Error de base de datos:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}