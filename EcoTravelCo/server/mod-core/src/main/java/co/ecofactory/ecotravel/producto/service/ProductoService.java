package co.ecofactory.ecotravel.producto.service;

import co.ecofactory.ecotravel.galeria.service.dao.GaleriaDAO;
import co.ecofactory.ecotravel.producto.service.dao.ProductoDAO;
import co.ecofactory.ecotravel.init.Conexion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;

public class ProductoService extends AbstractVerticle {
    private ProductoDAO dao;

    @Override
    public void start() throws Exception {
        dao = new ProductoDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        //CRUD productos
        this.getVertx().eventBus().consumer("listarProductos", this::listarProductos);
        this.getVertx().eventBus().consumer("listarProducto", this::listarProducto);
        this.getVertx().eventBus().consumer("insertarProducto", this::insertarProducto);
        this.getVertx().eventBus().consumer("editarProducto", this::editarProducto);
        this.getVertx().eventBus().consumer("borrarProducto", this::borrarProducto);
        this.getVertx().eventBus().consumer("listarProductosHome", this::listarProductosHome);
        this.getVertx().eventBus().consumer("listarProductosDetalle", this::listarProductosDetalle);
        this.getVertx().eventBus().consumer("listarProductosBusqueda", this::listarProductosBusqueda);
    }

    public void listarProductos(Message<JsonObject> message) {
        System.out.println("listarProductosProveedor");
        try {
            CompletableFuture<List<JsonObject>> data = this.dao.listarProductos(message.body());
            data.whenComplete((ok, error) -> {
                System.out.println("listarProductos");
                if (ok != null) {
                    System.out.println("listarProductosProveedor:OK" + ok);
                    JsonArray arr = new JsonArray();

                    ok.forEach(o ->

                            {
                                if (o.getString("caracteristicas") != null) {
                                    o.put("caracteristicas", new JsonObject(o.getString("caracteristicas")));
                                }
                                arr.add(o);
                            }

                    );

                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }

    public void listarProductosHome(Message<JsonObject> message) {

        System.out.println("listarProductos");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarProductosHome();
            data.whenComplete((ok, error) -> {
                System.out.println("listarProductos");
                if (ok != null) {
                    //System.out.println("listarProductos:OK" + ok);
                    JsonArray arr = new JsonArray();

                    ok.forEach(o -> arr.add(o));

                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }

    public void listarProductosDetalle(Message<JsonObject> message) {

        System.out.println("listarProductosDetalle");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarProductosDetalle(message.body().getString("id"));
            System.out.println(message.body().getString("id"));
            data.whenComplete((ok, error) -> {
                System.out.println("listarProductosDetalle");
                if (ok != null) {
                    // System.out.println("listarProductos:OK" + ok);
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> {
                        if (o.getString("caracteristicas") != null) {
                            o.put("caracteristicas", new JsonObject(o.getString("caracteristicas")));
                        }
                        arr.add(o);
                    });
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }

    //Listar producto con un id como paramento
    public void listarProducto(Message<JsonObject> message) {
        System.out.println("listarProducto ID: " + message.body().getLong("id"));
        try {
            CompletableFuture<JsonObject> data = this.dao.listarProducto(message.body().getLong("id"));
            data.whenComplete((ok, error) -> {
                System.out.println("listarProducto");
                if (ok != null) {
                    message.reply(ok);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

    //Insertar producto
    public void insertarProducto(Message<JsonObject> message) {
        System.out.println("Service insertarProducto" + message.body());
        final int[] llave = {0};
        final int[] idProducto = {0};
        try {
            CompletableFuture<JsonObject> data = null;
            vertx.eventBus().send("insertarDireccion", message.body(), res -> {
                if (res.succeeded()) {
                    int dir =  (int)((JsonArray)((JsonObject) res.result().body()).getMap().get("keys")).getInteger(0);
                    if (dir != 0) {
                        CompletableFuture<JsonObject> data2 = this.dao.insertarProducto(message.body());
                        data2.whenComplete((ok2, error2) -> {
                            if (ok2 != null) {
                                message.reply(ok2);
                            } else {
                                error2.printStackTrace();
                                message.fail(0, "ERROR in data producto");
                            }
                        });

                    } else {
                        message.fail(0, "ERROR in data direccion");
                    }


                }else{
                    message.fail(0, "ERROR in res direccion");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

    //Editar producto
    public void editarProducto(Message<JsonObject> message) {
        System.out.println("Service editar producto" + message.body());
        final int[] llave = {0};
        final int[] idProducto = {0};
        try {

            CompletableFuture<JsonObject> data = null;
            vertx.eventBus().send("actualizarDireccion", message.body(), res -> {
                if (res.succeeded()) {
                    CompletableFuture<JsonObject> data2 = this.dao.editarProducto(message.body(), idProducto[0]);
                    data2.whenComplete((ok2, error2) -> {
                        if (ok2 != null) {
                            idProducto[0] = (int) ok2.getJsonArray("keys").getValue(0);
                            message.reply(ok2);
                        } else {
                            error2.printStackTrace();
                            message.fail(0, "ERROR al actualizar el producto ");
                        }
                    });

                } else {
                    message.fail(0, "ERROR  al actualizar la direccion");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch actualizar");
        }
    }

    //Borrar producto con un id como paramento
    public void borrarProducto(Message<JsonObject> message) {
        System.out.println("borrarProducto ID: " + message.body().getLong("id"));
        try {
            //borrar la imagen primero
            CompletableFuture<JsonObject> data2 = this.dao.borrarImagen(message.body().getLong("id"));
            data2.whenComplete((ok2, error2) -> {
                System.out.println("borrarImagen");
                if (ok2 != null) {
                    System.out.println("borrarImagen:OK" + ok2);
                    message.reply(ok2);
                    //borrar preguntas asociadas al producto
                    CompletableFuture<JsonObject> data = this.dao.borrarPreguntas(message.body().getLong("id"));
                    data.whenComplete((ok, error) -> {
                        System.out.println("borrarPreguntas");
                        if (ok != null) {
                            System.out.println("borrarPreguntas:OK" + ok);
                            message.reply(ok);
                            CompletableFuture<JsonObject> data4 = this.dao.borrarProducto(message.body().getLong("id"));
                            data.whenComplete((ok4, error4) -> {
                                System.out.println("borrarProducto");
                                if (ok4 != null) {
                                    System.out.println("borrarProducto:OK" + ok4);
                                    message.reply(ok4);
                                    vertx.eventBus().send("borrarDireccion", message.body(), res -> {
                                        if (res.succeeded()) {
                                        } else {
                                            message.fail(0, "ERROR  al borrar la direccion");
                                        }
                                    });

                                } else {
                                    error4.printStackTrace();
                                    message.fail(0, "ERROR in data producto");
                                }
                            });

                        } else {
                            error.printStackTrace();
                            message.fail(0, "ERROR in data preguntas");
                        }
                    });

                } else {
                    error2.printStackTrace();
                    message.fail(0, "ERROR in data imagen");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

    public void listarProductosBusqueda(Message<JsonObject> message) {

        System.out.println("listarProductosBusqueda");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarProductosBusqueda(message.body());
            System.out.println(11);
            data.whenComplete((ok, error) -> {
                System.out.println("listarProductosbb");
                if (ok != null) {
                    System.out.println("listarProductosbusq:OK" + ok);
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> arr.add(o));
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }
}


