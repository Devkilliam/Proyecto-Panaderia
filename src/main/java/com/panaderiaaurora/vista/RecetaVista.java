package com.panaderiaaurora.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RecetaVista extends JPanel {

    public JTextField txtIdReceta;
    public JTextField txtIdProducto;
    public JTextField txtVersionReceta;
    public JTextField txtRendimiento;

    public JButton btnNuevo;
    public JButton btnGuardar;
    public JButton btnActualizar;
    public JButton btnEliminar;
    public JButton btnConsultar;

    public JTable tabla;
    public DefaultTableModel modelo;

    public RecetaVista() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de Recetas");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        txtIdReceta = new JTextField(20);
        txtIdReceta.setEditable(false);

        txtIdProducto = new JTextField(20);
        txtVersionReceta = new JTextField(20);
        txtRendimiento = new JTextField(20);

        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));

        formulario.add(new JLabel("ID Receta:"));
        formulario.add(txtIdReceta);

        formulario.add(new JLabel("ID Producto:"));
        formulario.add(txtIdProducto);

        formulario.add(new JLabel("Versión de receta:"));
        formulario.add(txtVersionReceta);

        formulario.add(new JLabel("Rendimiento:"));
        formulario.add(txtRendimiento);

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnConsultar = new JButton("Consultar");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);

        JPanel superior = new JPanel(new BorderLayout(10, 10));

        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new String[]{
                    "ID Receta",
                    "ID Producto",
                    "Versión",
                    "Rendimiento"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(tabla);

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}