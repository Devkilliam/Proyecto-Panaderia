
package com.panaderiaaurora.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClienteVista extends JPanel {

    public JTextField txtId = new JTextField(22);
    public JTextField txtNombre = new JTextField(22);
    public JTextField txtTelefono = new JTextField(22);
    public JTextField txtCorreo = new JTextField(22);

    public JButton btnNuevo = new JButton("Nuevo");
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnActualizar = new JButton("Actualizar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnConsultar = new JButton("Consultar");

    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"ID", "Nombre", "Teléfono", "Correo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public JTable tabla = new JTable(modeloTabla);

    public ClienteVista() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TÍTULO
        JLabel titulo = new JLabel("GESTIÓN DE CLIENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encabezado.add(titulo);

        add(encabezado, BorderLayout.NORTH);

        // FORMULARIO
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos del cliente")
        );

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(formulario, g, 0, "ID:", txtId);
        agregarCampo(formulario, g, 1, "Nombre:", txtNombre);
        agregarCampo(formulario, g, 2, "Teléfono:", txtTelefono);
        agregarCampo(formulario, g, 3, "Correo:", txtCorreo);

        txtId.setEditable(false);

        formulario.setPreferredSize(new Dimension(600, 250));

        // BOTONES
        JPanel botones = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        // PANEL SUPERIOR
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        superior.add(formulario);
        superior.add(Box.createVerticalStrut(10));
        superior.add(botones);

        // TABLA
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
            BorderFactory.createTitledBorder("Lista de clientes")
        );

        // DISTRIBUCIÓN FINAL
        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // MÉTODO PARA ORGANIZAR LOS CAMPOS
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
