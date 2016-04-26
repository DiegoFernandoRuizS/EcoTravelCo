package co.ecofactory.ecotravel.reporte;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.reporte.service.ReporteService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloReporte implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Orden");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ReporteService ordenService = new ReporteService();
        vertx.deployVerticle(ordenService, options);
    }

    public String getNombre() {
        return "/orden";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/").handler(rc -> {
            System.out.println("Listar Ordenes - GET");
            JsonObject _params = new JsonObject();

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));

            _params.put("id", idUsuario);

            vertx.eventBus().send("listarOrden", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo orden");
                }
            });
        });

        //Calificar Servicio
        rutas.post("/calificar/").handler(rc -> {
            System.out.println("Calificar Servicio - POST");
            JsonObject _params = new JsonObject();

            _params = rc.getBodyAsJson();

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            _params.put("id_usuario", idUsuario);

            vertx.eventBus().send("calificarServicio", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo orden");
                }
            });
        });

        //Cancelar
        rutas.delete("/").handler(rc -> {
            System.out.println("Cancelar Orden - DELETE");
            JsonObject _params = new JsonObject();
            _params.put("id_orden", rc.request().getParam("id"));

            vertx.eventBus().send("cancelarOrden", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR!! en el modulo orden cancelandola");
                }
            });
        });

        //Pagar
        rutas.put("/").handler(rc -> {
            System.out.println("Pagar Orden - PUT");
            JsonObject _params = new JsonObject();
            _params.put("id_orden", rc.request().getParam("id"));

            vertx.eventBus().send("pagarOrden", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR!! en el modulo orden pagandola");
                }
            });
        });


        rutas.get("/detalle/").handler(rc -> {
            System.out.println("Detalle Orden - GET");
            JsonObject _params = new JsonObject();
            final String id = rc.request().getParam("id");
            _params.put("id", id);
            vertx.eventBus().send("detalleOrden", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo orden");
                }
            });
        });

        return rutas;
    }
}
