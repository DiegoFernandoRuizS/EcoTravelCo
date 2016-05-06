package co.ecofactory.ecotravel.calificacion;

import co.ecofactory.ecotravel.calificacion.service.CalificacionService;
import co.ecofactory.ecotravel.module.contract.Modulo;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloCalificacion implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Calificación y comentarios");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        CalificacionService calificacionService = new CalificacionService();
        vertx.deployVerticle(calificacionService, options);
    }

    public String getNombre() {
        return "/calificacion";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);

        rutas.get("/:id").handler(rc -> {
            JsonObject _params = new JsonObject();
            final String id = rc.request().getParam("id");
            _params.put("id", id);
            vertx.eventBus().send("listarCalificacion", _params, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto" );
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo calificacion");
                }
            });
        });

        //Calificar Servicio
        rutas.post("/").handler(rc -> {
            System.out.println("Calificar Servicio - POST");
            JsonObject _params = new JsonObject();

            _params = rc.getBodyAsJson();

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            _params.put("id_usuario", idUsuario);

            vertx.eventBus().send("calificarServicio", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo calificacion");
                }
            });
        });


        return rutas;
    }
}
