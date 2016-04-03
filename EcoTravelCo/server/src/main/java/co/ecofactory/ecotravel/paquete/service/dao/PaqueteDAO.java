package co.ecofactory.ecotravel.paquete.service.dao;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PaqueteDAO {
    private JDBCClient dataAccess;

    public PaqueteDAO(Vertx vertx, JsonObject conf) {
        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    //Listar paises
    public CompletableFuture<List<JsonObject>> ListarPaquetes(JsonObject usuario) {
        int idUsuario = usuario.getInteger("id_usuario", 0);

        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select * from mp_producto p inner join mp_tipo_producto tp on tp.id=p.tipo_producto_id where p.tipo_producto_id=5\n" +
                "and p.id=1000 and id_usuario="+idUsuario;
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
                    try {
                        conn.result().close();
                    } catch (Exception e) {
                    }
                }
        );
        return res;
    }


}
