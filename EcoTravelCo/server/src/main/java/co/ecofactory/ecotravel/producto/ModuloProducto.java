package co.ecofactory.ecotravel.producto;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.producto.service.ProductoService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.bson.json.JsonWriter;

import java.util.List;

/**
 * Created by samuel on 2/15/16.
 */
public class ModuloProducto implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Producto");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ProductoService productoService = new ProductoService();
        vertx.deployVerticle(productoService, options);
    }

    public String getNombre() {
        return "/producto";
    }

    public Router getRutas(Vertx vertx) {

        Router rutas = Router.router(vertx);

        rutas.get("/").handler(rc -> {

            vertx.eventBus().send("listarProductos", new JsonObject(), res -> {

                System.out.println("servidor" + res);
                if (res.succeeded()) {

                    System.out.println("servidor" );

                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());


                } else {
                    rc.response().end("ERROR");
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
