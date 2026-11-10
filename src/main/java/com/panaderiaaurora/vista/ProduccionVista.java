package com.panaderiaaurora.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProduccionVista extends JPanel {

    public JTextField txtIdProduccion;
    public JTextField txtIdReceta;
    public JTextField txtIdEmpleado;
    public JTextField txtLotes;

    public JComboBox<String> cmbEstado;

    public JButton btnNuevo;
    public JButton btnGuardar;
    public JButton btnActualizar;
    public JButton btnEliminar;
    public JButton btnConsultar;

    public JTable tabla;
    public DefaultTableModel modelo;

    public ProduccionVista() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de Producción");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(5, 2, 10, 10));

        txtIdProduccion = new JTextField();
        txtIdProduccion.setEditable(false);

        txtIdReceta = new JTextField();
        txtIdEmpleado = new JTextField();
        txtLotes = new JTextField();

        cmbEstado = new JComboBox<>(new String[]{
            "PLANIFICADA",
            "EN_PROCESO",
            "COMPLETADA",
            "CANCELADA"
        });

        formulario.add(new JLabel("ID Producción:"));
        formulario.add(txtIdProduccion);

        formulario.add(new JLabel("ID Receta:"));
        formulario.add(txtIdReceta);

        formulario.add(new JLabel("ID Empleado:"));
        formulario.add(txtIdEmpleado);

        formulario.add(new JLabel("Lotes producidos:"));
        formulario.add(txtLotes);

        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

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

        JPanel superior = new JPanel(new BorderLayout(10, 10));
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
            new String[]{
                "ID Producción",
                "ID Receta",
                "ID Empleado",
                "Fecha Producción",
                "Lotes",
                "Estado"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}