package com.panaderiaaurora.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MovimientoInventarioVista extends JPanel {

    public JTextField txtIdMovimiento;
    public JTextField txtIdIngrediente;
    public JTextField txtIdEmpleado;
    public JTextField txtIdProveedor;
    public JTextField txtIdProduccion;
    public JTextField txtCantidad;

    public JComboBox<String> cmbTipo;

    public JButton btnNuevo;
    public JButton btnGuardar;
    public JButton btnActualizar;
    public JButton btnEliminar;
    public JButton btnConsultar;
    public JButton btnExistencia;

    public JTable tabla;
    public DefaultTableModel modelo;

    public MovimientoInventarioVista() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de Inventario");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        txtIdMovimiento = new JTextField(20);
        txtIdMovimiento.setEditable(false);

        txtIdIngrediente = new JTextField(20);
        txtIdEmpleado = new JTextField(20);
        txtIdProveedor = new JTextField(20);
        txtIdProduccion = new JTextField(20);
        txtCantidad = new JTextField(20);

        cmbTipo = new JComboBox<>(new String[]{
            "ENTRADA",
            "SALIDA",
            "AJUSTE_ENTRADA",
            "AJUSTE_SALIDA"
        });

        JPanel formulario = new JPanel(new GridLayout(7, 2, 10, 10));

        formulario.add(new JLabel("ID Movimiento:"));
        formulario.add(txtIdMovimiento);

        formulario.add(new JLabel("ID Ingrediente:"));
        formulario.add(txtIdIngrediente);

        formulario.add(new JLabel("ID Empleado:"));
        formulario.add(txtIdEmpleado);

        formulario.add(new JLabel("ID Proveedor (opcional):"));
        formulario.add(txtIdProveedor);

        formulario.add(new JLabel("ID Producción (opcional):"));
        formulario.add(txtIdProduccion);

        formulario.add(new JLabel("Tipo de movimiento:"));
        formulario.add(cmbTipo);

        formulario.add(new JLabel("Cantidad:"));
        formulario.add(txtCantidad);

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnConsultar = new JButton("Consultar");
        btnExistencia = new JButton("Ver existencia");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnConsultar);
        botones.add(btnExistencia);

        JPanel superior = new JPanel(new BorderLayout(10, 10));

        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new String[]{
                    "ID Movimiento",
                    "ID Ingrediente",
                    "ID Empleado",
                    "ID Proveedor",
                    "ID Producción",
                    "Tipo",
                    "Cantidad",
                    "Fecha"
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