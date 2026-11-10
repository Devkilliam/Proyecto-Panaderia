
package com.panaderiaaurora.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentaVista extends JPanel {

    public JTextField txtIdVenta;
    public JTextField txtIdPedido;

    public JComboBox<String> cmbMedioPago;

    public JButton btnNuevo;
    public JButton btnGuardar;
    public JButton btnActualizar;
    public JButton btnEliminar;
    public JButton btnConsultar;

    public JTable tabla;
    public DefaultTableModel modeloTabla;

    public VentaVista() {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la venta"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdVenta = new JTextField(15);
        txtIdVenta.setEditable(false);

        txtIdPedido = new JTextField(15);

        cmbMedioPago = new JComboBox<>(new String[]{
            "EFECTIVO",
            "TARJETA",
            "TRANSFERENCIA",
            "SINPE MOVIL"
        });

        agregarCampo(formulario, gbc, 0, "ID Venta:", txtIdVenta);
        agregarCampo(formulario, gbc, 1, "ID Pedido:", txtIdPedido);
        agregarCampo(formulario, gbc, 2, "Medio de pago:", cmbMedioPago);

        JPanel botones = new JPanel(new FlowLayout());

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnConsultar = new JButton("Consultar");

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        modeloTabla = new DefaultTableModel(
            new String[]{
                "ID Venta",
                "ID Pedido",
                "Fecha Venta",
                "Medio de Pago",
                "Total"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void agregarCampo(
        JPanel panel,
        GridBagConstraints gbc,
        int fila,
        String etiqueta,
        JComponent campo
    ) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    public void limpiarFormulario() {
        txtIdVenta.setText("");
        txtIdPedido.setText("");
        cmbMedioPago.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
