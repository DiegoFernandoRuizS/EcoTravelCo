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
    public CompletableFuture<List<JsonObject>> listarProductos(JsonObject usuario) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();

        int idUsuario=usuario.getInteger("id_usuario",0);
        System.out.println("Usuario en el dao "+idUsuario);
        String query = "SELECT p.id, p.estado, p.nombre, p.fecha_registro, p.fecha_actualizacion, p.calificacion_promedio, \n" +
                "                    p.id_padre,p.id_direccion_id, tp.tipo, p.descripcion, p.precio\n" +
                "                FROM public.mp_producto p, public.mp_tipo_producto tp\n" +
                "                 where p.tipo_producto_id=tp.id\n" +
                "                 and p.id_usuario="+idUsuario;
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
        String query = "select  p.id, g.url,  p.nombre,  tp.tipo, p.descripcion, p.precio ,p.fecha_registro\n" +
                "FROM public.mp_producto p inner join  public.mp_tipo_producto tp on tp.id= p.tipo_producto_id inner join mp_persona pe on pe.id= p.id_usuario left join mp_galeria g on g.producto_id =p.id and g.foto_principal=1 \n" +
                "order by p.fecha_registro desc limit 10";
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
        System.out.println("entro id = " + id);
        String query = "select a.id, a.nombre, to_char(a.fecha_registro, 'YYYY-MM-DD') as fecha_registro, a.calificacion_promedio, a.descripcion, a.precio, a.cantidad_actual\n" +
                ", b.tipo\n" +
                ", c.url\n" +
                ", ( pe.nombre ||' '|| pe.nombre_sec ||' '|| pe.apellido||' '|| pe.apellido_sec) as vendedor, pe.foto\n" +
                ",  ( d.nombre ||' , '|| d.pais ||' , '|| d.departamento||' , '|| d.ciudad) as direccion, d.latitud, d.longitud\n" +
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
        String query = "SELECT tp.tipo, p.nombre,p.descripcion, p.precio, p.cantidad_actual as cantidad, p.estado,\n" +
                "dr.nombre as nombredireccion,dr.latitud,dr.longitud,dr.ciudad,dr.departamento,dr.pais,ga.url as imagen,\n" +
                "p.tipo_producto_id as id_tipo_producto,p.id as id_producto,dr.id as id_direccion\n" +
                "  FROM mp_producto p, mp_tipo_producto tp,mp_direccion dr,mp_galeria ga\n" +
                "  where tp.id=p.tipo_producto_id\n" +
                "  and ga.producto_id=p.id\n" +
                "  and p.id_direccion_id=dr.id\n" +
                "  and p.id=" + id;
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
        JsonUtils.add(params, idUsuario);//Usuario quemado
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
                "     null, \n" +
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

        System.out.println("------> direccion " + direccion);

        System.out.println("------> latitud  " + latitud);


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

    //Actualizar DireccionAsociada al producto
    public CompletableFuture<JsonObject> actualizarDireccion(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params2 = new JsonArray();

        int id = nuevoProducto.getInteger("id_direccion",0);
        String direccion = nuevoProducto.getString("nombredireccion", "");
        double latitud = Double.parseDouble(nuevoProducto.getString("latitud", ""));

        double longitud = Double.parseDouble(nuevoProducto.getString("longitud", ""));
        String pais = nuevoProducto.getString("pais", "");
        String departamento = nuevoProducto.getString("departamento", "");
        String ciudad = nuevoProducto.getString("ciudad", "");

        System.out.println("DATOS PARA ACTUALIZAR direccion");

        System.out.println(id);
        System.out.println(direccion);
        System.out.println(latitud);
        System.out.println(longitud);
        System.out.println(pais);
        System.out.println(departamento);
        System.out.println(ciudad);


        JsonUtils.add(params2, direccion);
        JsonUtils.add(params2, (latitud));
        JsonUtils.add(params2, (longitud));
        JsonUtils.add(params2, ciudad);
        JsonUtils.add(params2, departamento);
        JsonUtils.add(params2, pais);


        String query2 = "UPDATE mp_direccion\n" +
                "   SET  nombre=?, latitud=?, longitud=?, ciudad=?, departamento=?, \n" +
                "       pais=?\n" +
                " WHERE id="+id;

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

    //Actualizar ImagenAsociada al producto
    public CompletableFuture<JsonObject> actualizarImagen(JsonObject nuevoProducto, int productoAsociado) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();

        JsonArray params3 = new JsonArray();

        String imagen = nuevoProducto.getString("imagen", "");
        String tipo = "Imagen";
        String ciudad = nuevoProducto.getString("ciudad", "");

        String imagen2 = nuevoProducto.getString("imagen2", "");
        String tipo2 = "Imagen";
        String ciudad2 = nuevoProducto.getString("ciudad", "");

        String imagen3 = nuevoProducto.getString("imagen3", "");
        String tipo3 = "Imagen";
        String ciudad3 = nuevoProducto.getString("ciudad", "");

        System.out.println("EN EL DAO DE LA IMAGEN");
        JsonUtils.add(params3, tipo);
        JsonUtils.add(params3, imagen);
        JsonUtils.add(params3, ciudad);
        JsonUtils.add(params3, productoAsociado);


        String query3 = "UPDATE mp_galeria\n" +
                "   SET tipo=?, url=?, descripcion=?, foto_principal=1\n" +
                " WHERE producto_id="+productoAsociado;

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

    //Insertar ImagenAsociada al producto
    public CompletableFuture<JsonObject> insertarImagen(JsonObject nuevoProducto, int productoAsociado) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params3 = new JsonArray();

        String imagen = nuevoProducto.getString("imagen", "");
        String tipo = "Imagen";
        String ciudad = nuevoProducto.getString("ciudad", "");

        String imagen2 = nuevoProducto.getString("imagen2", "");
        String tipo2 = "Imagen";
        String ciudad2 = nuevoProducto.getString("ciudad", "");

        String imagen3 = nuevoProducto.getString("imagen3", "");
        String tipo3 = "Imagen";
        String ciudad3 = nuevoProducto.getString("ciudad", "");

        System.out.println("EN EL DAO DE LA IMAGEN");
        JsonUtils.add(params3, tipo);
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
                "    1);\n";

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
    public CompletableFuture<JsonObject> editarProducto(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        int idUsuario = nuevoProducto.getInteger("id_usuario", 0);

        int idProducto2 = nuevoProducto.getInteger("id_producto", 0);

        JsonUtils.add(params, nuevoProducto.getString("estado", ""));
        JsonUtils.add(params, nuevoProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());

        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_direccion", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("id_tipo_producto", "")));
        JsonUtils.add(params, nuevoProducto.getString("descripcion", ""));
        JsonUtils.add(params, Double.parseDouble(nuevoProducto.getString("precio", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));

        // System.out.println("LA IMAGEN LLEGA? " + nuevoProducto.getString("imagen", ""));

        int idProducto = nuevoProducto.getInteger("id_producto", 0);

        String query="UPDATE mp_producto\n" +
                "   SET estado=?, nombre=?, fecha_actualizacion=?, \n" +
                "       id_direccion_id=?, tipo_producto_id=?, \n" +
                "       descripcion=?, precio=?, cantidad_origen=?\n" +
                " WHERE id="+idProducto;

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
                " WHERE producto_id=" + id + ";";

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
                " WHERE producto_id=" + id + ";";

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


    //Listar un producto
    public CompletableFuture<List<JsonObject>> listarProductosBusqueda(JsonObject criterios) {
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        System.out.println("ini");
        // criterios.getString("search", "")
        String[] split = criterios.getString("criterios").split("%20");
        String query = "";

        for (int i = 0; split.length > i; i++) {
            String search = split[i];
            if (!search.equals("")) {
                if (i != 0)
                    query += " UNION ";
                query += "SELECT DISTINCT  p.id, g.url,  p.nombre,  tp.tipo, p.descripcion, p.precio , p.cantidad_actual, ( pe.nombre ||' '|| pe.nombre_sec ||' '|| pe.apellido||' '|| pe.apellido_sec) as vendedor\n" +
                        "FROM public.mp_producto p inner join  public.mp_tipo_producto tp on tp.id= p.tipo_producto_id inner join mp_persona pe on pe.id= p.id_usuario left join mp_galeria g on g.producto_id =p.id and g.foto_principal=1 \n" +
                        "where UPPER(p.nombre) like UPPER('%" + search + "%') or\n" +
                        "UPPER(tp.tipo) like UPPER('%" + search + "%') or\n" +
                        "UPPER(tp.descripcion) like UPPER('%" + search + "%') or\n" +
                        "UPPER(p.descripcion) like UPPER('%" + search + "%') or\n" +
                        "UPPER(pe.nombre) like UPPER('%" + search + "%') or\n" +
                        "UPPER(pe.nombre_sec) like UPPER('%" + search + "%') or\n" +
                        "UPPER(pe.apellido) like UPPER('%" + search + "%') or\n" +
                        "UPPER(pe.apellido_sec) like UPPER('%" + search + "%') AND UPPER(p.estado)='ACTIVO';\n";
            }

        }
        final String queryf = query + ";";

        System.out.println("Query" + queryf);
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(queryf, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta listar producto" + data.result().getRows().size());
                            } else {
                                System.out.println("err " + data.cause());
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

}
