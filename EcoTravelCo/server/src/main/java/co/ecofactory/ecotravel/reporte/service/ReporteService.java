package co.ecofactory.ecotravel.reporte.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.reporte.service.Interfaz.ReporteInterfaz;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReporteService extends AbstractVerticle {
    private JDBCClient dataAccess;

    @Override
    public void start() throws Exception {
        dataAccess = JDBCClient.createShared(vertx, new Conexion().getConf());
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("datosReporte", this::datosReporte);
    }

    public void datosReporte(Message<JsonObject> message) {
        try {
            //Se obtiene el nombre de la clase a instanciar
            String tipoReporte = System.getProperty(message.body().getString("tipo"));
            ReporteInterfaz reporte = (ReporteInterfaz) Class.forName(tipoReporte).newInstance();
            reporte.setDataAcces(dataAccess);

            CompletableFuture<List<JsonObject>> data = reporte.getInfoReporte(message.body());
            data.whenComplete((ok, error) -> {
                if (ok != null) {
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> arr.add(o));
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data Reporte");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "REPORTE NO DISPONIBLE");
        }
    }
}
