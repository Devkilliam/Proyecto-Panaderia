
package com.panaderiaaurora.controlador;

import com.panaderiaaurora.dao.ClienteDAO;
import com.panaderiaaurora.modelo.Cliente;
import com.panaderiaaurora.vista.ClienteVista;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class ClienteControlador {

    private final ClienteDAO dao;
    private final ClienteVista vista;

    public ClienteControlador(ClienteDAO dao, ClienteVista vista) {
        this.dao = dao;
        this.vista = vista;

        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnConsultar.addActionListener(e -> consultar());

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarCliente();
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
            Cliente cliente = leerFormulario();

            dao.crear(cliente);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Cliente registrado correctamente. ID: "
                    + cliente.getIdCliente());

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new Exception("Selecciona un cliente de la tabla.");
            }

            Cliente cliente = leerFormulario();
            cliente.setIdCliente(
                    Long.parseLong(vista.txtId.getText().trim()));

            dao.actualizar(cliente);
            dao.confirmarCambios();

            JOptionPane.showMessageDialog(vista,
                    "Cliente actualizado correctamente.");

            nuevo();
            consultar();

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            if (vista.txtId.getText().trim().isEmpty()) {
                throw new Exception("Selecciona un cliente de la tabla.");
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Deseas eliminar este cliente?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(vista.txtId.getText().trim());

                dao.eliminar(id);
                dao.confirmarCambios();

                JOptionPane.showMessageDialog(vista,
                        "Cliente eliminado correctamente.");

                nuevo();
                consultar();
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void consultar() {
        try {
            List<Cliente> clientes = dao.consultar(null);

            vista.modeloTabla.setRowCount(0);

            for (Cliente c : clientes) {
                vista.modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getCorreo()
                });
            }

        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarCliente() {
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

    private Cliente leerFormulario() throws Exception {
        String nombre = vista.txtNombre.getText().trim();
        String telefono = vista.txtTelefono.getText().trim();
        String correo = vista.txtCorreo.getText().trim();

        if (nombre.isEmpty()) {
            throw new Exception("El nombre del cliente es obligatorio.");
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

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono.isEmpty() ? null : telefono);
        cliente.setCorreo(correo.isEmpty() ? null : correo);

        return cliente;
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
