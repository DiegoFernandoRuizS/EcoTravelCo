package co.ecofactory.ecotravel.canasta.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
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

    public CompletableFuture<List<JsonObject>> listarCanasta(int idPersona) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select mp_orden.*, mp_orden_item.*, mp_producto.*, mp_galeria.* from mp_producto " +
                "inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id " +
                "left join mp_galeria on mp_producto.id = mp_galeria.producto_id " +
                "where mp_orden.estado = 'CANASTA' and mp_orden.id_cliente_id = ? and mp_galeria.foto_principal = 1";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idPersona);

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
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


    public CompletableFuture<JsonObject> agregarCanasta(int idUsuario, int idProducto, int cantidad) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "INSERT INTO mp_orden_item(id, cantidad, id_producto_id, id_orden_id) " +
                "VALUES (nextval('mp_orden_item_id_seq'), ?, ?, " +
                "(SELECT mp_orden.id FROM mp_orden where id_cliente_id = ? and estado = 'CANASTA'))";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, cantidad);
        JsonUtils.add(params, idProducto);
        JsonUtils.add(params, idUsuario);

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
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


    public CompletableFuture<JsonObject> crearCanasta(int idUsuario){
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "INSERT INTO mp_orden(id, estado, precioTotal, id_cliente_id) " +
                "SELECT nextval('mp_orden_id_seq'), 'CANASTA', 0, ? " +
                "WHERE ? NOT IN (SELECT id_cliente_id FROM mp_orden " +
                "WHERE id_cliente_id = ? and estado = 'CANASTA')";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idUsuario);
        JsonUtils.add(params, idUsuario);
        JsonUtils.add(params, idUsuario);

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }try{
                conn.result().close();
            }catch(Exception e){

            }
        });

        return res;
    }

    public void adicionarValorCanasta(int idUsuario, int idProducto, int cantidad){
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "UPDATE mp_orden SET preciototal = preciototal + ( ? * " +
                "(SELECT precio FROM mp_producto WHERE id = ?)) " +
                "WHERE  id_cliente_id = ? AND estado = 'CANASTA'";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, cantidad);
        JsonUtils.add(params, idProducto);
        JsonUtils.add(params, idUsuario);

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }try{
                conn.result().close();
            }catch(Exception e){

            }
        });
    }

    public void restarValorCanasta(int idItemCanasta){
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "UPDATE mp_orden SET preciototal = preciototal - ( " +
                "(SELECT cantidad FROM mp_orden_item WHERE id = ?) * " +
                "(SELECT precio FROM mp_orden_item INNER JOIN mp_producto " +
                "ON mp_orden_item.id_producto_id = mp_producto.id WHERE mp_orden_item.id = ?) " +
                ") WHERE  id = (SELECT id_orden_id FROM mp_orden_item WHERE id = ?)";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idItemCanasta);
        JsonUtils.add(params, idItemCanasta);
        JsonUtils.add(params, idItemCanasta);

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }try{
                conn.result().close();
            }catch(Exception e){

            }
        });
    }

    public CompletableFuture<JsonObject> eliminarProductoCanasta(int idOrdenItem) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "DELETE FROM mp_orden_item WHERE mp_orden_item.id = ?";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idOrdenItem);

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
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


    public CompletableFuture<List<JsonObject>> modificarProductoCanasta(String idOrdenItem, String cantidad) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "update mp_orden_item set cantidad = '"+cantidad+"' where mp_orden_item.id = '"+idOrdenItem+"';";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
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
