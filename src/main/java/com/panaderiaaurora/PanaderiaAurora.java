package com.panaderiaaurora;

import com.panaderiaaurora.dao.RecetaDAO;
import com.panaderiaaurora.controlador.RecetaControlador;
import com.panaderiaaurora.vista.RecetaVista;
import com.panaderiaaurora.dao.MovimientoInventarioDAO;
import com.panaderiaaurora.controlador.MovimientoInventarioControlador;
import com.panaderiaaurora.vista.MovimientoInventarioVista;
import com.panaderiaaurora.dao.ProduccionDAO;
import com.panaderiaaurora.controlador.ProduccionControlador;
import com.panaderiaaurora.vista.ProduccionVista;
import com.panaderiaaurora.dao.VentaDAO;
import com.panaderiaaurora.controlador.VentaControlador;
import com.panaderiaaurora.vista.VentaVista;
import com.panaderiaaurora.dao.PedidoDAO;
import com.panaderiaaurora.controlador.PedidoControlador;
import com.panaderiaaurora.vista.PedidoVista;
import com.panaderiaaurora.dao.ClienteDAO;
import com.panaderiaaurora.controlador.ClienteControlador;
import com.panaderiaaurora.vista.ClienteVista;
import com.panaderiaaurora.dao.ProveedorDAO;
import com.panaderiaaurora.controlador.ProveedorControlador;
import com.panaderiaaurora.vista.ProveedorVista;
import com.panaderiaaurora.dao.IngredienteDAO;
import com.panaderiaaurora.controlador.IngredienteControlador;
import com.panaderiaaurora.vista.IngredienteVista;
import com.panaderiaaurora.dao.ProductoDAO;
import com.panaderiaaurora.controlador.ProductoControlador;
import com.panaderiaaurora.vista.ProductoVista;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanaderiaAurora extends JFrame {

    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String USUARIO = "PANADERIA_AURORA";
    private static final Color AZUL = new Color(39, 54, 70);
    private static final Color NARANJA = new Color(191, 111, 49);
    private static final Color FONDO = new Color(248, 246, 241);

    private final Connection conexion;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JTextField campoNombre = new JTextField(25);
    private final JTextField campoDescripcion = new JTextField(25);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripción"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private Long categoriaSeleccionada = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PanaderiaAurora::iniciarSesion);
    }

    private static void iniciarSesion() {
        JPasswordField claveCampo = new JPasswordField(18);
        int respuesta = JOptionPane.showConfirmDialog(null, claveCampo,
                "Panadería Aurora - Contraseña de Oracle",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        char[] caracteres = claveCampo.getPassword();
        try {
            String clave = new String(caracteres);
            Connection conexion = DriverManager.getConnection(URL, USUARIO, clave);
            conexion.setAutoCommit(false);
            new PanaderiaAurora(conexion).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "No logramos conectar con Oracle.\n" + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(caracteres, '\0');
            claveCampo.setText("");
        }
    }

    public PanaderiaAurora(Connection conexion) {
        this.conexion = conexion;
        setTitle("Panadería Aurora | Administración");
        setSize(1040, 660);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    conexion.close();
                } catch (SQLException ex) {
                    System.err.println("No se pudo cerrar la conexión: " + ex.getMessage());
                }
                dispose();
            }
        });

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FONDO);
        principal.add(construirMenu(), BorderLayout.WEST);

        contenido.add(construirInicio(), "inicio");
        contenido.add(construirCategorias(), "categorias");
        contenido.add(construirProductos(), "productos");
        contenido.add(construirIngredientes(), "ingredientes");
        contenido.add(construirProveedores(), "proveedores");
        contenido.add(construirClientes(), "clientes");
        contenido.add(construirPedidos(), "pedidos");
        contenido.add(construirVentas(), "ventas");
        contenido.add(construirProduccion(), "produccion");
        contenido.add(construirInventario(), "inventario");
        contenido.add(construirRecetas(), "recetas");

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);
        tarjetas.show(contenido, "inicio");
    }

    private JPanel construirVentas() {
        VentaVista vista = new VentaVista();
        VentaDAO dao = new VentaDAO(conexion);
        new VentaControlador(dao, vista);
        return vista;
    }

    private JPanel construirRecetas() {
        RecetaVista vista = new RecetaVista();
        RecetaDAO dao = new RecetaDAO(conexion);
        new RecetaControlador(dao, vista);
        return vista;
    }

    private JPanel construirProduccion() {
        ProduccionVista vista = new ProduccionVista();
        ProduccionDAO dao = new ProduccionDAO(conexion);
        new ProduccionControlador(dao, vista);
        return vista;
    }

    private JPanel construirMenu() {
        JPanel lateral = new JPanel(new GridBagLayout());
        lateral.setBackground(AZUL);
        lateral.setPreferredSize(new Dimension(220, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 14, 10, 14);

        JLabel marca = new JLabel("PANADERÍA AURORA");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.gridy = 0;
        lateral.add(marca, g);

        JButton inicio = boton("Inicio", false);
        inicio.addActionListener(e -> tarjetas.show(contenido, "inicio"));
        g.gridy = 1;
        lateral.add(inicio, g);

        JButton categorias = boton("Categorías", false);
        categorias.addActionListener(e -> {
            tarjetas.show(contenido, "categorias");
            cargarCategorias();
        });
        g.gridy = 2;
        lateral.add(categorias, g);

        JLabel proximos = new JLabel("PRÓXIMOS MÓDULOS");
        proximos.setForeground(new Color(209, 213, 217));
        g.gridy = 3;
        lateral.add(proximos, g);
        for (String titulo : new String[]{
            "Productos",
            "Ingredientes",
            "Clientes",
            "Proveedores",
            "Pedidos",
            "Ventas",
            "Producción",
            "Inventario",
            "Recetas"
        }) {

            if (titulo.equals("Productos")
                    || titulo.equals("Recetas")
                    || titulo.equals("Inventario")
                    || titulo.equals("Producción")
                    || titulo.equals("Ingredientes")
                    || titulo.equals("Proveedores")
                    || titulo.equals("Clientes")
                    || titulo.equals("Pedidos")
                    || titulo.equals("Ventas")) {
                JButton modulo = boton(titulo, false);

                String tarjeta;

                if (titulo.equals("Productos")) {
                    tarjeta = "productos";
                } else if (titulo.equals("Ingredientes")) {
                    tarjeta = "ingredientes";
                } else if (titulo.equals("Proveedores")) {
                    tarjeta = "proveedores";
                } else if (titulo.equals("Clientes")) {
                    tarjeta = "clientes";
                } else if (titulo.equals("Pedidos")) {
                    tarjeta = "pedidos";
                } else if (titulo.equals("Producción")) {
                    tarjeta = "produccion";
                } else if (titulo.equals("Inventario")) {
                    tarjeta = "inventario";
                } else if (titulo.equals("Recetas")) {
                    tarjeta = "recetas";
                } else {
                    tarjeta = "ventas";
                }

                modulo.addActionListener(e -> tarjetas.show(contenido, tarjeta));

                g.gridy++;
                lateral.add(modulo, g);

            } else {
                JButton pendiente = boton(titulo, true);
                g.gridy++;
                lateral.add(pendiente, g);
            }
        }
        g.gridy++;
        g.weighty = 1;
        lateral.add(new JLabel(""), g);
        return lateral;
    }

    private JPanel construirInventario() {
        MovimientoInventarioVista vista = new MovimientoInventarioVista();
        MovimientoInventarioDAO dao = new MovimientoInventarioDAO(conexion);
        new MovimientoInventarioControlador(dao, vista);
        return vista;
    }

    private JPanel construirInicio() {
        JPanel pagina = paginaBase("Control de producción, inventario y pedidos");
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(6, 0, 6, 0);

        JLabel bienvenida = new JLabel("Bienvenidos a Panadería Aurora");
        bienvenida.setFont(new Font("SansSerif", Font.BOLD, 24));
        bienvenida.setForeground(AZUL);
        tarjeta.add(bienvenida, g);

        g.gridy = 1;
        tarjeta.add(new JLabel("Conectados al esquema PANADERIA_AURORA de Oracle."), g);
        g.gridy = 2;
        tarjeta.add(new JLabel("Iniciamos con la administración real de categorías mediante PL/SQL."), g);
        g.gridy = 3;
        JButton ir = boton("Administrar categorías", false);
        ir.addActionListener(e -> {
            tarjetas.show(contenido, "categorias");
            cargarCategorias();
        });
        tarjeta.add(ir, g);
        pagina.add(tarjeta, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel construirCategorias() {
        JPanel pagina = paginaBase("Administración de categorías");
        JPanel cuerpo = new JPanel(new BorderLayout(0, 18));
        cuerpo.setOpaque(false);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        formulario.add(new JLabel("Nombre:"), g);
        g.gridx = 1;
        formulario.add(campoNombre, g);
        g.gridx = 0;
        g.gridy = 1;
        formulario.add(new JLabel("Descripción:"), g);
        g.gridx = 1;
        formulario.add(campoDescripcion, g);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.setOpaque(false);
        JButton nuevo = boton("Nuevo", false);
        nuevo.addActionListener(e -> limpiarFormulario());
        JButton guardar = boton("Guardar", false);
        guardar.addActionListener(e -> guardarCategoria());
        JButton eliminar = boton("Eliminar", false);
        eliminar.addActionListener(e -> eliminarCategoria());
        JButton recargar = boton("Actualizar lista", false);
        recargar.addActionListener(e -> cargarCategorias());
        acciones.add(nuevo);
        acciones.add(guardar);
        acciones.add(eliminar);
        acciones.add(recargar);
        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 2;
        formulario.add(acciones, g);

        tabla.setRowHeight(28);
        tabla.setSelectionBackground(new Color(242, 222, 203));
        tabla.setSelectionForeground(AZUL);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || tabla.getSelectedRow() < 0) {
                return;
            }
            int fila = tabla.convertRowIndexToModel(tabla.getSelectedRow());
            categoriaSeleccionada = ((Number) modelo.getValueAt(fila, 0)).longValue();
            campoNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
            Object descripcion = modelo.getValueAt(fila, 2);
            campoDescripcion.setText(descripcion == null ? "" : descripcion.toString());
        });

        cuerpo.add(formulario, BorderLayout.NORTH);
        cuerpo.add(new JScrollPane(tabla), BorderLayout.CENTER);
        pagina.add(cuerpo, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel construirProductos() {
        ProductoVista vista = new ProductoVista();
        ProductoDAO dao = new ProductoDAO(conexion);
        new ProductoControlador(dao, vista);
        return vista;
    }

    private JPanel construirIngredientes() {
        IngredienteVista vista = new IngredienteVista();
        IngredienteDAO dao = new IngredienteDAO(conexion);
        new IngredienteControlador(dao, vista);
        return vista;
    }

    private JPanel construirProveedores() {
        ProveedorVista vista = new ProveedorVista();
        ProveedorDAO dao = new ProveedorDAO(conexion);
        new ProveedorControlador(dao, vista);
        return vista;
    }

    private JPanel construirClientes() {
        ClienteVista vista = new ClienteVista();
        ClienteDAO dao = new ClienteDAO(conexion);
        new ClienteControlador(dao, vista);
        return vista;
    }

    private JPanel construirPedidos() {
        PedidoVista vista = new PedidoVista();
        PedidoDAO dao = new PedidoDAO(conexion);
        new PedidoControlador(dao, vista);
        return vista;
    }

    private JPanel paginaBase(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        JLabel etiqueta = new JLabel(titulo, SwingConstants.LEFT);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 23));
        etiqueta.setForeground(AZUL);
        panel.add(etiqueta, BorderLayout.NORTH);
        return panel;
    }

    private JButton boton(String texto, boolean deshabilitado) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setBackground(deshabilitado ? new Color(218, 218, 218) : NARANJA);
        b.setForeground(deshabilitado ? Color.DARK_GRAY : Color.WHITE);
        b.setEnabled(!deshabilitado);
        return b;
    }

    private void cargarCategorias() {
        try (CallableStatement llamada = conexion.prepareCall(
                "{ call PR_CATEGORIA_CONSULTAR(?, ?) }")) {
            llamada.setNull(1, Types.NUMERIC);
            llamada.registerOutParameter(2, Types.REF_CURSOR);
            llamada.execute();
            try (ResultSet filas = (ResultSet) llamada.getObject(2)) {
                modelo.setRowCount(0);
                while (filas.next()) {
                    modelo.addRow(new Object[]{
                        filas.getLong("ID_CATEGORIA"),
                        filas.getString("NOMBRE"),
                        filas.getString("DESCRIPCION")
                    });
                }
            }
        } catch (SQLException ex) {
            mostrarError("consultar categorías", ex);
        }
    }

    private void guardarCategoria() {
        String nombre = campoNombre.getText().trim();
        String descripcion = campoDescripcion.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debemos indicar un nombre.");
            return;
        }
        try {
            if (categoriaSeleccionada == null) {
                try (CallableStatement llamada = conexion.prepareCall(
                        "{ call PR_CATEGORIA_CREAR(?, ?, ?) }")) {
                    llamada.setString(1, nombre);
                    llamada.setString(2, descripcion);
                    llamada.registerOutParameter(3, Types.NUMERIC);
                    llamada.execute();
                }
            } else {
                try (CallableStatement llamada = conexion.prepareCall(
                        "{ call PR_CATEGORIA_ACTUALIZAR(?, ?, ?) }")) {
                    llamada.setLong(1, categoriaSeleccionada);
                    llamada.setString(2, nombre);
                    llamada.setString(3, descripcion);
                    llamada.execute();
                }
            }
            conexion.commit();
            limpiarFormulario();
            cargarCategorias();
            JOptionPane.showMessageDialog(this, "Guardamos la categoría correctamente.");
        } catch (SQLException ex) {
            deshacerCambios();
            mostrarError("guardar la categoría", ex);
        }
    }

    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría de la tabla.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar la categoría seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }
        try (CallableStatement llamada = conexion.prepareCall(
                "{ call PR_CATEGORIA_ELIMINAR(?) }")) {
            llamada.setLong(1, categoriaSeleccionada);
            llamada.execute();
            conexion.commit();
            limpiarFormulario();
            cargarCategorias();
            JOptionPane.showMessageDialog(this, "Eliminamos la categoría.");
        } catch (SQLException ex) {
            deshacerCambios();
            mostrarError("eliminar la categoría", ex);
        }
    }

    private void limpiarFormulario() {
        categoriaSeleccionada = null;
        tabla.clearSelection();
        campoNombre.setText("");
        campoDescripcion.setText("");
        campoNombre.requestFocusInWindow();
    }

    private void deshacerCambios() {
        try {
            conexion.rollback();
        } catch (SQLException ex) {
            mostrarError("revertir la operación", ex);
        }
    }

    private void mostrarError(String operacion, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "No pudimos " + operacion + ".\nCódigo Oracle: "
                + ex.getErrorCode() + "\n" + ex.getMessage(),
                "Error de Oracle", JOptionPane.ERROR_MESSAGE);
    }
}
