package co.ecofactory.ecotravel.cliente;

import co.ecofactory.ecotravel.cliente.service.ClienteService;
import co.ecofactory.ecotravel.module.contract.Modulo;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloCliente implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Cliente");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ClienteService clienteService = new ClienteService();
        vertx.deployVerticle(clienteService, options);
    }

    public String getNombre() {
        return "/cliente";
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

            JsonObject _params = rc.getBodyAsJson();

            vertx.eventBus().send("insertarCliente", _params, res -> {


                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarCliente -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().setStatusCode(422).end("ERROR en el modulo producto insertar un cliente");
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

