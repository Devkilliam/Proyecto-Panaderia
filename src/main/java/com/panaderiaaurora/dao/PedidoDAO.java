
package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Pedido;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private final Connection conexion;

    public PedidoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void crear(Pedido pedido) throws SQLException {
        String sql = "{ call PKG_PEDIDO.crear(?, ?, ?, ?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, pedido.getIdCliente());
            llamada.setLong(2, pedido.getIdEmpleado());

            if (pedido.getFechaEntrega() == null) {
                llamada.setNull(3, Types.DATE);
            } else {
                llamada.setDate(3, pedido.getFechaEntrega());
            }

            llamada.setString(4, pedido.getTipo());
            llamada.registerOutParameter(5, Types.NUMERIC);

            llamada.execute();

            pedido.setIdPedido(llamada.getLong(5));
        }
    }

    public List<Pedido> consultar(Long idPedido) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "{ call PKG_PEDIDO.consultar(?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {

            if (idPedido == null) {
                llamada.setNull(1, Types.NUMERIC);
            } else {
                llamada.setLong(1, idPedido);
            }

            llamada.registerOutParameter(2, Types.REF_CURSOR);
            llamada.execute();

            try (ResultSet resultado = (ResultSet) llamada.getObject(2)) {
                while (resultado.next()) {
                    Pedido pedido = new Pedido(
                        resultado.getLong("ID_PEDIDO"),
                        resultado.getLong("ID_CLIENTE"),
                        resultado.getLong("ID_EMPLEADO"),
                        resultado.getDate("FECHA_PEDIDO"),
                        resultado.getDate("FECHA_ENTREGA"),
                        resultado.getString("TIPO"),
                        resultado.getString("ESTADO")
                    );

                    pedidos.add(pedido);
                }
            }
        }

        return pedidos;
    }

    public void actualizar(Pedido pedido) throws SQLException {
        String sql = "{ call PKG_PEDIDO.actualizar(?, ?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, pedido.getIdPedido());

            if (pedido.getFechaEntrega() == null) {
                llamada.setNull(2, Types.DATE);
            } else {
                llamada.setDate(2, pedido.getFechaEntrega());
            }

            llamada.setString(3, pedido.getEstado());

            llamada.execute();
        }
    }

    public void eliminar(Long idPedido) throws SQLException {
        String sql = "{ call PKG_PEDIDO.eliminar(?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, idPedido);
            llamada.execute();
        }
    }

    public double consultarTotal(Long idPedido) throws SQLException {
        String sql = "{ ? = call PKG_PEDIDO.total(?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.registerOutParameter(1, Types.NUMERIC);
            llamada.setLong(2, idPedido);
            llamada.execute();

            return llamada.getDouble(1);
        }
    }

    public void confirmarCambios() throws SQLException {
        conexion.commit();
    }

    public void revertirCambios() throws SQLException {
        conexion.rollback();
    }
}
