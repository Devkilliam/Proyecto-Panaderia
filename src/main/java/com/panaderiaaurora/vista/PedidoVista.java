
package com.panaderiaaurora.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PedidoVista extends JPanel {

    public JTextField txtId = new JTextField(15);
    public JTextField txtCliente = new JTextField(15);
    public JTextField txtEmpleado = new JTextField(15);
    public JTextField txtFechaPedido = new JTextField(15);
    public JTextField txtFechaEntrega = new JTextField(15);

    public JComboBox<String> cmbTipo = new JComboBox<>(
        new String[]{"MOSTRADOR", "ENCARGO"}
    );

    public JComboBox<String> cmbEstado = new JComboBox<>(
        new String[]{"PENDIENTE", "EN_PRODUCCION", "LISTO",
                     "ENTREGADO", "CANCELADO"}
    );

    public JButton btnNuevo = new JButton("Nuevo");
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnActualizar = new JButton("Actualizar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnConsultar = new JButton("Consultar");

    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"ID", "Cliente", "Empleado", "Fecha pedido",
                     "Fecha entrega", "Tipo", "Estado"}, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public JTable tabla = new JTable(modeloTabla);

    public PedidoVista() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("GESTIÓN DE PEDIDOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encabezado.add(titulo);

        add(encabezado, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos del pedido")
        );

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(formulario, g, 0, "ID:", txtId);
        agregarCampo(formulario, g, 1, "ID Cliente:", txtCliente);
        agregarCampo(formulario, g, 2, "ID Empleado:", txtEmpleado);
        agregarCampo(formulario, g, 3, "Fecha pedido:", txtFechaPedido);
        agregarCampo(formulario, g, 4, "Fecha entrega:", txtFechaEntrega);

        g.gridx = 0;
        g.gridy = 5;
        formulario.add(new JLabel("Tipo:"), g);
        g.gridx = 1;
        formulario.add(cmbTipo, g);

        g.gridx = 0;
        g.gridy = 6;
        formulario.add(new JLabel("Estado:"), g);
        g.gridx = 1;
        formulario.add(cmbEstado, g);

        txtId.setEditable(false);
        txtFechaPedido.setEditable(false);
        cmbEstado.setSelectedItem("PENDIENTE");

        JPanel botones = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 8, 8)
        );

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(formulario);
        superior.add(Box.createVerticalStrut(8));
        superior.add(botones);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
            BorderFactory.createTitledBorder("Lista de pedidos")
        );

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarCampo(
        JPanel panel,
        GridBagConstraints g,
        int fila,
        String etiqueta,
        JTextField campo
    ) {
        g.gridx = 0;
        g.gridy = fila;
        g.weightx = 0;
        panel.add(new JLabel(etiqueta), g);

        g.gridx = 1;
        g.weightx = 1;
        panel.add(campo, g);
    }
}
