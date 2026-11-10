package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.MovimientoInventario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class MovimientoInventarioDAO {

    private final Connection conexion;

    public MovimientoInventarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Long crear(MovimientoInventario m) throws SQLException {
        String sql = "{ call PKG_MOVIMIENTO_INV.crear(?, ?, ?, ?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, m.getIdIngrediente());
            cs.setLong(2, m.getIdEmpleado());

            if (m.getIdProveedor() == null) {
                cs.setNull(3, Types.NUMERIC);
            } else {
                cs.setLong(3, m.getIdProveedor());
            }

            if (m.getIdProduccion() == null) {
                cs.setNull(4, Types.NUMERIC);
            } else {
                cs.setLong(4, m.getIdProduccion());
            }

            cs.setString(5, m.getTipo());
            cs.setDouble(6, m.getCantidad());
            cs.registerOutParameter(7, Types.NUMERIC);

            cs.execute();

            Long id = cs.getLong(7);
            conexion.commit();

            return id;

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public List<MovimientoInventario> consultar(Long idMovimiento)
            throws SQLException {

        List<MovimientoInventario> lista = new ArrayList<>();

        String sql = "{ call PKG_MOVIMIENTO_INV.consultar(?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            if (idMovimiento == null) {
                cs.setNull(1, Types.NUMERIC);
            } else {
                cs.setLong(1, idMovimiento);
            }

            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {

                while (rs.next()) {
                    MovimientoInventario m = new MovimientoInventario();

                    m.setIdMovimiento(rs.getLong("ID_MOVIMIENTO"));
                    m.setIdIngrediente(rs.getLong("ID_INGREDIENTE"));
                    m.setIdEmpleado(rs.getLong("ID_EMPLEADO"));

                    long proveedor = rs.getLong("ID_PROVEEDOR");
                    m.setIdProveedor(rs.wasNull() ? null : proveedor);

                    long produccion = rs.getLong("ID_PRODUCCION");
                    m.setIdProduccion(rs.wasNull() ? null : produccion);

                    m.setTipo(rs.getString("TIPO"));
                    m.setCantidad(rs.getDouble("CANTIDAD"));
                    m.setFechaMovimiento(rs.getDate("FECHA_MOVIMIENTO"));

                    lista.add(m);
                }
            }
        }

        return lista;
    }

    public void actualizar(Long idMovimiento, String tipo, Double cantidad)
            throws SQLException {

        String sql = "{ call PKG_MOVIMIENTO_INV.actualizar(?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, idMovimiento);
            cs.setString(2, tipo);
            cs.setDouble(3, cantidad);

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public void eliminar(Long idMovimiento) throws SQLException {
        String sql = "{ call PKG_MOVIMIENTO_INV.eliminar(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, idMovimiento);

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public Double obtenerExistencia(Long idIngrediente) throws SQLException {
        String sql = "{ ? = call PKG_MOVIMIENTO_INV.existencia(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idIngrediente);

            cs.execute();

            return cs.getDouble(1);
        }
    }
}