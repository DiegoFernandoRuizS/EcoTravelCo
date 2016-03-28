package co.ecofactory.ecotravel.utils.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class UtilidadDatosDAO {
    private JDBCClient dataAccess;

    public UtilidadDatosDAO(Vertx vertx, JsonObject conf) {
        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    //Listar paises
    public CompletableFuture<List<JsonObject>> listaPaises() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT id, tipo, codigo, UPPER(valor) as valor, id_padre\n" +
                "  FROM mp_lista_valores ORDER BY VALOR ASC;";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("paises " + res);

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

    //Listar tipo producto
    public CompletableFuture<List<JsonObject>> listaTipoProducto() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select * from mp_tipo_producto order by tipo asc;";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("tipos producto " + res);

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
