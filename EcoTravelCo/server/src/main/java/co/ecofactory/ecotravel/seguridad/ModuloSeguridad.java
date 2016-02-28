package co.ecofactory.ecotravel.seguridad;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloSeguridad implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Seguridad");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        SeguridadService seguridadService = new SeguridadService();
        vertx.deployVerticle(seguridadService, options);
    }

    public String getNombre() {
        return "/seguridad";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);


        //Agregar nuevo producto
        rutas.post("/autenticar").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();

            vertx.eventBus().send("autenticarUsuario", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto autenticarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    System.out.println(res.cause());
                    rc.response().setStatusCode(401).end("ERROR al autenticar el usuario");
                }

            });

        });

        return rutas;
    }

}

