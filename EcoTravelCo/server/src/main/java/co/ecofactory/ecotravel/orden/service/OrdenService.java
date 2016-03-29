package co.ecofactory.ecotravel.orden.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.orden.service.dao.OrdenDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by samuel on 2/15/16.
 */
public class OrdenService extends AbstractVerticle {
    private OrdenDAO dao;

    @Override
    public void start() throws Exception {
        dao = new OrdenDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarOrden", this::listarOrden);
        this.getVertx().eventBus().consumer("detalleOrden", this::detalleOrden);
        this.getVertx().eventBus().consumer("cancelarOrden", this::cancelarOrden);
        this.getVertx().eventBus().consumer("pagarOrden", this::pagarOrden);
        this.getVertx().eventBus().consumer("calificarServicio", this::calificarServicio);
    }

    public void listarOrden(Message<JsonObject> message) {
        try {
            int idPersona = message.body().getInteger("id");
            CompletableFuture<List<JsonObject>> data = this.dao.listarOrden(idPersona);
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

    public void detalleOrden(Message<JsonObject> message) {
        try {
            int idOrden = Integer.parseInt(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.detalleOrden(idOrden);
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

    public void cancelarOrden(Message<JsonObject> message) {
        try {
            int idOrden = Integer.parseInt(message.body().getString("id_orden"));
            CompletableFuture<JsonObject> can = this.dao.cancelarOrden(idOrden);
            can.whenComplete((ok, error) -> {
                if (ok != null) {
                    CompletableFuture<JsonObject> desc = this.dao.devolverDisponibilidadProductos(idOrden);
                    desc.whenComplete((okDesc, errorDesc) -> {
                        if (okDesc != null) {
                            message.reply(ok);
                        } else {
                            errorDesc.printStackTrace();
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

    public void pagarOrden(Message<JsonObject> message) {
        try {
            int idOrden = Integer.parseInt(message.body().getString("id_orden"));
            CompletableFuture<JsonObject> can = this.dao.pagarOrden(idOrden);
            can.whenComplete((ok, error) -> {
                if (ok != null) {
                    message.reply(ok);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in Item");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
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
}
