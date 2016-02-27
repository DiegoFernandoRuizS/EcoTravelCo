package co.ecofactory.ecotravel.producto.service.dao;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ProductoDAO {
    private JDBCClient dataAccess;

    public ProductoDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    public CompletableFuture<List<JsonObject>> listarProductos() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select * from mp_producto";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta " +res);

                            } else {
                                data.cause().printStackTrace();
                                res.completeExceptionally(data.cause());
                            }
                        });
                    } else {
                        conn.cause().printStackTrace();
                    }
                    try{
                        conn.result().close();
                    }catch(Exception e){

                    }
                }
        );
        return res;
    }
}
