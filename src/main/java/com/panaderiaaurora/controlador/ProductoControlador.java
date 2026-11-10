package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.ProductoDAO;
import com.panaderiaaurora.modelo.Producto;
import com.panaderiaaurora.vista.ProductoVista;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductoControlador {

    private final ProductoDAO dao;
    private final ProductoVista vista;

    public ProductoControlador(ProductoDAO dao, ProductoVista vista) {
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

    private Producto leerFormulario() {
        Long idCategoria = Long.parseLong(vista.txtCategoria.getText().trim());
        String nombre = vista.txtNombre.getText().trim();
        BigDecimal precio = new BigDecimal(vista.txtPrecio.getText().trim());
        int activo = vista.cmbActivo.getSelectedIndex() == 0 ? 1 : 0;

        if (nombre.isEmpty() || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Completa los datos correctamente.");
        }

        return new Producto(null, idCategoria, nombre, precio, activo);
    }

    private void guardar() {
        try {
            Producto p = leerFormulario();
            long id = dao.crearProducto(p);

            dao.confirmarCambios();

            JOptionPane.showMessageDialog(
                    vista, "Producto creado correctamente. ID: " + id
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
                throw new IllegalArgumentException("Selecciona un producto.");
            }

            Producto p = leerFormulario();
            p.setIdProducto(Long.parseLong(vista.txtId.getText()));

            dao.actualizarProducto(p);

            // Guardar definitivamente los cambios en Oracle
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista, "Producto actualizado correctamente.");

            limpiar();
            cargar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Selecciona un producto.");
            }

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar este producto?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {

                long id = Long.parseLong(vista.txtId.getText());

                dao.eliminarProducto(id);

                // Guardar definitivamente los cambios en Oracle
                dao.confirmarCambios();

                JOptionPane.showMessageDialog(vista, "Producto eliminado correctamente.");

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

            for (Producto p : dao.consultarProductos()) {
                vista.modelo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getIdCategoria(),
                    p.getNombre(),
                    p.getPrecioVenta(),
                    p.getActivo() == 1 ? "Sí" : "No"
                });
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void seleccionar() {
        int fila = vista.tabla.getSelectedRow();

        if (fila >= 0) {
            vista.txtId.setText(vista.modelo.getValueAt(fila, 0).toString());
            vista.txtCategoria.setText(vista.modelo.getValueAt(fila, 1).toString());
            vista.txtNombre.setText(vista.modelo.getValueAt(fila, 2).toString());
            vista.txtPrecio.setText(vista.modelo.getValueAt(fila, 3).toString());
            vista.cmbActivo.setSelectedIndex(
                    "Sí".equals(vista.modelo.getValueAt(fila, 4).toString()) ? 0 : 1);
        }
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtCategoria.setText("");
        vista.txtNombre.setText("");
        vista.txtPrecio.setText("");
        vista.cmbActivo.setSelectedIndex(0);
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
