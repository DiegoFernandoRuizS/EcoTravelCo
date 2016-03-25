package co.ecofactory.ecotravel.usuario.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsuarioDAO {
    private JDBCClient dataAccess;

    public UsuarioDAO(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }


    public CompletableFuture<JsonObject> consultarUsuarioPorLogin(String login) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "SELECT * FROM public.mp_persona where login = ?";
        JsonArray params = new JsonArray();
        params.add(login);
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {

                                JsonObject usuario = null;

                                if (data.result() != null && data.result().getRows() != null && data.result().getRows().size() > 0) {
                                    usuario = data.result().getRows().get(0);
                                }
                                res.complete(usuario);
                            } else {
                                data.cause().printStackTrace();
                                res.completeExceptionally(data.cause());
                            }
                        });
                    } else {
                        conn.cause().printStackTrace();
                        res.completeExceptionally(conn.cause());
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
    public CompletableFuture<JsonObject> insertarUsuario(JsonObject dataIn) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "insert into public.mp_persona (nombre,nombre_sec,apellido,apellido_sec,telefono,correo_electronico,tipo,foto,fecha_registro,fecha_actualizacion,login,contrasenia,id_direccion_id) " +
                "values (?,?,?,?,?,?,?,?,to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'),to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'),?,?,?)";
        JsonArray params = new JsonArray();
        JsonUtils.add(params, dataIn.getString("nombre"));
        JsonUtils.add(params, dataIn.getString("nombre_sec"));
        JsonUtils.add(params, dataIn.getString("apellido"));
        JsonUtils.add(params, dataIn.getString("apellido_sec"));
        JsonUtils.add(params, dataIn.getInteger("telefono"));
        JsonUtils.add(params, dataIn.getString("correo_electronico"));
        JsonUtils.add(params, dataIn.getString("tipo"));
        JsonUtils.add(params, dataIn.getString("foto"));
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, dataIn.getString("login"));
        JsonUtils.add(params, dataIn.getString("contrasenia"));
        JsonUtils.add(params, dataIn.getString("id_direccion_id"));

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
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
