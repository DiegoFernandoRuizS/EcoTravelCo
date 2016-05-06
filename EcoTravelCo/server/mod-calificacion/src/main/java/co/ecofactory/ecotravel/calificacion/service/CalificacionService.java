package co.ecofactory.ecotravel.calificacion.service;

import co.ecofactory.ecotravel.calificacion.service.dao.CalificacionDAO;
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
public class CalificacionService extends AbstractVerticle {
    private CalificacionDAO dao;

    @Override
    public void start() throws Exception {
        dao = new CalificacionDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("calificarServicio", this::calificarServicio);
        this.getVertx().eventBus().consumer("listarCalificacion", this::listarCalificacion);
    }

    public void calificarServicio(Message<JsonObject> message) {
        try {

            int idCliente = message.body().getInteger("id_usuario");
            int idProducto = message.body().getInteger("id_producto");
            int idItem = message.body().getInteger("id_item");
            int calificacion = message.body().getInteger("calificacion");
            String comentario = message.body().getString("comentario");


            CompletableFuture<JsonObject> cal = this.dao.calificarServicio(idProducto, idCliente, calificacion, comentario);

            cal.whenComplete((ok, error) -> {
                if (ok != null) {
                    CompletableFuture<JsonObject> producto = this.dao.actualizacionPromedio(idProducto);
                    CompletableFuture<JsonObject> item = this.dao.marcarOrdenItem(idItem);
                    item.whenComplete((okItem, errorItem) -> {
                        if (okItem != null) {
                            message.reply(ok);
                        } else {
                            errorItem.printStackTrace();
                            message.fail(0, "ERROR in Item");
                        }
                    });
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in Calificar");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }


    public void listarCalificacion(Message<JsonObject> message) {
        try {

            CompletableFuture<List<JsonObject>> data = this.dao.listarCalificacion(message.body().getString("id"));
            System.out.println(message.body().getString("id"));
            data.whenComplete((ok, error) -> {
                System.out.println("listarCalificacion");
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
