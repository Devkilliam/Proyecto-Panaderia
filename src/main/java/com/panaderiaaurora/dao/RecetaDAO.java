package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Receta;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class RecetaDAO {

    private final Connection conexion;

    public RecetaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Long crear(Receta receta) throws SQLException {
        String sql = "{ call PKG_RECETA.crear(?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, receta.getIdProducto());
            cs.setInt(2, receta.getVersionReceta());
            cs.setDouble(3, receta.getRendimiento());
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

    public List<Receta> consultar(Long idReceta) throws SQLException {

        List<Receta> lista = new ArrayList<>();

        String sql = "{ call PKG_RECETA.consultar(?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            if (idReceta == null) {
                cs.setNull(1, Types.NUMERIC);
            } else {
                cs.setLong(1, idReceta);
            }

            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {

                while (rs.next()) {
                    Receta receta = new Receta();

                    receta.setIdReceta(rs.getLong("ID_RECETA"));
                    receta.setIdProducto(rs.getLong("ID_PRODUCTO"));
                    receta.setVersionReceta(
                            rs.getInt("VERSION_RECETA"));
                    receta.setRendimiento(
                            rs.getDouble("RENDIMIENTO"));

                    lista.add(receta);
                }
            }
        }

        return lista;
    }

    public void actualizar(Receta receta) throws SQLException {

        String sql = "{ call PKG_RECETA.actualizar(?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, receta.getIdReceta());
            cs.setLong(2, receta.getIdProducto());
            cs.setInt(3, receta.getVersionReceta());
            cs.setDouble(4, receta.getRendimiento());

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    public void eliminar(Long idReceta) throws SQLException {

        String sql = "{ call PKG_RECETA.eliminar(?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, idReceta);

            cs.execute();
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }
}