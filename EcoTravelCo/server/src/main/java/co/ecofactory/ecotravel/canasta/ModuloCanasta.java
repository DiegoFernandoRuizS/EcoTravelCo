package co.ecofactory.ecotravel.canasta;

import co.ecofactory.ecotravel.canasta.service.CanastaService;
import co.ecofactory.ecotravel.module.contract.Modulo;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloCanasta implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Canasta");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        CanastaService productoService = new CanastaService();
        vertx.deployVerticle(productoService, options);
    }

    public String getNombre() {
        return "/canasta";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);


        rutas.get("/").handler(rc -> {
            vertx.eventBus().send("listarCanasta", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto" );
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo canasta");
                }
            });
        });

        rutas.get("/:id").handler(rc -> {
            rc.response().write("OK");
        });

        rutas.put("/:id").handler(rc -> {
            // TODO Add a new product...
            rc.response().end();

        });

        rutas.delete("/:id").handler(rc -> {
            // TODO delete the product...
            rc.response().end();

        });

        return rutas;
    }

}
