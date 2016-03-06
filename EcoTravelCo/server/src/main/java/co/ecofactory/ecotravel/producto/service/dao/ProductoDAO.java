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

    public CompletableFuture<List<JsonObject>> listarProductosHome() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select a.*, b.*, c.* from mp_producto a left join mp_tipo_producto b on a.tipo_producto_id=b.id left join mp_galeria c on a.id=c.producto_id";
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

    public CompletableFuture<List<JsonObject>> listarProductosDetalle(String id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        System.out.println("entro");
        String query = "select a.*, b.*, c.* from mp_producto a left join mp_tipo_producto b on a.tipo_producto_id=b.id left join mp_galeria c on a.id=c.producto_id where a.id="+id;
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

    //Listar un producto
    public CompletableFuture<List<JsonObject>> listarProducto(Long id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT * FROM public.mp_producto where id = " + id;
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta listar producto" + data.result().getRows());
                            } else {
                                data.cause().printStackTrace();
                                res.completeExceptionally(data.cause());
                            }
                        });
                    } else {
                        conn.cause().printStackTrace();
                    }
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }

    //Insertar un producto
    public CompletableFuture<List<JsonObject>> insertarProducto() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT * FROM public.mp_producto where id=1000";
        JsonArray params = new JsonArray();

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta insertar producto DAO" + data.result().getRows());
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error insertar producto DAO print");
                                res.completeExceptionally(data.cause());
                            }
                        });
                    } else {
                        conn.cause().printStackTrace();
                    }
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }


}
