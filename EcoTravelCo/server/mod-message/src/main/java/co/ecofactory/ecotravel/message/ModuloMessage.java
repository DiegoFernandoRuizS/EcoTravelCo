package co.ecofactory.ecotravel.message;

import co.ecofactory.ecotravel.message.service.MessageService;
import co.ecofactory.ecotravel.module.contract.Modulo;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloMessage implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Mensajes");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        MessageService messageService = new MessageService();
        vertx.deployVerticle(messageService, options);
    }

    public String getNombre() {
        return "/mensaje";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);

        rutas.get("/:id").handler(rc -> {
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            JsonObject datos = new JsonObject();
            datos.put("origen", idUsuario.toString());
            datos.put("destino", rc.request().getParam("id"));

            vertx.eventBus().send("listarMensajes", datos, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo producto");
                }
            });
        });


        //Agregar nuevo cliente
        rutas.post("/").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            vertx.eventBus().send("agregarMensaje", _params, res -> {

                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un usuario");
                }

            });

        });

        return rutas;
    }

}

