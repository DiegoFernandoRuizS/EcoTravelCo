package co.ecofactory.ecotravel.conexion;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by CristianCamilo on 15/05/2016.
 */
public class ConnectionUtils {
    public static Connection getConnection() throws SQLException,
            ClassNotFoundException {

        return PostgresConnUtils.getPostgresConnection();
    }
}
