
package com.panaderiaaurora.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductoVista extends JPanel {

    public JTextField txtId = new JTextField(8);
    public JTextField txtCategoria = new JTextField(8);
    public JTextField txtNombre = new JTextField(18);
    public JTextField txtPrecio = new JTextField(10);
    public JComboBox<String> cmbActivo =
            new JComboBox<>(new String[]{"Sí", "No"});

    public JButton btnNuevo = new JButton("Nuevo");
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnActualizar = new JButton("Actualizar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnConsultar = new JButton("Consultar");

    public DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Categoría", "Nombre", "Precio", "Activo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public JTable tabla = new JTable(modelo);

    public ProductoVista() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Administración de Productos");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));

        txtId.setEditable(false);

        formulario.add(new JLabel("ID Producto:"));
        formulario.add(txtId);

        formulario.add(new JLabel("ID Categoría:"));
        formulario.add(txtCategoria);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Precio de venta:"));
        formulario.add(txtPrecio);

        formulario.add(new JLabel("Activo:"));
        formulario.add(cmbActivo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        JPanel superior = new JPanel(new BorderLayout(10, 10));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        add(superior, BorderLayout.CENTER);
        add(new JScrollPane(tabla), BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
