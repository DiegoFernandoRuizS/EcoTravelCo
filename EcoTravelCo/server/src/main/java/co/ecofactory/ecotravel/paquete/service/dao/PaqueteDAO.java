package co.ecofactory.ecotravel.paquete.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PaqueteDAO {
    private JDBCClient dataAccess;

    public PaqueteDAO(Vertx vertx, JsonObject conf) {
        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    //Listar paquetes
    public CompletableFuture<List<JsonObject>> ListarPaquetes(JsonObject usuario) {
        int idUsuario = usuario.getInteger("id_usuario", 0);

        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select * from mp_producto p inner join mp_tipo_producto tp on tp.id=p.tipo_producto_id where p.tipo_producto_id=5\n" +
                "and id_usuario=" + idUsuario;
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

    //InsertarPaquete
    public CompletableFuture<JsonObject> insertarPaquete(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del paquete
        JsonArray params = new JsonArray();

        int idUsuario = nuevoProducto.getInteger("id_usuario", 0);

        JsonUtils.add(params, nuevoProducto.getString("estado", ""));
        JsonUtils.add(params, nuevoProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, 5.0D);
        //JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_padre", "")));
        // JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_direccion_id", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("tipo_producto_id", "")));
        JsonUtils.add(params, nuevoProducto.getString("descripcion", ""));
        JsonUtils.add(params, Double.parseDouble(nuevoProducto.getString("precio", "")));
        JsonUtils.add(params, idUsuario);
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));

        String query = "INSERT INTO mp_producto(\n" +
                "            id, estado, nombre, fecha_registro, fecha_actualizacion, calificacion_promedio, \n" +
                "            id_padre, id_direccion_id, tipo_producto_id, descripcion, precio,id_usuario,cantidad_actual,cantidad_origen)\n" +
                "    VALUES (nextval('mp_producto_id_seq'), \n" +
                "    ?, \n" +
                "    ?, \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "     ?, \n" +
                "     null, \n" +
                "     null, \n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?,?,?,?);";

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error insertar paquete DAO print");
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
                }
        );
        return res;
    }

    //asociarProductosAPaquete
    public CompletableFuture<JsonObject> asociarProductosAPaquete(JsonObject nuevoProducto,int idPadre) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        for (int i = 0; i < nuevoProducto.getJsonArray("productos").size(); i++) {
            JsonArray params = new JsonArray();
            JsonObject produ= new JsonObject();
            produ= (JsonObject) nuevoProducto.getJsonArray("productos").getValue(i);
            System.out.println("id del producto asociado "+nuevoProducto.getJsonArray("productos").getValue(i));
            System.out.println("-----");
            System.out.println(produ.getInteger("id",0));
            //Asociando el producto
            int productoAsociar = produ.getInteger("id",0);

            int idUsuario = nuevoProducto.getInteger("id_usuario", 0);

            JsonUtils.add(params, idPadre);

            //JsonUtils.add(params, idUsuario);

            String query = "UPDATE mp_producto p\n" +
                    "   SET id_padre=?\n" +
                    " WHERE p.id="+productoAsociar;

            dataAccess.getConnection(conn -> {
                if (conn.succeeded()) {
                    conn.result().updateWithParams(query, params, data -> {
                        if (data.succeeded()) {
                            res.complete(data.result().toJson());
                        } else {
                            data.cause().printStackTrace();
                            System.out.println("Error asociar productos  paquete DAO print");
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
        }
        return res;
    }
}
