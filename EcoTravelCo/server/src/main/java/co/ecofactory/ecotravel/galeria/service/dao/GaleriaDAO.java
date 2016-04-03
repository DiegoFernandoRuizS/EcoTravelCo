package co.ecofactory.ecotravel.galeria.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GaleriaDAO {
    private JDBCClient dataAccess;

    public GaleriaDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    public CompletableFuture<List<JsonObject>> listarGaleria(int idProducto) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select g.* from mp_galeria g inner join mp_producto p on p.id=g.producto_id where p.id= ?";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idProducto);

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
                    try {
                        conn.result().close();
                    } catch (Exception e) {

                    }
                }
        );
        return res;
    }


    //Insertar Imagen producto
    public CompletableFuture<JsonObject> insertarImagen(String imagen, int idProducto, int fotoPrincipal) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        JsonUtils.add(params, "Imagen");
        JsonUtils.add(params, imagen);
        JsonUtils.add(params, "");
        JsonUtils.add(params, idProducto);
        JsonUtils.add(params, fotoPrincipal);

        String query = "INSERT INTO mp_galeria(id, tipo, url, descripcion, producto_id, foto_principal) " +
                "VALUES (nextval('mp_galeria_id_seq'), ?, ?, ?, ?, ?);";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar Galeria en DAO Galeria");
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

    //Eliminar Imagen producto
    public CompletableFuture<JsonObject> eliminarImagen(int idImagen) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();
        JsonUtils.add(params, idImagen);

        String query = "DELETE FROM mp_galeria WHERE id = ?;";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error eliminar Galeria en DAO Galeria");
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

    //Consultar Imagen
    public CompletableFuture<JsonObject> consultarImagen(int idGaleria) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        JsonUtils.add(params, idGaleria);

        String query = "SELECT * FROM mp_galeria WHERE id = ?";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().queryWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar Galeria en DAO Galeria");
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

    //Asignar Nueva Imagen Principal
    public CompletableFuture<JsonObject> asignarImagenPrincipal(int idProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        JsonUtils.add(params, idProducto);

        String query = "UPDATE mp_galeria SET foto_principal = 1 WHERE id = " +
                "(SELECT id FROM mp_galeria WHERE producto_id = ? ORDER BY id LIMIT 1)";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().queryWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar Galeria en DAO Galeria");
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
