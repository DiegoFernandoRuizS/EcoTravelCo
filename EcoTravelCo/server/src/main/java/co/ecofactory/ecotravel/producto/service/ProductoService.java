package co.ecofactory.ecotravel.producto.service;

import co.ecofactory.ecotravel.producto.service.dao.ProductoDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;

/**
 * Created by samuel on 2/15/16.
 */
public class ProductoService extends AbstractVerticle {
    private ProductoDAO dao;

    @Override
    public void start() throws Exception {
        dao = new ProductoDAO(this.getVertx(), new JsonObject()
                .put("url", "jdbc:postgresql://localhost:5432/ecotravelco")
                .put("driver_class", "org.postgresql.Driver")
                .put("user","postgres").put("password","password")
            //    .put("url", "jdbc:postgres://54.227.245.197:5432/d5edkisov7ljbj")
            //    .put("driver_class", "org.postgresql.Driver")
            //    .put("user","opojrqcxmvacqx").put("password","97KRqSwF2Y3CWkCSI_PGPPzKPD")
                .put("max_pool_size", 30));

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarProductos", this::listarProductos);

    }

    public void listarProductos(Message<JsonObject> message) {

        System.out.println("listarProductos");

        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarProductos();
            System.out.println(11);
            data.whenComplete((ok, error) -> {
                System.out.println("listarProductos");
                if (ok != null) {
                    System.out.println("listarProductos:OK" + ok);
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
