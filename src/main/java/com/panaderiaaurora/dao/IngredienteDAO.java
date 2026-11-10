
package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Ingrediente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {

    private final Connection conexion;

    public IngredienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public long crearIngrediente(Ingrediente ingrediente)
            throws SQLException {

        String sql = "{call PR_INGREDIENTE_CREAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, ingrediente.getNombre());
            cs.setString(2, ingrediente.getUnidadMedida());
            cs.setBigDecimal(3, ingrediente.getStockMinimo());
            cs.registerOutParameter(4, Types.NUMERIC);

            cs.execute();

            return cs.getLong(4);
        }
    }

    public List<Ingrediente> consultarIngredientes()
            throws SQLException {

        List<Ingrediente> lista = new ArrayList<>();
        String sql = "{call PR_INGREDIENTE_CONSULTAR(?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setNull(1, Types.NUMERIC);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    lista.add(new Ingrediente(
                        rs.getLong("ID_INGREDIENTE"),
                        rs.getString("NOMBRE"),
                        rs.getString("UNIDAD_MEDIDA"),
                        rs.getBigDecimal("STOCK_MINIMO")
                    ));
                }
            }
        }

        return lista;
    }

    public void actualizarIngrediente(Ingrediente ingrediente)
            throws SQLException {

        String sql = "{call PR_INGREDIENTE_ACTUALIZAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, ingrediente.getIdIngrediente());
            cs.setString(2, ingrediente.getNombre());
            cs.setString(3, ingrediente.getUnidadMedida());
            cs.setBigDecimal(4, ingrediente.getStockMinimo());
            cs.execute();
        }
    }

    public void eliminarIngrediente(long id)
            throws SQLException {

        String sql = "{call PR_INGREDIENTE_ELIMINAR(?)}";

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
