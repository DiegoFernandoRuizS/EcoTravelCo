package co.ecofactory.ecotravel.canasta.service.dao;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CanastaDAO {
    private JDBCClient dataAccess;

    public CanastaDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    public CompletableFuture<List<JsonObject>> listarCanasta(String id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select mp_orden.*, mp_orden_item.*, mp_producto.* from mp_producto " +
                "inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id " +
                "where mp_orden.estado = 'CANASTA' and mp_orden.id_cliente_id = '" + id + "'";
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


    public CompletableFuture<List<JsonObject>> agregarCanasta() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select mp_orden.*, mp_orden_item.*, mp_producto.* from mp_producto " +
                "inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id " +
                "where mp_orden.estado = 'CANASTA' and mp_orden.id_cliente_id = 1";
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


    public CompletableFuture<List<JsonObject>> quitarCanasta() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select mp_orden.*, mp_orden_item.*, mp_producto.* from mp_producto " +
                "inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id " +
                "where mp_orden.estado = 'CANASTA' and mp_orden.id_cliente_id = 1";
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


