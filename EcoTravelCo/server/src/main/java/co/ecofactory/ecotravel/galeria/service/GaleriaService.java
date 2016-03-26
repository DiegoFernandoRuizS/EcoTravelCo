package co.ecofactory.ecotravel.galeria.service;

import co.ecofactory.ecotravel.galeria.service.dao.GaleriaDAO;
import co.ecofactory.ecotravel.init.Conexion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GaleriaService extends AbstractVerticle {
    private GaleriaDAO dao;

    @Override
    public void start() throws Exception {
        dao = new GaleriaDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarGaleria", this::listarGaleria);
    }

    public void listarGaleria(Message<JsonObject> message) {
        try {
            int idProducto = Integer.parseInt(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarGaleria(idProducto);
            data.whenComplete((ok, error) -> {
                if (ok != null) {
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> arr.add(o));
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

}
