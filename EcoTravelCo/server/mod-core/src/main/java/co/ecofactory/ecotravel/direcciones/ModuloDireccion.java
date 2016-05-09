package co.ecofactory.ecotravel.direcciones;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.direcciones.service.DireccionService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloDireccion implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Direccion");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        DireccionService direccionService = new DireccionService();
        vertx.deployVerticle(direccionService, options);
    }

    public String getNombre() {
        return "/direccion";
    }


    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/:tipo").handler(rc -> {
            System.out.println("entro: Direccion");
            JsonObject _params = new JsonObject();
            vertx.eventBus().send("dirreccionTipo", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });

        rutas.get("/coordenada/:dir").handler(rc -> {
            System.out.println("entro: cordanada");
            final String dir = rc.request().getParam("dir");
            JsonObject _params = new JsonObject();
            _params.put("dir", dir);
            vertx.eventBus().send("dirreccionCoord", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });

        rutas.get("/ip/:dir").handler(rc -> {
            System.out.println("entro: cordanada");
            final String dir = rc.request().getParam("dir");
            JsonObject _params = new JsonObject();
            _params.put("dir", dir);
            vertx.eventBus().send("dirreccionCoordIp", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });


        return rutas;
    }

    }
