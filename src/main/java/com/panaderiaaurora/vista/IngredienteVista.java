
package com.panaderiaaurora.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class IngredienteVista extends JPanel {

    public JTextField txtId = new JTextField(22);
    public JTextField txtNombre = new JTextField(22);
    public JTextField txtUnidad = new JTextField(22);
    public JTextField txtStockMinimo = new JTextField(22);

    public JButton btnNuevo = new JButton("Nuevo");
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnActualizar = new JButton("Actualizar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnConsultar = new JButton("Consultar");

    public DefaultTableModel modelo = new DefaultTableModel(
        new Object[]{"ID", "Nombre", "Unidad de medida", "Stock mínimo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public JTable tabla = new JTable(modelo);

    public IngredienteVista() {
        setLayout(new BorderLayout(10, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Administración de ingredientes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del ingrediente"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        agregarCampo(formulario, g, 0, "ID:", txtId);
        agregarCampo(formulario, g, 1, "Nombre:", txtNombre);
        agregarCampo(formulario, g, 2, "Unidad de medida:", txtUnidad);
        agregarCampo(formulario, g, 3, "Stock mínimo:", txtStockMinimo);

        txtId.setEditable(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        JPanel superior = new JPanel(new BorderLayout(5, 10));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        tabla.setRowHeight(26);
        tabla.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(650, 300));

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints g,
            int fila, String etiqueta, JTextField campo) {

        g.gridx = 0;
        g.gridy = fila;
        g.weightx = 0;
        panel.add(new JLabel(etiqueta), g);

        g.gridx = 1;
        g.weightx = 1;
        panel.add(campo, g);
    }
}
