
package com.panaderiaaurora;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class PanaderiaAurora {

    private static final String URL =
            "jdbc:oracle:thin:@//localhost:1521/XEPDB1";

    private static final String USUARIO = "PANADERIA_AURORA";

    public static void main(String[] args) {

        JPasswordField campoClave = new JPasswordField();

        int opcion = JOptionPane.showConfirmDialog(
                null,
                campoClave,
                "Panadería Aurora - Contraseña de Oracle",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        char[] caracteres = campoClave.getPassword();

        try {
            String clave = new String(caracteres);

            try (Connection conexion =
                    DriverManager.getConnection(
                            URL, USUARIO, clave)) {

                String mensaje =
                        "Conexión exitosa con Oracle Database.\n"
                        + "Usuario: " + conexion.getSchema()
                        + "\nSistema: Panadería Aurora";

                JOptionPane.showMessageDialog(
                        null,
                        mensaje,
                        "Conexión establecida",
                        JOptionPane.INFORMATION_MESSAGE
                );

                System.out.println(mensaje);

            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible conectar con Oracle.\n"
                    + "Código: " + e.getErrorCode()
                    + "\nDetalle: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            Arrays.fill(caracteres, '\0');
            campoClave.setText("");
        }
    }
}
