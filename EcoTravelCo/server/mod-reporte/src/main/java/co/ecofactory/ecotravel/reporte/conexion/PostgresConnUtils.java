package co.ecofactory.ecotravel.conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by CristianCamilo on 15/05/2016.
 */
public class PostgresConnUtils {
    public static Connection getPostgresConnection()
            throws ClassNotFoundException, SQLException {

        Properties propiedades = new Properties();
        try {
            propiedades.load(new FileInputStream(System.getenv("KEY_STORE") + "/datos.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**Obtenemos los parametros definidos en el archivo*/
        String nombre = propiedades.getProperty("nombre");

        String hostName = propiedades.getProperty("hostName");
        String sid = propiedades.getProperty("sid");
        String userName = propiedades.getProperty("userName");
        String password = propiedades.getProperty("password");

        return getPostgresConnection(hostName, sid, userName, password);
    }

    public static Connection getPostgresConnection(String hostName, String sid,
                                                   String userName, String password) throws ClassNotFoundException,
            SQLException {

        Class.forName("org.postgresql.Driver");
        // Example: jdbc:oracle:thin:@localhost:1521:db11g
        String connectionURL = "jdbc:postgresql://" + hostName + ":5432/" + sid;
        Connection conn = DriverManager.getConnection(connectionURL, userName,
                password);
        return conn;
    }
}
