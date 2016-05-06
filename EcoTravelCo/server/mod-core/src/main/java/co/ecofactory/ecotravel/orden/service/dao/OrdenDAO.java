package co.ecofactory.ecotravel.orden.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrdenDAO {
    private JDBCClient dataAccess;

    public OrdenDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    public CompletableFuture<List<JsonObject>> listarOrden(int idPersona) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT mp_orden.*, sum(mp_orden_item.cantidad) as cantidad " +
                "FROM mp_orden INNER JOIN mp_orden_item ON mp_orden.id = mp_orden_item.id_orden_id " +
                "WHERE mp_orden.id_cliente_id = ? and mp_orden.estado <> " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO') " +
                "GROUP BY mp_orden.id, mp_orden.estado, mp_orden.preciototal";
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
        });
        return res;
    }

    public CompletableFuture<List<JsonObject>> detalleOrden(int idOrden) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select mp_orden.preciototal as precioCanasta, mp_orden_item.id as id_item, mp_orden_item.cantidad, mp_galeria.url, " +
                "mp_producto.id as id_producto, mp_producto.nombre, mp_producto.descripcion, mp_producto.precio, (mp_producto.precio * mp_orden_item.cantidad) as precio_total, " +
                "mp_orden.estado, mp_orden_item.calificado from mp_producto inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id " +
                "inner join mp_galeria on mp_producto.id = mp_galeria.producto_id " +
                "where mp_orden.id = ? and mp_galeria.foto_principal = 1";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idOrden);

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


    public CompletableFuture<JsonObject> cancelarOrden(int idOrden) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_orden SET estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CANCELADO')" +
                " WHERE mp_orden.id = ? AND mp_orden.estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'PENDIENTE')";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, idOrden);

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

    public CompletableFuture<JsonObject> pagarOrden(int idOrden) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_orden SET estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'PAGO')" +
                " WHERE mp_orden.id = ? AND mp_orden.estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'PENDIENTE')";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, idOrden);

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

    public CompletableFuture<JsonObject> devolverDisponibilidadProductos(int idOrden) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_producto SET cantidad_actual = mp_producto.cantidad_actual + " +
                "(SELECT SUM(item.cantidad) FROM mp_orden_item AS item WHERE item.id_producto_id = mp_producto.id " +
                "and item.id_orden_id = mp_orden.id GROUP BY item.id_producto_id) " +
                "FROM mp_producto producto INNER JOIN mp_orden_item ON producto.id = mp_orden_item.id_producto_id " +
                "INNER JOIN mp_orden ON mp_orden.id = mp_orden_item.id_orden_id " +
                "WHERE mp_orden.id = ? and mp_orden.estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CANCELADO')" +
                " AND producto.id = mp_producto.id";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, idOrden);

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
}
