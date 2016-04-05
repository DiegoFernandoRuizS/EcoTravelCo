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
        String query = "select *,p.id as idProducto,p.descripcion as descrippaquete from mp_producto p inner join mp_tipo_producto tp on tp.id=p.tipo_producto_id where p.tipo_producto_id=5 and id_usuario=\n" + idUsuario;
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

    //BorrarPaquete
    public CompletableFuture<JsonObject> borrarPaquete(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        JsonArray params = new JsonArray();

       // int idUsuario = nuevoProducto.getInteger("id_usuario", 0);
        int idPaquete=nuevoProducto.getInteger("idPaquete",0);

        String query = "delete from mp_producto mp where mp.id="+idPaquete;
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

    public CompletableFuture<JsonObject> borrarGaleriaPaquete(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        JsonArray params = new JsonArray();

        // int idUsuario = nuevoProducto.getInteger("id_usuario", 0);
        int idPaquete=nuevoProducto.getInteger("idPaquete",0);

        String query = "delete from mp_galeria mp where mp.producto_id="+idPaquete;
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error borrar galeria paquete DAO print");
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


    public CompletableFuture<List<JsonObject>> listarPaquetesDetalle(Long id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        System.out.println("entro id = " + id);
        String query = "select a.id, a.nombre, to_char(a.fecha_registro, 'YYYY-MM-DD') as fecha_registro, a.calificacion_promedio, a.descripcion, a.precio, a.cantidad_actual, a.caracteristicas\n" +
                ", b.tipo\n" +
                ", (case when pe.nombre isnull then '' else (pe.nombre)|| ' ' end)||(case when pe.nombre_sec isnull then '' else (pe.nombre_sec)|| ' ' end)||(case when pe.apellido isnull then '' else (pe.apellido)|| ' ' end)||(case when pe.apellido_sec isnull then '' else (pe.apellido_sec) end) as vendedor, \n" +
                "  ( d.nombre ||' , '|| d.pais ||' , '|| d.departamento||' , '|| d.ciudad) as direccion, a.estado, d.latitud, d.longitud, d.pais, d.departamento, d.ciudad, d.nombre as nombredireccion, d.id as id_direccion\n" +
                "from mp_producto a left join mp_tipo_producto b on a.tipo_producto_id=b.id \n" +
                "left join mp_galeria c on c.producto_id=a.id and c.foto_principal=1 \n" +
                "left join mp_persona pe on pe.id= a.id_usuario\n" +
                "left join mp_direccion d on d.id= a.id_direccion_id\n" +
                "where a.id=\n" + id + ";";
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

    public CompletableFuture<List<JsonObject>> listarHijos(Long id){
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        System.out.println("entro id = " + id);
        String query = "SELECT id, estado, nombre, fecha_registro, fecha_actualizacion, calificacion_promedio, \n" +
                "       id_padre, id_direccion_id, tipo_producto_id, descripcion, precio, \n" +
                "       id_usuario, cantidad_actual, cantidad_origen, caracteristicas\n" +
                "  FROM mp_producto\n" +
                "  where id_padre=" + id + ";";
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

    public CompletableFuture<JsonObject> editarPaquete(JsonObject nuevoProducto, int productoAsociado) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        int idUsuario = nuevoProducto.getInteger("id_usuario", 0);

        JsonUtils.add(params, nuevoProducto.getString("estado", ""));
        JsonUtils.add(params, nuevoProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());
        String tipo3 = nuevoProducto.getString("tipo", "");

        int tipo_producto_id = 0;

        if (tipo3.equals("Paquete")) {
            tipo_producto_id = 5;
        }

        JsonUtils.add(params, tipo_producto_id);
        JsonUtils.add(params, nuevoProducto.getString("descripcion", ""));
        JsonUtils.add(params, Double.parseDouble(nuevoProducto.getString("precio", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));



        int idProducto = nuevoProducto.getInteger("id", 0);

        String query = "UPDATE mp_producto\n" +
                "   SET estado=?, nombre=?, fecha_actualizacion=to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "        tipo_producto_id=?, \n" +
                "       descripcion=?, precio=?, cantidad_origen=?\n " +
                " WHERE id=" + idProducto;

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error actualizar producto DAO print");
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
}
