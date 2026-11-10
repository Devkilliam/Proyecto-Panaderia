package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Produccion;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class ProduccionDAO {

    private final Connection conexion;

    public ProduccionDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Long crear(Produccion produccion) throws SQLException {
        String sql = "{ call PKG_PRODUCCION.crear(?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, produccion.getIdReceta());
            cs.setLong(2, produccion.getIdEmpleado());
            cs.setInt(3, produccion.getLotesProducidos());
            cs.registerOutParameter(4, Types.NUMERIC);

            cs.execute();

            Long id = cs.getLong(4);
            conexion.commit();

            return id;
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public List<Produccion> consultar(Long idProduccion) throws SQLException {
        List<Produccion> lista = new ArrayList<>();

        String sql = "{ call PKG_PRODUCCION.consultar(?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            if (idProduccion == null) {
                cs.setNull(1, Types.NUMERIC);
            } else {
                cs.setLong(1, idProduccion);
            }

            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Produccion p = new Produccion();

                    p.setIdProduccion(rs.getLong("ID_PRODUCCION"));
                    p.setIdReceta(rs.getLong("ID_RECETA"));
                    p.setIdEmpleado(rs.getLong("ID_EMPLEADO"));

                    java.sql.Date fecha = rs.getDate("FECHA_PRODUCCION");
                    p.setFechaProduccion(fecha);

                    p.setLotesProducidos(rs.getInt("LOTES_PRODUCIDOS"));
                    p.setEstado(rs.getString("ESTADO"));

                    lista.add(p);
                }
            }
        }

        return lista;
    }

    public void actualizar(Long idProduccion, Integer lotesProducidos,
            String estado) throws SQLException {

        String sql = "{ call PKG_PRODUCCION.actualizar(?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, idProduccion);
            cs.setInt(2, lotesProducidos);
            cs.setString(3, estado);

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public void eliminar(Long idProduccion) throws SQLException {
        String sql = "{ call PKG_PRODUCCION.eliminar(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, idProduccion);

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }
}