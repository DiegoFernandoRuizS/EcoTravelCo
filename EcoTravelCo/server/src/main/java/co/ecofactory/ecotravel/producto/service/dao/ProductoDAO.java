package co.ecofactory.ecotravel.producto.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import jdk.nashorn.internal.objects.NativeURIError;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class ProductoDAO {
    private JDBCClient dataAccess;

    public ProductoDAO(Vertx vertx, JsonObject conf) {
        dataAccess = JDBCClient.createShared(vertx, conf);
    }

    //Listar productos
    public CompletableFuture<List<JsonObject>> listarProductos() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT p.id, p.estado, p.nombre, p.fecha_registro, p.fecha_actualizacion, p.calificacion_promedio, \n" +
                "       p.id_padre,p.id_direccion_id, tp.tipo, p.descripcion, p.precio\n" +
                "  FROM public.mp_producto p, public.mp_tipo_producto tp\n" +
                "  where p.tipo_producto_id=tp.id;";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta " + res);

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

    public CompletableFuture<List<JsonObject>> listarProductosHome() {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select a.*, b.*, c.* from mp_producto a left join mp_tipo_producto b on a.tipo_producto_id=b.id left join mp_galeria c on a.id=c.producto_id";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta " + res);
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
                        e.printStackTrace();
                    }
                }
        );
        return res;
    }

    public CompletableFuture<List<JsonObject>> listarProductosDetalle(String id) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        System.out.println("entro");
        String query = "select a.*, b.*, c.* from mp_producto a left join mp_tipo_producto b on a.tipo_producto_id=b.id left join mp_galeria c on a.id=c.producto_id where a.id=" + id;
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta " + res);

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

    //Listar un producto
    public CompletableFuture<List<JsonObject>> listarProducto(Long id) {

        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT p.id, p.estado, p.nombre, p.fecha_registro, p.fecha_actualizacion, p.calificacion_promedio, \n" +
                "       p.id_padre,p.id_direccion_id, tp.tipo, p.descripcion, p.precio\n" +
                "  FROM public.mp_producto p, public.mp_tipo_producto tp\n" +
                "  where p.tipo_producto_id=tp.id and p.id = " + id;
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta listar producto" + data.result().getRows().size());
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
                        e.printStackTrace();
                    }
                }
        );
        return res;
    }

    //Insertar un producto
    public CompletableFuture<JsonObject> insertarProducto(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();


        JsonUtils.add(params, nuevoProducto.getString("estado", ""));
        JsonUtils.add(params, nuevoProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, 5.0D);
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_padre", "")));
        // JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_direccion_id", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("tipo_producto_id", "")));
        JsonUtils.add(params, nuevoProducto.getString("descripcion", ""));
        JsonUtils.add(params, Double.parseDouble(nuevoProducto.getString("precio", "")));
        JsonUtils.add(params, 1);//Usuario quemado
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));

       // System.out.println("LA IMAGEN LLEGA? " + nuevoProducto.getString("imagen", ""));

        String query = "INSERT INTO mp_producto(\n" +
                "            id, estado, nombre, fecha_registro, fecha_actualizacion, calificacion_promedio, \n" +
                "            id_padre, id_direccion_id, tipo_producto_id, descripcion, precio,id_usuario,cantidad_actual,cantidad_origen)\n" +
                "    VALUES (nextval('mp_producto_id_seq'), \n" +
                "    ?, \n" +
                "    ?, \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "     ?, \n" +
                "     ?, \n" +
                "     (SELECT max(id) FROM mp_direccion), \n" +
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
                        e.printStackTrace();
                    }
                }
        );
        return res;
    }


    //Insertar DireccionAsociada al producto
    public CompletableFuture<JsonObject> insertarDireccion(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params2 = new JsonArray();

        String direccion = nuevoProducto.getString("nombredireccion", "");
        String latitud = nuevoProducto.getString("latitud", "");
        String longitud = nuevoProducto.getString("longitud", "");
        String pais = nuevoProducto.getString("pais", "");
        String departamento = nuevoProducto.getString("departamento", "");
        String ciudad = nuevoProducto.getString("ciudad", "");

        System.out.println("------> direccion "+direccion);

        System.out.println("------> latitud  "+latitud);

        JsonUtils.add(params2, direccion);
        JsonUtils.add(params2, Double.parseDouble(latitud));
        JsonUtils.add(params2, Double.parseDouble(longitud));
        JsonUtils.add(params2, ciudad);
        JsonUtils.add(params2, departamento);
        JsonUtils.add(params2, pais);


        String query2 = "INSERT INTO mp_direccion(\n" +
                "            id, nombre, latitud, longitud, ciudad, departamento, pais)\n" +
                "    VALUES (nextval('mp_direccion_id_seq'),\n" +
                "     ?,\n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?);\n";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query2, params2, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar direccion en DAO producto");
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


    //Insertar ImagenAsociada al producto
    public CompletableFuture<JsonObject> insertarImagen(JsonObject nuevoProducto,int productoAsociado) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params3 = new JsonArray();

        String imagen = nuevoProducto.getString("imagen", "");
        String tipo ="Imagen" ;
        String ciudad = nuevoProducto.getString("ciudad", "");

        System.out.println("EN EL DAO DE LA IMAGEN");
        JsonUtils.add(params3,tipo);
        JsonUtils.add(params3, imagen);
        JsonUtils.add(params3, ciudad);
        JsonUtils.add(params3, productoAsociado);



        String query3 = "INSERT INTO mp_galeria(\n" +
                "            id, tipo, url, descripcion, producto_id, foto_principal)\n" +
                "    VALUES (nextval('mp_galeria_id_seq'), \n" +
                "    ?, \n" +
                "    ?, \n" +
                "    ?,\n" +
                "    ?, \n" +
                "     nextval('mp_galeria_id_seq'));\n";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query3, params3, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar Galeria en DAO producto");
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

    //Editar un producto
    public CompletableFuture<JsonObject> editarProducto(JsonObject editProducto, Long id) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();
        System.out.println("Que ID llega al DAO " + id);
        JsonUtils.add(params, editProducto.getString("estado", ""));
        JsonUtils.add(params, editProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());
        JsonUtils.add(params, editProducto.getDouble("calificacion_promedio", 0.0));
        JsonUtils.add(params, editProducto.getInteger("id_padre", 0));
        JsonUtils.add(params, editProducto.getInteger("id_direccion_id", 0));
        JsonUtils.add(params, editProducto.getInteger("tipo_producto_id", 0));
        JsonUtils.add(params, editProducto.getString("descripcion", ""));
        JsonUtils.add(params, editProducto.getDouble("precio", 0D));

        String query = "UPDATE public.mp_producto\n" +
                "   SET estado=?, nombre=?, fecha_actualizacion=to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "       calificacion_promedio=?, id_padre=?, id_direccion_id=?, tipo_producto_id=?,descripcion=?,precio=?\n" +
                " WHERE id=" + id + ";";

        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Editar producto DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error editar producto DAO print");
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

    //Borrar un producto
    public CompletableFuture<JsonObject> borrarProducto(Long id) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        String query = "DELETE FROM public.mp_producto\n" +
                " WHERE id=" + id + ";";

        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Borrar producto DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error Borrar producto DAO print");
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
    //Borrar imagen
    public CompletableFuture<JsonObject> borrarImagen(Long id) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        String query = "DELETE FROM mp_galeria\n" +
                " WHERE producto_id="+id+";";

        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Borrar imagen DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error Borrar imagen DAO print");
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

    //Borrar un Direccion
    public CompletableFuture<JsonObject> borrarDireccion(Long id) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        String query = "DELETE FROM mp_galeria\n" +
                " WHERE producto_id="+id+";";

        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Borrar producto DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error Borrar producto DAO print");
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
