package co.ecofactory.ecotravel.galeria;

import co.ecofactory.ecotravel.galeria.service.GaleriaService;
import co.ecofactory.ecotravel.module.contract.Modulo;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloGaleria implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: galeria");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        GaleriaService galeriaService = new GaleriaService();
        vertx.deployVerticle(galeriaService, options);
    }

    public String getNombre() {
        return "/galeria";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);
        rutas.get("/:id").handler(rc -> {
            System.out.println("Listar galeria - GET");
            JsonObject _params = new JsonObject();
            final String id = rc.request().getParam("id");
            _params.put("id", id);
            vertx.eventBus().send("listarGaleria", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonArray)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo galeria");
                }
            });
        });

        rutas.post("/").handler(rc -> {
            System.out.println("Insertar Imagen - POST");
            JsonObject _params = new JsonObject();

            JsonObject mensaje = new JsonObject();
            mensaje = rc.getBodyAsJson();

            _params.put("imagen", mensaje.getValue("imagen"));
            _params.put("idProducto", mensaje.getValue("id_producto"));
            _params.put("principal", mensaje.getValue("principal"));

            vertx.eventBus().send("insertarImagen", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo galeria");
                }
            });
        });


        rutas.delete("/:id").handler(rc -> {
            System.out.println("Eliminar Imagen - DELETE");
            JsonObject _params = new JsonObject();

            final int idImagen = Integer.parseInt(rc.request().getParam("id"));

            _params.put("idImagen", idImagen);

            vertx.eventBus().send("eliminarImagen", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo galeria");
                }
            });
        });

        return rutas;
    }
}
