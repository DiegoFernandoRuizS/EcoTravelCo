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
        CanastaService canastaService = new CanastaService();
        vertx.deployVerticle(canastaService, options);
    }

    public String getNombre() {
        return "/canasta";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/").handler(rc -> {
            System.out.println("Listar Canasta - GET");
            JsonObject _params = new JsonObject();
            final String id = rc.request().getParam("id");
            _params.put("id", id);
            vertx.eventBus().send("listarCanasta", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo canasta");
                }
            });
        });

        //Agregar
        rutas.post("/").handler(rc -> {
            System.out.println("Agregar Canasta - POST");
            JsonObject _params = new JsonObject();
            _params.put("id_usuario", rc.request().getParam("id_usuario"));
            _params.put("id_producto", rc.request().getParam("id_producto"));
            _params.put("cantidad", rc.request().getParam("cantidad"));

            vertx.eventBus().send("agregarProductoCanasta", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo canasta adicionando Item");
                }
            });
        });

        //Modificar
        rutas.put("/").handler(rc -> {
            System.out.println("Modificar Canasta - PUT");
            JsonObject _params = new JsonObject();

            _params.put("id_orden_item", rc.request().getParam("id"));
            _params.put("cantidad", rc.request().getParam("cantidad"));

            vertx.eventBus().send("modificarCanasta", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo canasta modificando Item");
                }
            });
        });

        //Eliminar
        rutas.delete("/").handler(rc -> {
            System.out.println("Eliminar Canasta - DELETE");
            JsonObject _params = new JsonObject();
            _params.put("id_orden_item", rc.request().getParam("id_orden_item"));

            //Eliminar Registro
            vertx.eventBus().send("eliminarProductoCanasta", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR!! en el modulo canasta eliminando Item");
                }
            });
        });
        return rutas;
    }
}
