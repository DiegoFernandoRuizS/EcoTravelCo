package co.ecofactory.ecotravel.dao;

import co.ecofactory.ecotravel.conexion.ConnectionUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CristianCamilo on 15/05/2016.
 */
public class LogDAO {
    private Connection conn;

    public void writeLog(int idProducto, String tipo, int calificacion, int cantidad, int idCliente) {
        Statement stmt = null;
        try {
            Connection conn = ConnectionUtils.getConnection();
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO public.mp_log(id_producto_id, fecha, tipo, calificacion, cantidad, id_cliente_id) " +
                    "VALUES (" + idProducto + ", current_timestamp, '" + tipo + "', " + calificacion + ", " + cantidad + ", "+ idCliente +");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList getProductosOrden(int idOrden) {
        Statement stmt = null;
        ArrayList resp = new ArrayList();
        try {
            Connection conn = ConnectionUtils.getConnection();
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id_producto_id, sum(cantidad) as cantidad, id_cliente_id from mp_orden_item inner join mp_orden on " +
                    "mp_orden.id = id_orden_id WHERE id_orden_id = "+idOrden+" GROUP BY id_producto_id, id_cliente_id";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //Retrieve by column name
                int id = rs.getInt("id_producto_id");
                int cantidad = rs.getInt("cantidad");
                int idCliente = rs.getInt("id_cliente_id");
                resp.add(id+"-"+cantidad+"-"+idCliente);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resp;
    }
}
