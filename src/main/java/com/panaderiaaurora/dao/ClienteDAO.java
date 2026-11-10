
package com.panaderiaaurora.dao;

import com.panaderiaaurora.modelo.Cliente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final Connection conexion;

    public ClienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void crear(Cliente cliente) throws SQLException {
        String sql = "{call PR_CLIENTE_CREAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, cliente.getNombre());
            cs.setString(2, cliente.getTelefono());
            cs.setString(3, cliente.getCorreo());
            cs.registerOutParameter(4, Types.NUMERIC);

            cs.execute();

            cliente.setIdCliente(cs.getLong(4));
        }
    }

    public List<Cliente> consultar(Long idCliente) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "{call PR_CLIENTE_CONSULTAR(?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            if (idCliente == null) {
                cs.setNull(1, Types.NUMERIC);
            } else {
                cs.setLong(1, idCliente);
            }

            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();

                    cliente.setIdCliente(rs.getLong("ID_CLIENTE"));
                    cliente.setNombre(rs.getString("NOMBRE"));
                    cliente.setTelefono(rs.getString("TELEFONO"));
                    cliente.setCorreo(rs.getString("CORREO"));

                    lista.add(cliente);
                }
            }
        }

        return lista;
    }

    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "{call PR_CLIENTE_ACTUALIZAR(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, cliente.getIdCliente());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getTelefono());
            cs.setString(4, cliente.getCorreo());

            cs.execute();
        }
    }

    public void eliminar(Long idCliente) throws SQLException {
        String sql = "{call PR_CLIENTE_ELIMINAR(?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setLong(1, idCliente);
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
