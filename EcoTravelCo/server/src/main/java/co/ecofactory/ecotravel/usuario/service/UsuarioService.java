package co.ecofactory.ecotravel.usuario.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.usuario.service.dao.UsuarioDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class UsuarioService extends AbstractVerticle {
    private UsuarioDAO dao;

    @Override
    public void start() throws Exception {
        dao = new UsuarioDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus

        this.getVertx().eventBus().consumer("listarUsuarios", this::listarUsuarios);
        this.getVertx().eventBus().consumer("insertarCliente", this::insertarCliente);
        this.getVertx().eventBus().consumer("listarProveedores", this::listarProveedores);
        this.getVertx().eventBus().consumer("insertarProveedor", this::insertarProveedor);
        this.getVertx().eventBus().consumer("consultarUsuarioPorLogin", this::consultarUsuarioPorLogin);
        this.getVertx().eventBus().consumer("consultarUsuarioPorId", this::consultarUsuarioPorId);
        this.getVertx().eventBus().consumer("actualizarCliente", this::actualizarCliente);
        this.getVertx().eventBus().consumer("actualizarFoto", this::actualizarFoto);

    }

    public void listarUsuarios(Message<JsonObject> message) {
        System.out.println("Service listarUsuarios" + message.body());
        try {

            CompletableFuture<List> data = this.dao.listarUsuarios();
            data.whenComplete((ok, error) -> {
                System.out.println("listarUsuarios");
                if (ok != null) {
                    System.out.println("listarUsuarios:OK" + ok);
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


    public void listarProveedores(Message<JsonObject> message) {
        System.out.println("Service listarProveedores" + message.body());
        try {

            CompletableFuture<List> data = this.dao.listarProveedores();
            data.whenComplete((ok, error) -> {
                System.out.println("listarProveedores");
                if (ok != null) {
                    System.out.println("listarProveedores:OK" + ok);
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

    //Insertar usuario
    public void insertarProveedor(Message<JsonObject> message) {
        System.out.println("Service insertarProveedor" + message.body());
        try {

            JsonObject datos = message.body();
            datos.put("tipo", "PROVEEDOR");

            CompletableFuture<JsonObject> data = this.dao.insertarUsuario(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("insertarUsuario");
                if (ok != null) {
                    System.out.println("insertarProducto:OK" + ok);
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

    //Insertar usuario
    public void insertarCliente(Message<JsonObject> message) {
        System.out.println("Service insertarUsuario" + message.body());
        try {

            JsonObject datos = message.body();
            datos.put("tipo", "CLIENTE");

            CompletableFuture<JsonObject> data = this.dao.insertarUsuario(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("insertarUsuario");
                if (ok != null) {
                    System.out.println("insertarProducto:OK" + ok);
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

    //Insertar usuario
    public void actualizarCliente(Message<JsonObject> message) {
        System.out.println("Service insertarUsuario" + message.body());
        try {

            JsonObject datos = message.body();
            datos.put("tipo", "CLIENTE");

            CompletableFuture<JsonObject> data = this.dao.actualizarUsuario(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("actualizarUsuario");
                if (ok != null) {
                    System.out.println("actualizarUsuario:OK" + ok);
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

    public void actualizarFoto(Message<JsonObject> message) {
        System.out.println("Service insertarUsuario" + message.body());
        try {

            JsonObject datos = message.body();


            CompletableFuture<JsonObject> data = this.dao.actualizarFoto(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("actualizarUsuario");
                if (ok != null) {
                    System.out.println("actualizarUsuario:OK" + ok);
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

    //Insertar usuario
    public void consultarUsuarioPorLogin(Message<String> message) {
        System.out.println("Service consultarUsuarioPorLogin: -> " + message.body());
        try {

            String datos = message.body();

            CompletableFuture<JsonObject> data = this.dao.consultarUsuarioPorLogin(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("consultarUsuarioPorLogin");
                if (error == null) {
                    //System.out.println("consultarUsuarioPorLogin:OK" + ok);
                    message.reply(ok);
                } else {
                    try {
                        error.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }

    public void consultarUsuarioPorId(Message<JsonObject> message) {
        System.out.println("Service consultarUsuarioPorID: -> " + message.body());
        try {

            JsonObject datos = (JsonObject) message.body();

            Integer id = datos.getInteger("id");


            CompletableFuture<JsonObject> data = this.dao.consultarUsuarioPorId(id);
            data.whenComplete((ok, error) -> {
                System.out.println("consultarUsuarioPorLogin");
                if (error == null) {
                    System.out.println("consultarUsuarioPorLogin:OK" + ok);
                    message.reply(ok);
                } else {
                    try {
                        error.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }
}
