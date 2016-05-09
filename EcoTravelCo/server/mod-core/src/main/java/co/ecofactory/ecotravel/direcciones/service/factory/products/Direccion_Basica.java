/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;
import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.concurrent.CompletableFuture;

public class Direccion_Basica extends Direccion{

    public Direccion_Basica(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }


    public int getType() {

        return 0;
    }

    //Insertar DireccionAsociada al producto
    public CompletableFuture<JsonObject> insertarDireccion(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params2 = new JsonArray();
        String direccion = nuevoProducto.getString("nombredireccion", "");
        String pais = nuevoProducto.getString("pais", "");
        String departamento = nuevoProducto.getString("departamento", "");
        String ciudad = nuevoProducto.getString("ciudad", "");

        JsonUtils.add(params2, direccion);
        JsonUtils.add(params2, ciudad);
        JsonUtils.add(params2, departamento);
        JsonUtils.add(params2, pais);


        String query2 = "INSERT INTO mp_direccion(\n" +
                "            id, nombre, ciudad, departamento, pais)\n" +
                "    VALUES (nextval('mp_direccion_id_seq'),\n" +
                "     ?,\n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?);\n";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query2, params2, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar direccion en DAO producto");
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }
            try {
                conn.result().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return res;
    }


    //Actualizar DireccionAsociada al producto
    public CompletableFuture<JsonObject> actualizarDireccion(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params2 = new JsonArray();

        int id = nuevoProducto.getInteger("id_direccion", 0);
        String direccion = nuevoProducto.getString("nombredireccion", "");
        double latitud = Double.parseDouble(nuevoProducto.getString("latitud", ""));

        double longitud = Double.parseDouble(nuevoProducto.getString("longitud", ""));
        String pais = nuevoProducto.getString("pais", "");
        String departamento = nuevoProducto.getString("departamento", "");
        String ciudad = nuevoProducto.getString("ciudad", "");

        JsonUtils.add(params2, direccion);
        JsonUtils.add(params2, ciudad);
        JsonUtils.add(params2, departamento);
        JsonUtils.add(params2, pais);


        String query2 = "UPDATE mp_direccion\n" +
                "   SET  nombre=?, ciudad=?, departamento=?, \n" +
                "       pais=?\n" +
                " WHERE id=" + id;

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query2, params2, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar direccion en DAO producto");
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }
            try {
                conn.result().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return res;
    }


}
