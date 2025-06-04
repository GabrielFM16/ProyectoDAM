package com.cesur.biblioteca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class ConexionBBDD {

    // Configuración de la conexión a MariaDB
    private static final String URL = "jdbc:mariadb://localhost:3306/biblioteca_db";
    private static final String USER = "bibliotecario"; // O el usuario que creaste (e.g., 'root' si no creaste otro)
    private static final String PASSWORD = "12345"; // La contraseña de tu usuario MariaDB

    /**
     * Obtiene una conexión a la base de datos.
     * @return Objeto Connection si la conexión es exitosa, null en caso contrario.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            
            try {
                // Cargar el driver JDBC de MariaDB
                Class.forName("org.mariadb.jdbc.Driver"); 
            } catch (ClassNotFoundException ex) {
                System.err.println("Error al obtener el driver JDBC: " + ex.getMessage());
            }
            
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la BBDD.");
            
        } catch (SQLException e) {
            System.err.println("Error al conectarse a la BBDD: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos de forma segura.
     * @param connection La conexión a cerrar.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada con la BBDD.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión con la BBDD: " + e.getMessage());
            }
        }
    }
}