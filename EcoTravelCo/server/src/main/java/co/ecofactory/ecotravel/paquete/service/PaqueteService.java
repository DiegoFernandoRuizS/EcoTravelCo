package co.ecofactory.ecotravel.paquete.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.paquete.service.dao.PaqueteDAO;
import co.ecofactory.ecotravel.utils.service.dao.UtilidadDatosDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaqueteService extends AbstractVerticle {
    private PaqueteDAO dao;

    @Override
    public void start() throws Exception {
        dao = new PaqueteDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus

        this.getVertx().eventBus().consumer("listarPaquetes", this::listarPaquetes);

    }
    public void listarPaquetes(Message<JsonObject> message) {
        System.out.println("listar Paquetes");
        try {
            CompletableFuture<List<JsonObject>> data = this.dao.ListarPaquetes(message.body());
            data.whenComplete((ok, error) -> {
                System.out.println("Listar Paquetes");
                if (ok != null) {
                      System.out.println("ListarPaquetes:OK" + ok);
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


