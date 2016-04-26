package co.ecofactory.ecotravel.usuario;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.usuario.service.UsuarioService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloUsuario implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Usuario");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        UsuarioService usuarioService = new UsuarioService();
        vertx.deployVerticle(usuarioService, options);
    }

    public String getNombre() {
        return "/usuario";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);

        rutas.get("/").handler(rc -> {
            vertx.eventBus().send("listarProductos", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo producto");
                }
            });
        });


        //Listar proveedores
        rutas.get("/proveedores").handler(rc -> {
            vertx.eventBus().send("listarProveedores", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo usuarios");
                }
            });
        });

        //Listar producto
        rutas.get("/consultar").handler(rc -> {

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));

            JsonObject _params = new JsonObject();

            _params.put("id", idUsuario);

            vertx.eventBus().send("consultarUsuarioPorId", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto consultarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {

                    if (res.cause() != null) {
                        res.cause().printStackTrace();
                    }

                    rc.response().end("ERROR en el modulo usuario > consultar usuario");
                }

            });
        });


        //Agregar nuevo cliente
        rutas.post("/").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();

            vertx.eventBus().send("insertarUsuario", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un usuario");
                }

            });

        });


        //Agregar nuevo proveedor
        rutas.post("/proveedor").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();

            vertx.eventBus().send("insertarProveedor", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarProveedor -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un proveedor");
                }

            });

        });

        rutas.put("/cliente").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();

            vertx.eventBus().send("actualizarCliente", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto actualizar usuario/cliente -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto actualizar un usuario/cliente");
                }

            });

        });

        rutas.put("/cliente/foto").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            _params.put("id",idUsuario);


            vertx.eventBus().send("actualizarFoto", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto actualizar usuario/cliente foto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto actualizar un usuario/cliente");
                }

            });

        });

        //Editar un producto
        rutas.put("/:id").handler(rc -> {
            // TODO Add a new product...
            rc.response().end();

        });

        //Borrar un producto
        rutas.delete("/:id").handler(rc -> {
            // TODO delete the product...
            rc.response().end();

        });

        return rutas;
    }

}

