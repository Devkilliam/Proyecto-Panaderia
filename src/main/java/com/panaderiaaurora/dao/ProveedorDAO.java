
package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Proveedor;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    private final Connection conexion;

    public ProveedorDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void crear(Proveedor proveedor) throws SQLException {
        String sql = "{call PR_PROVEEDOR_CREAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, proveedor.getNombre());
            cs.setString(2, proveedor.getTelefono());
            cs.setString(3, proveedor.getCorreo());
            cs.registerOutParameter(4, Types.NUMERIC);

            cs.execute();

            proveedor.setIdProveedor(
                cs.getLong(4)
            );
        }
    }

    public List<Proveedor> consultar(Long idProveedor)
            throws SQLException {

        List<Proveedor> lista = new ArrayList<>();
        String sql = "{call PR_PROVEEDOR_CONSULTAR(?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            if (idProveedor == null) {
                cs.setNull(1, Types.NUMERIC);
            } else {
                cs.setLong(1, idProveedor);
            }

            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor();

                    proveedor.setIdProveedor(
                        rs.getLong("ID_PROVEEDOR")
                    );
                    proveedor.setNombre(rs.getString("NOMBRE"));
                    proveedor.setTelefono(rs.getString("TELEFONO"));
                    proveedor.setCorreo(rs.getString("CORREO"));

                    lista.add(proveedor);
                }
            }
        }

        return lista;
    }

    public void actualizar(Proveedor proveedor)
            throws SQLException {

        String sql = "{call PR_PROVEEDOR_ACTUALIZAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, proveedor.getIdProveedor());
            cs.setString(2, proveedor.getNombre());
            cs.setString(3, proveedor.getTelefono());
            cs.setString(4, proveedor.getCorreo());

            cs.execute();
        }
    }

    public void eliminar(Long idProveedor) throws SQLException {
        String sql = "{call PR_PROVEEDOR_ELIMINAR(?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, idProveedor);
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
