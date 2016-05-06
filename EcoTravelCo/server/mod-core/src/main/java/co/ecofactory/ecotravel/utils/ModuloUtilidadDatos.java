package co.ecofactory.ecotravel.utils;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.utils.service.UtilidadDatosService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloUtilidadDatos implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Datos");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        UtilidadDatosService datosService = new UtilidadDatosService();
        vertx.deployVerticle(datosService, options);
    }

    public String getNombre() {
        return "/datos";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
//Listar paises
        rutas.get("/").handler(rc -> {

            vertx.eventBus().send("listarPaises", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo datos");
                }
            });
        });

        //Listar tipoproducto
        rutas.get("/tipo").handler(rc -> {

            vertx.eventBus().send("listaTipoProducto", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo datos");
                }
            });
        });
        return rutas;
    }

}

