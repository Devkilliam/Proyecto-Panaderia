package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private final Connection conexion;

    public ProductoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // CREATE
    public long crearProducto(Producto producto) throws SQLException {
        String sql = "{call PR_PRODUCTO_CREAR(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, producto.getIdCategoria());
            cs.setString(2, producto.getNombre());
            cs.setBigDecimal(3, producto.getPrecioVenta());
            cs.setInt(4, producto.getActivo());
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.execute();
            return cs.getLong(5);
        }
    }

    // READ: todos
    public List<Producto> consultarProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "{call PR_PRODUCTO_CONSULTAR(?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setNull(1, Types.NUMERIC);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    productos.add(new Producto(
                            rs.getLong("ID_PRODUCTO"),
                            rs.getLong("ID_CATEGORIA"),
                            rs.getString("NOMBRE"),
                            rs.getBigDecimal("PRECIO_VENTA"),
                            rs.getInt("ACTIVO")
                    ));
                }
            }
        }
        return productos;
    }

    // READ: por ID
    public Producto consultarProducto(long id) throws SQLException {
        String sql = "{call PR_PRODUCTO_CONSULTAR(?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, id);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    return new Producto(
                            rs.getLong("ID_PRODUCTO"),
                            rs.getLong("ID_CATEGORIA"),
                            rs.getString("NOMBRE"),
                            rs.getBigDecimal("PRECIO_VENTA"),
                            rs.getInt("ACTIVO")
                    );
                }
            }
        }
        return null;
    }

    // UPDATE
    public void actualizarProducto(Producto producto) throws SQLException {
        String sql = "{call PR_PRODUCTO_ACTUALIZAR(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, producto.getIdProducto());
            cs.setLong(2, producto.getIdCategoria());
            cs.setString(3, producto.getNombre());
            cs.setBigDecimal(4, producto.getPrecioVenta());
            cs.setInt(5, producto.getActivo());
            cs.execute();
        }
    }

    // DELETE
    public void eliminarProducto(long id) throws SQLException {
        String sql = "{call PR_PRODUCTO_ELIMINAR(?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, id);
            cs.execute();
        }
    }

    public void confirmarCambios() throws SQLException {
        conexion.commit();
    }

    public void revertirCambios() throws SQLException {
        conexion.rollback();
    }
}
