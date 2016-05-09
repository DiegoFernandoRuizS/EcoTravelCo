/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.concurrent.CompletableFuture;


public abstract class Direccion {

    public JDBCClient dataAccess;

    private int tipo;

    public abstract int getType();

    public abstract CompletableFuture<JsonObject> insertarDireccion(JsonObject nuevoProducto) ;
    public abstract CompletableFuture<JsonObject> actualizarDireccion(JsonObject nuevoProducto);

    //Borrar un Direccion
    public CompletableFuture<JsonObject> borrarDireccion(Long id) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        String query = "DELETE FROM mp_direccion WHERE id in (SELECT id_direccion_id from mp_producto where id =" + id + ");";

        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Borrar direccion DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error Borrar direccion DAO print");
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
                }
        );
        return res;
    }


}