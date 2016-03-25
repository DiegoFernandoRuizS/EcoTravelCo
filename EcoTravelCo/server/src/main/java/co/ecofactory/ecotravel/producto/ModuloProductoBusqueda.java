package co.ecofactory.ecotravel.producto;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.producto.service.ProductoService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloProductoBusqueda implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Productobusqueda");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ProductoService productoService = new ProductoService();
        vertx.deployVerticle(productoService, options);
    }

    public String getNombre() {
        return "/producto_busqueda";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/:criterios").handler(rc -> {
            JsonObject _params = new JsonObject();
            final String criterios = rc.request().path().replace("/producto_busqueda/","");
            _params.put("criterios", criterios);
            vertx.eventBus().send("listarProductosBusqueda", _params, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto busqueda" );
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo producto");
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
