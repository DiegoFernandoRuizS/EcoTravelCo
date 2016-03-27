package co.ecofactory.ecotravel.preguntas;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.preguntas.service.PreguntasService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloPreguntas implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: preguntas");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        PreguntasService preguntasService = new PreguntasService();
        vertx.deployVerticle(preguntasService, options);
    }

    public String getNombre() {
        return "/preguntas";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/:id").handler(rc -> {
            System.out.println("Listar preguntas - GET");
            JsonObject _params = new JsonObject();
            final String id = rc.request().getParam("id");
            _params.put("id", id);
            vertx.eventBus().send("listarPreguntas", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo preguntas");
                }
            });
        });


        //Agregar
        rutas.post("/").handler(rc -> {
            System.out.println("Agregar Canasta - POST");
            JsonObject mensaje = new JsonObject();
            mensaje = rc.getBodyAsJson();
            JsonObject _params = new JsonObject();
            _params.put("id_usuario", mensaje.getValue("usuario"));
            _params.put("id_producto", mensaje.getValue("producto"));
            _params.put("pregunta", mensaje.getValue("pregunta"));

            vertx.eventBus().send("agregarPregunta", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de agregar pregunta");
                }
            });
        });


        return rutas;
    }
}
