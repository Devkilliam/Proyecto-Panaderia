
package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Venta;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private Connection conexion;

    public VentaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void crear(Venta venta) throws Exception {
        String sql = "{ call PKG_VENTA.crear(?, ?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, venta.getIdPedido());
            llamada.setString(2, venta.getMedioPago());
            llamada.registerOutParameter(3, Types.NUMERIC);

            llamada.execute();

            venta.setIdVenta(llamada.getLong(3));
            confirmarCambios();
        } catch (Exception e) {
            revertirCambios();
            throw e;
        }
    }

    public List<Venta> consultar(Long idVenta) throws Exception {
        List<Venta> ventas = new ArrayList<>();

        String sql = "{ call PKG_VENTA.consultar(?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            if (idVenta == null) {
                llamada.setNull(1, Types.NUMERIC);
            } else {
                llamada.setLong(1, idVenta);
            }

            llamada.registerOutParameter(2, Types.REF_CURSOR);
            llamada.execute();

            try (ResultSet rs = (ResultSet) llamada.getObject(2)) {
                while (rs.next()) {
                    Venta venta = new Venta();

                    venta.setIdVenta(rs.getLong("ID_VENTA"));
                    venta.setIdPedido(rs.getLong("ID_PEDIDO"));
                    venta.setFechaVenta(rs.getDate("FECHA_VENTA"));
                    venta.setMedioPago(rs.getString("MEDIO_PAGO"));

                    venta.setTotal(obtenerTotal(venta.getIdVenta()));

                    ventas.add(venta);
                }
            }
        }

        return ventas;
    }

    public void actualizar(Venta venta) throws Exception {
        String sql = "{ call PKG_VENTA.actualizar(?, ?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, venta.getIdVenta());
            llamada.setString(2, venta.getMedioPago());

            llamada.execute();
            confirmarCambios();
        } catch (Exception e) {
            revertirCambios();
            throw e;
        }
    }

    public void eliminar(Long idVenta) throws Exception {
        String sql = "{ call PKG_VENTA.eliminar(?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.setLong(1, idVenta);
            llamada.execute();
            confirmarCambios();
        } catch (Exception e) {
            revertirCambios();
            throw e;
        }
    }

    public Double obtenerTotal(Long idVenta) throws Exception {
        String sql = "{ ? = call PKG_VENTA.total(?) }";

        try (CallableStatement llamada = conexion.prepareCall(sql)) {
            llamada.registerOutParameter(1, Types.NUMERIC);
            llamada.setLong(2, idVenta);
            llamada.execute();

            return llamada.getDouble(1);
        }
    }

    private void confirmarCambios() throws Exception {
        conexion.commit();
    }

    private void revertirCambios() {
        try {
            conexion.rollback();
        } catch (Exception e) {
            System.err.println("Error al revertir: " + e.getMessage());
        }
    }
}
