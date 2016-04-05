package co.ecofactory.ecotravel.producto.service.dao;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import jdk.nashorn.internal.objects.NativeURIError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        int idUsuario = usuario.getInteger("id_usuario", 0);
        System.out.println("Usuario en el dao " + idUsuario);
        String query = "SELECT p.id, p.estado, p.nombre, p.fecha_registro, p.fecha_actualizacion, p.calificacion_promedio, \n" +
                "                    p.id_padre,p.id_direccion_id, tp.tipo, p.descripcion, p.precio,\n" +
                "                p.caracteristicas FROM public.mp_producto p, public.mp_tipo_producto tp\n" +
                "                 where p.tipo_producto_id=tp.id\n" +
                "                 and p.id_usuario=" + idUsuario + " and p.tipo_producto_id not in (5) order by p.fecha_actualizacion desc";
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                //  System.out.println("En el If respuesta " + res);

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
        String query = "select  p.id, g.url,  p.nombre,  p.precio \n" +
                "FROM public.mp_producto p left join mp_galeria g on g.producto_id =p.id and g.foto_principal=1\n" +
                "order by p.fecha_registro desc limit 10;";
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
        String query = "select a.id, a.nombre, to_char(a.fecha_registro, 'YYYY-MM-DD') as fecha_registro, a.calificacion_promedio, a.descripcion, a.precio, a.cantidad_actual, a.caracteristicas\n" +
                ", b.tipo\n" +
                ", c.url\n" +
                ", (case when pe.nombre isnull then '' else (pe.nombre)|| ' ' end)||(case when pe.nombre_sec isnull then '' else (pe.nombre_sec)|| ' ' end)||(case when pe.apellido isnull then '' else (pe.apellido)|| ' ' end)||(case when pe.apellido_sec isnull then '' else (pe.apellido_sec) end) as vendedor, pe.foto\n" +
                ",  ( d.nombre ||' , '|| d.pais ||' , '|| d.departamento||' , '|| d.ciudad) as direccion, a.estado, d.latitud, d.longitud, d.pais, d.departamento, d.ciudad, d.nombre as nombredireccion, d.id as id_direccion\n" +
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

    //Listar un producto
    public CompletableFuture<JsonObject> listarProducto(Long id) {

        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();
        String query = "SELECT tp.tipo, p.nombre,p.descripcion, p.precio, p.cantidad_origen as cantidad, p.estado,\n" +
                "dr.nombre as nombredireccion,dr.latitud,dr.longitud,dr.ciudad,dr.departamento,dr.pais,\n" +
                "     p.tipo_producto_id as id_tipo_producto,p.id as id_producto,dr.id as id_direccion,tp.tipo as tipo\n" +
                "                 FROM mp_producto p, mp_tipo_producto tp,mp_direccion dr\n" +
                "                 where tp.id=p.tipo_producto_id\n" +
                "                and p.id_direccion_id=dr.id and p.id=" + id;
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("En el If respuesta listar producto");
                                System.out.println(data.result().getRows().size());
                                System.out.println(data.result().getRows());
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

        System.out.println( nuevoProducto.getJsonObject("", new JsonObject()) );

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
        JsonUtils.add(params, nuevoProducto.getJsonObject("caracteristicas", new JsonObject()).toString());



        // System.out.println("LA IMAGEN LLEGA? " + nuevoProducto.getString("imagen", ""));

        String query = "INSERT INTO mp_producto(\n" +
                "            id, estado, nombre, fecha_registro, fecha_actualizacion, calificacion_promedio, \n" +
                "            id_padre, id_direccion_id, tipo_producto_id, descripcion, precio,id_usuario,cantidad_actual,cantidad_origen,caracteristicas)\n" +
                "    VALUES (nextval('mp_producto_id_seq'), \n" +
                "    ?, ?, \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "    to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "     ?, null, \n" +
                "     (SELECT max(id) FROM mp_direccion), \n" +
                "     ?, ?, ?,?,?,?,?::JSON);";


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
        if (!latitud.equals(""))
            JsonUtils.add(params2, Double.parseDouble(latitud));
        else
            JsonUtils.add(params2, 0);
        if (!longitud.equals(""))
            JsonUtils.add(params2, Double.parseDouble(longitud));
        else
            JsonUtils.add(params2, 0);
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

        int id = nuevoProducto.getInteger("id_direccion", 0);
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
                " WHERE id=" + id;

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

    //Editar un producto
    public CompletableFuture<JsonObject> editarProducto(JsonObject nuevoProducto, int productoAsociado) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto
        JsonArray params = new JsonArray();

        int idUsuario = nuevoProducto.getInteger("id_usuario", 0);

        JsonUtils.add(params, nuevoProducto.getString("estado", ""));
        JsonUtils.add(params, nuevoProducto.getString("nombre", ""));
        JsonUtils.add(params, new Date().toInstant());

        String tipo1 = nuevoProducto.getString("tipo", "");
        String tipo2 = nuevoProducto.getString("tipo", "");
        String tipo3 = nuevoProducto.getString("tipo", "");
        String tipo4 = nuevoProducto.getString("tipo", "");
        String tipo5 = nuevoProducto.getString("tipo", "");
        int tipo_producto_id = 0;
        if (tipo1.equals("Alimentación")) {
            tipo_producto_id = 2;
        }
        if (tipo2.equals("Alojamiento")) {
            tipo_producto_id = 1;
        }
        if (tipo3.equals("Paquete")) {
            tipo_producto_id = 5;
        }
        if (tipo4.equals("Transporte")) {
            tipo_producto_id = 3;
        }
        if (tipo5.equals("Paseos Ecológicos")) {
            tipo_producto_id = 4;
        }

        JsonUtils.add(params, nuevoProducto.getInteger("id_direccion", 0));
        JsonUtils.add(params, tipo_producto_id);
        JsonUtils.add(params, nuevoProducto.getString("descripcion", ""));
        JsonUtils.add(params, Double.parseDouble(nuevoProducto.getString("precio", "")));
        JsonUtils.add(params, Integer.parseInt(nuevoProducto.getString("cantidad", "")));

        JsonUtils.add(params, nuevoProducto.getJsonObject("caracteristicas", new JsonObject()).toString());

        int idProducto = nuevoProducto.getInteger("id", 0);

        String query = "UPDATE mp_producto\n" +
                "   SET estado=?, nombre=?, fecha_actualizacion=to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss'), \n" +
                "       id_direccion_id=?, tipo_producto_id=?, \n" +
                "       descripcion=?, precio=?, cantidad_origen=?, caracteristicas = ?::JSON  \n " +
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

/*    //Listar Imagenes
    public CompletableFuture<List<JsonObject>> listarImagenes(Long id) {

        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "select * from mp_galeria mp\n" +
                "where mp.producto_id=" + id;
        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().queryWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().getRows());
                                System.out.println("En el If respuesta listar imagenes");
                                System.out.println(data.result().getRows().size());
                                System.out.println(data.result().getRows());

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
    }*/

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

        String query = "DELETE FROM mp_direccion\n" +
                " WHERE id=" + id + ";";

        JsonArray params = new JsonArray();
        dataAccess.getConnection(conn -> {
                    if (conn.succeeded()) {
                        conn.result().updateWithParams(query, params, data -> {
                            if (data.succeeded()) {
                                res.complete(data.result().toJson());
                                System.out.println("Borrar direccion DAO");
                            } else {
                                data.cause().printStackTrace();
                                System.out.println("Error Borrar direccion DAO print");
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

    //Borrar las preguntas asociadas al producto
    public CompletableFuture<JsonObject> borrarPreguntas(Long idProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<JsonObject>();

        String query = "delete from mp_preguntas s \n" +
                "  where s.id_producto=" + idProducto;

        JsonArray params = new JsonArray();

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
                        "UPPER(pe.apellido_sec) like UPPER('%" + search + "%') AND UPPER(p.estado)='ACTIVO'\n";
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

    //borrar producto
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
