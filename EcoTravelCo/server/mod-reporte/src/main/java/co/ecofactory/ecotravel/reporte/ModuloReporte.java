package co.ecofactory.ecotravel.reporte;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.reporte.service.ReporteService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloReporte implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Reportes");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ReporteService reporteService = new ReporteService();
        vertx.deployVerticle(reporteService, options);
    }

    public String getNombre() {
        return "/reporte";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);

        rutas.post("/").handler(rc -> {
            JsonObject _params = new JsonObject();

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            JsonObject mensaje = new JsonObject();
            mensaje = rc.getBodyAsJson();

            _params.put("idUsuario", idUsuario);
            _params.put("fechaInicial", mensaje.getValue("fecha_inicial"));
            _params.put("fechaFinal", mensaje.getValue("fecha_final"));
            _params.put("reporte", mensaje.getValue("tipo"));

            vertx.eventBus().send("generarReporte", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo calificacion");
                }
            });
        });
        return rutas;
    }
}
