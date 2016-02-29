package co.ecofactory.ecotravel.producto;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.producto.service.ProductoService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.bson.json.JsonWriter;

import java.util.List;

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
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo producto");
                }
            });
        });

        //Listar producto
        rutas.get("/:id").handler(rc -> {

            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);

            JsonObject _params = new JsonObject();

            _params.put("id", idAsLong);

            vertx.eventBus().send("listarProducto", _params, res -> {

                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto consultando un producto");
                }

            });
        });


        //Agregar nuevo producto
        rutas.post("/").handler(rc -> {

        //    String json = Json.decodeValue(rc.getBodyAsString(),String.class);

            //Trayendo los datos desde el JSON
            JsonObject producto = new JsonObject();
           // producto=rc.getBodyAsJson();

          //  System.out.println("PRODUCTO JSON COMO LLEGA:  "+json);
            //final String id = rc.request().getParam("id");
            //final Long idAsLong = Long.valueOf(id);
            //System.out.println("Llego el ID "+ idAsLong);
            JsonObject _params = new JsonObject();

            //_params.put("id", 3L);


            System.out.println("----->"+rc.getBodyAsString());
            System.out.println("----->"+rc.getBodyAsJson().getString("estado"));




            vertx.eventBus().send("insertarProducto", _params, res -> {

                System.out.println("servidor insertarProducto: " + res.result().body());
                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarProducto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un producto");
                }

            });

        });

        //Editar un producto
        rutas.put("/:id").handler(rc -> {
            // TODO Add a new product...
            rc.response().end();

        });

        //Borrar un producto
        rutas.delete("/:id").handler(rc -> {
            // TODO delete the product...
            rc.response().end();

        });

        return rutas;
    }

}

