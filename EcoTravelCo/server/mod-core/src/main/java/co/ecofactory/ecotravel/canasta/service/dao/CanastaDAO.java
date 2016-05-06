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
        String query = "select mp_orden.preciototal as precioCanasta, mp_orden_item.id as id_item, mp_orden_item.cantidad, mp_galeria.url, mp_orden.id as idOrden, " +
                "mp_producto.id as id_producto, mp_producto.nombre, mp_producto.descripcion, mp_producto.precio, (mp_producto.precio * mp_orden_item.cantidad) as precio_total " +
                "from mp_producto inner join mp_orden_item on mp_producto.id = mp_orden_item.id_producto_id " +
                "inner join mp_orden on mp_orden_item.id_orden_id = mp_orden.id inner join mp_galeria on mp_producto.id = mp_galeria.producto_id " +
                "where mp_orden.estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO') " +
                "and mp_orden.id_cliente_id = ? and mp_galeria.foto_principal = 1";
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
                "(SELECT mp_orden.id FROM mp_orden where id_cliente_id = ? and estado = " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO')))";
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
                "WHERE id_cliente_id = ? and estado = " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO'))";
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
                "WHERE  id_cliente_id = ? AND estado = " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO')";
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

    public CompletableFuture<JsonObject> restarValorCanasta(int idItemCanasta){
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "UPDATE mp_orden SET preciototal = preciototal - " +
                "(SELECT cantidad * precio FROM mp_orden_item INNER JOIN mp_producto " +
                "ON mp_orden_item.id_producto_id = mp_producto.id WHERE mp_orden_item.id = ?) " +
                "WHERE ID = (SELECT id_orden_id FROM mp_orden_item where mp_orden_item.id = ?)";
        JsonArray params = new JsonArray();
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
        return res;
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

    public CompletableFuture<List<JsonObject>> validarDisponibilidadProductos(int idPersona) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT mp_orden_item.id_producto_id, sum(mp_orden_item.cantidad), " +
                "mp_producto.cantidad_actual, mp_producto.estado FROM mp_orden " +
                "INNER JOIN mp_orden_item ON mp_orden.id = mp_orden_item.id_orden_id " +
                "INNER JOIN mp_producto ON mp_orden_item.id_producto_id = mp_producto.id " +
                "WHERE mp_orden.id_cliente_id = ? and mp_orden.estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO') " +
                "GROUP BY mp_orden_item.id_producto_id, mp_producto.cantidad_actual, mp_producto.estado " +
                "HAVING sum(mp_orden_item.cantidad) > mp_producto.cantidad_actual OR mp_producto.estado = 'Inactivo'";

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

    public CompletableFuture<JsonObject> descontarCantidadProductos(int idUsuario) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_producto SET cantidad_actual = mp_producto.cantidad_actual - " +
                "(SELECT SUM(item.cantidad) FROM mp_orden_item AS item WHERE item.id_producto_id = mp_producto.id " +
                "and item.id_orden_id = mp_orden.id GROUP BY item.id_producto_id)\n" +
                "FROM mp_producto producto INNER JOIN mp_orden_item ON producto.id = mp_orden_item.id_producto_id " +
                "INNER JOIN mp_orden ON mp_orden.id = mp_orden_item.id_orden_id " +
                "WHERE mp_orden.id_cliente_id = ? and mp_orden.estado = " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO') AND producto.id = mp_producto.id";

        JsonArray params = new JsonArray();
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


    public CompletableFuture<JsonObject> confirmarCanasta(int idUsuario) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "UPDATE mp_orden SET estado = (SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'PENDIENTE') " +
                "WHERE mp_orden.id_cliente_id = ? and mp_orden.estado = " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO')";

        JsonArray params = new JsonArray();
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
}
