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
        this.getVertx().eventBus().consumer("agregarProductoCanasta", this::agregarProductoCanasta);
        this.getVertx().eventBus().consumer("modificarProductoCanasta", this::modificarProductoCanasta);
        this.getVertx().eventBus().consumer("eliminarProductoCanasta", this::eliminarProductoCanasta);
    }

    public void listarCanasta(Message<JsonObject> message) {
        try {
            int idPersona = Integer.parseInt(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarCanasta(idPersona);
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


    public void agregarProductoCanasta(Message<JsonObject> message) {
        try {
            int idUsuario = Integer.parseInt(message.body().getString("id_usuario"));
            int idProducto = Integer.parseInt(message.body().getString("id_producto"));
            int cantidad = Integer.parseInt(message.body().getString("cantidad"));

            CompletableFuture<JsonObject> canasta = this.dao.crearCanasta(idUsuario);

            canasta.whenComplete((ok, error) -> {
                if (ok != null) {
                    CompletableFuture<JsonObject> item = this.dao.agregarCanasta(idUsuario, idProducto, cantidad);
                    this.dao.adicionarValorCanasta(idUsuario, idProducto, cantidad);
                    item.whenComplete((okItem, errorItem) -> {
                        if (okItem != null) {
                            message.reply(okItem);
                        } else {
                            error.printStackTrace();
                            message.fail(0, "ERROR in Item");
                        }
                    });
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in Carro");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }


    public void eliminarProductoCanasta(Message<JsonObject> message) {
        try {
            int idOrderItem = Integer.parseInt(message.body().getString("id_orden_item"));
            this.dao.restarValorCanasta(idOrderItem);
            CompletableFuture<JsonObject> data = this.dao.eliminarProductoCanasta(idOrderItem);

            data.whenComplete((ok, error) -> {
                if (ok != null) {
                    message.reply(ok);
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

    public void modificarProductoCanasta(Message<JsonObject> message) {
        try {
            CompletableFuture<List<JsonObject>> data = this.dao.modificarProductoCanasta(
                    message.body().getString("id_orden_item"), message.body().getString("cantidad"));
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
