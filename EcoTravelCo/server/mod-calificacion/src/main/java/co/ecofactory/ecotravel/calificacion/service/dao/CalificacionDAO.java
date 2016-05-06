package co.ecofactory.ecotravel.calificacion.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CalificacionDAO {
    private JDBCClient dataAccess;

    public CalificacionDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    public CompletableFuture<JsonObject> calificarServicio(int idProducto, int idCliente, int calificacion, String comentario) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "INSERT INTO mp_calificacion VALUES(nextval('mp_calificacion_id_seq'), ?, current_timestamp, ?, ?, ?)";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, calificacion);
        JsonUtils.add(params, comentario);
        JsonUtils.add(params, idCliente);
        JsonUtils.add(params, idProducto);

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
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }

    public CompletableFuture<JsonObject> actualizacionPromedio(int idProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_producto SET calificacion_promedio = " +
                "(SELECT ROUND(AVG(mp_calificacion.calificacion)) FROM mp_calificacion " +
                "WHERE mp_calificacion.id_producto_id = ?) WHERE mp_producto.id = ?";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, idProducto);
        JsonUtils.add(params, idProducto);

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
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }

    public CompletableFuture<JsonObject> marcarOrdenItem(int idItem) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_orden_item SET calificado = TRUE WHERE mp_orden_item.id_orden_id IN (" +
                "SELECT mp_orden_item.id_orden_id FROM mp_orden_item WHERE mp_orden_item.id = ?) AND " +
                "mp_orden_item.id_producto_id = (SELECT mp_orden_item.id_producto_id FROM mp_orden_item " +
                "WHERE mp_orden_item.id = ?)";

        JsonArray params = new JsonArray();
        JsonUtils.add(params, idItem);
        JsonUtils.add(params, idItem);

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
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }

    public CompletableFuture<List<JsonObject>> listarCalificacion(String id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select c.calificacion, to_char(c.fecha, 'YYYY-MM-DD HH24:MI:SS') as fecha, c.comentario ,pe.foto\n" +
                ",(case when pe.nombre isnull then '' else (pe.nombre)|| ' ' end)||(case when pe.nombre_sec isnull then '' else (pe.nombre_sec)|| ' ' end)||(case when pe.apellido isnull then '' else (pe.apellido)|| ' ' end)||(case when pe.apellido_sec isnull then '' else (pe.apellido_sec) end) as usuario\n" +
                "  from mp_calificacion c left join mp_persona pe on pe.id=c.id_cliente_id where id_producto_id=" + id + ";";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                //   System.out.println("En el If respuesta " + res);

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
}
