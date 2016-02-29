package co.ecofactory.ecotravel.canasta.service;

import co.ecofactory.ecotravel.canasta.service.dao.CanastaDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by samuel on 2/15/16.
 */
public class CanastaService extends AbstractVerticle {
    private CanastaDAO dao;

    @Override
    public void start() throws Exception {
        dao = new CanastaDAO(this.getVertx(), new JsonObject()
                    .put("url", "jdbc:postgresql://localhost/ecofactory")
                .put("driver_class", "org.postgresql.Driver")
                .put("user","postgres").put("password","1234")
                .put("max_pool_size", 30));

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarCanasta", this::listarCanasta);

    }

    public void listarCanasta(Message<JsonObject> message) {

        System.out.println("listarCanasta");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarCanasta();
            System.out.println(11);
            data.whenComplete((ok, error) -> {
                System.out.println("listarCanasta");
                if (ok != null) {
                    System.out.println("listarCanasta:OK" + ok);
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
