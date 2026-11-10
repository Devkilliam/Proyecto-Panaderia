
package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.ProveedorDAO;
import com.panaderiaaurora.modelo.Proveedor;
import com.panaderiaaurora.vista.ProveedorVista;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class ProveedorControlador {

    private final ProveedorDAO dao;
    private final ProveedorVista vista;

    public ProveedorControlador(ProveedorDAO dao, ProveedorVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProveedor();
            }
        });

        consultar();
    }

    private void nuevo() {
        vista.txtId.setText("");
        vista.txtNombre.setText("");
        vista.txtTelefono.setText("");
        vista.txtCorreo.setText("");
        vista.txtNombre.requestFocus();
    }

    private void guardar() {
        try {
            Proveedor proveedor = leerFormulario();

            dao.crear(proveedor);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Proveedor registrado correctamente. ID: "
                    + proveedor.getIdProveedor());

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new Exception("Selecciona un proveedor de la tabla.");
            }

            Proveedor proveedor = leerFormulario();
            proveedor.setIdProveedor(
                    Long.parseLong(vista.txtId.getText().trim()));

            dao.actualizar(proveedor);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Proveedor actualizado correctamente.");

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new Exception("Selecciona un proveedor de la tabla.");
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar este proveedor?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(vista.txtId.getText().trim());

                dao.eliminar(id);
                dao.confirmarCambios();

                JOptionPane.showMessageDialog(vista,
                        "Proveedor eliminado correctamente.");

                nuevo();
                consultar();
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void consultar() {
        try {
            List<Proveedor> proveedores = dao.consultar(null);

            vista.modeloTabla.setRowCount(0);

            for (Proveedor p : proveedores) {
                vista.modeloTabla.addRow(new Object[]{
                    p.getIdProveedor(),
                    p.getNombre(),
                    p.getTelefono(),
                    p.getCorreo()
                });
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarProveedor() {
        int fila = vista.tabla.getSelectedRow();

        if (fila >= 0) {
            vista.txtId.setText(
                    vista.modeloTabla.getValueAt(fila, 0).toString());

            vista.txtNombre.setText(
                    vista.modeloTabla.getValueAt(fila, 1).toString());

            Object telefono = vista.modeloTabla.getValueAt(fila, 2);
            vista.txtTelefono.setText(
                    telefono == null ? "" : telefono.toString());

            Object correo = vista.modeloTabla.getValueAt(fila, 3);
            vista.txtCorreo.setText(
                    correo == null ? "" : correo.toString());
        }
    }

    private Proveedor leerFormulario() throws Exception {
        String nombre = vista.txtNombre.getText().trim();
        String telefono = vista.txtTelefono.getText().trim();
        String correo = vista.txtCorreo.getText().trim();

        if (nombre.isEmpty()) {
            throw new Exception("El nombre del proveedor es obligatorio.");
        }

        if (nombre.length() > 150) {
            throw new Exception("El nombre no puede superar 150 caracteres.");
        }

        if (telefono.length() > 25) {
            throw new Exception("El teléfono no puede superar 25 caracteres.");
        }

        if (correo.length() > 150) {
            throw new Exception("El correo no puede superar 150 caracteres.");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre);
        proveedor.setTelefono(telefono.isEmpty() ? null : telefono);
        proveedor.setCorreo(correo.isEmpty() ? null : correo);

        return proveedor;
    }

    private void mostrarError(Exception ex) {
        try {
            dao.revertirCambios();
        } catch (SQLException ignored) {
        }

        JOptionPane.showMessageDialog(
                vista,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
