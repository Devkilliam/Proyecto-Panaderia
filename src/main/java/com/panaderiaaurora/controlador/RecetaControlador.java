package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.RecetaDAO;
import com.panaderiaaurora.modelo.Receta;
import com.panaderiaaurora.vista.RecetaVista;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class RecetaControlador {

    private final RecetaDAO dao;
    private final RecetaVista vista;

    public RecetaControlador(RecetaDAO dao, RecetaVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> limpiar());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRegistro();
            }
        });

        consultar();
    }

    private Receta obtenerDatos() {
        Receta receta = new Receta();

        receta.setIdProducto(Long.parseLong(
                vista.txtIdProducto.getText().trim()));

        receta.setVersionReceta(Integer.parseInt(
                vista.txtVersionReceta.getText().trim()));

        receta.setRendimiento(Double.parseDouble(
                vista.txtRendimiento.getText().trim()));

        if (receta.getIdProducto() <= 0
                || receta.getVersionReceta() <= 0
                || !Double.isFinite(receta.getRendimiento())
                || receta.getRendimiento() <= 0) {
            throw new IllegalArgumentException(
                    "El producto, la versión y el rendimiento deben ser mayores que cero.");
        }

        return receta;
    }

    private void guardar() {
        try {
            Receta receta = obtenerDatos();

            Long id = dao.crear(receta);

            JOptionPane.showMessageDialog(vista,
                    "Receta creada correctamente. ID: " + id);

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            mostrarAviso("Ingresa valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtIdReceta.getText().trim().isEmpty()) {
                mostrarAviso("Selecciona una receta de la tabla.");
                return;
            }

            Receta receta = obtenerDatos();

            receta.setIdReceta(Long.parseLong(
                    vista.txtIdReceta.getText().trim()));

            dao.actualizar(receta);

            JOptionPane.showMessageDialog(vista,
                    "Receta actualizada correctamente.");

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            mostrarAviso("Verifica los valores numéricos.");
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtIdReceta.getText().trim().isEmpty()) {
                mostrarAviso("Selecciona una receta de la tabla.");
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar esta receta?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            Long id = Long.parseLong(
                    vista.txtIdReceta.getText().trim());

            dao.eliminar(id);

            JOptionPane.showMessageDialog(vista,
                    "Receta eliminada correctamente.");

            limpiar();
            consultar();

        } catch (NumberFormatException e) {
            mostrarAviso("El ID de receta no es válido.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void consultar() {
        try {
            List<Receta> recetas = dao.consultar(null);

            vista.modelo.setRowCount(0);

            for (Receta receta : recetas) {
                vista.modelo.addRow(new Object[]{
                    receta.getIdReceta(),
                    receta.getIdProducto(),
                    receta.getVersionReceta(),
                    receta.getRendimiento()
                });
            }

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

        vista.txtIdReceta.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 0)));

        vista.txtIdProducto.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 1)));

        vista.txtVersionReceta.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 2)));

        vista.txtRendimiento.setText(
                String.valueOf(vista.modelo.getValueAt(fila, 3)));
    }

    private void limpiar() {
        vista.tabla.clearSelection();

        vista.txtIdReceta.setText("");
        vista.txtIdProducto.setText("");
        vista.txtVersionReceta.setText("");
        vista.txtRendimiento.setText("");
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