package co.ecofactory.ecotravel.paquete;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.paquete.service.PaqueteService;
import co.ecofactory.ecotravel.utils.service.UtilidadDatosService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloPaquete implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Datos");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        PaqueteService datosService = new PaqueteService();
        vertx.deployVerticle(datosService, options);
    }

    public String getNombre() {
        return "/paquete";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
       //Listar paquetes
        rutas.get("/").handler(rc -> {
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            JsonObject usuario = new JsonObject();
            usuario.put("id_usuario", idUsuario);
            vertx.eventBus().send("listarPaquetes", usuario, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo datos");
                }
            });
        });

        //Agregar nuevo paquete
        rutas.post("/").handler(rc -> {
            JsonObject producto = new JsonObject();
            producto = rc.getBodyAsJson();
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            producto.put("id_usuario", idUsuario);
            System.out.println("USUARIO AUTENTICADO para crear paquete ----->" + producto.encodePrettily());
            vertx.eventBus().send("insertarPaquete", producto, res -> {
                System.out.println("servidor insertarPaquete: " + res.result().body());
                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarPaquete -> : " + res.result().body());
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un producto");
                }
            });
        });

        //Borrar un paquete
        rutas.delete("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject _params = new JsonObject();
            _params.put("idPaquete", idAsLong);
          //  Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
         //   _params.put("id_usuario", idUsuario);
            vertx.eventBus().send("borrarPaquete", _params, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo paquete borrar paquete");
                }
            });

        });

        //Listar paquete
        rutas.get("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject _params = new JsonObject();
            _params.put("id", idAsLong);
            vertx.eventBus().send("listarPaquetesDetalle", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo listarPaquetesDetalle consultando un listarPaquetesDetalle");
                }
            });
        });

        //Listar hijos
        rutas.get("/hijos/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject _params = new JsonObject();
            _params.put("id", idAsLong);
            vertx.eventBus().send("listarHijos", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo hijos paquete");
                }
            });
        });

        //Editar un paquete
        rutas.put("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject producto = new JsonObject();
            producto = rc.getBodyAsJson();
            producto.put("id", idAsLong);
            vertx.eventBus().send("editarPaquete", producto, res -> {
                // System.out.println("servidor editarProducto: " + res.result().body());
                if (res.succeeded()) {
                    System.out.println("servidor correcto editarProducto -> : " + res.result().body());
                    if (((JsonObject) res.result().body()).getInteger("updated", 0) != 0) {
                        rc.response().end("Se editarProducto correctamente " + ((JsonObject) res.result().body()).encodePrettily());
                    } else {
                        rc.response().end("El ID " + idAsLong + " no fue encontrado");
                    }
                } else {

                    rc.response().end("ERROR en el modulo producto editar un producto");

                }
            });
        });

        return rutas;
    }

}

