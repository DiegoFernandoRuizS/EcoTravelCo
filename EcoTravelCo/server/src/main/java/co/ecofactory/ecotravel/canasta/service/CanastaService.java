package co.ecofactory.ecotravel.canasta.service;

import co.ecofactory.ecotravel.canasta.service.dao.CanastaDAO;
import co.ecofactory.ecotravel.init.Conexion;
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
        dao = new CanastaDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarCanasta", this::listarCanasta);

    }

    public void listarCanasta(Message<JsonObject> message) {
        System.out.println("listarCanasta");
        try {

            //CompletableFuture<List<JsonObject>> data = this.dao.listarCanasta(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarCanasta("1");
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


    public void agregarCanasta(Message<JsonObject> message) {

        System.out.println("agregarCanasta");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.agregarCanasta();
            System.out.println(11);
            data.whenComplete((ok, error) -> {
                System.out.println("agregarCanasta");
                if (ok != null) {
                    System.out.println("agregarCanasta:OK" + ok);
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


    public void quitarCanasta(Message<JsonObject> message) {

        System.out.println("quitarCanasta");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.quitarCanasta();
            System.out.println(11);
            data.whenComplete((ok, error) -> {
                System.out.println("quitarCanasta");
                if (ok != null) {
                    System.out.println("quitarCanasta:OK" + ok);
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
