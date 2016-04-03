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
        this.getVertx().eventBus().consumer("eliminarProductoCanasta", this::eliminarProductoCanasta);
        this.getVertx().eventBus().consumer("confirmarCanasta", this::confirmarCanasta);
    }

    public void listarCanasta(Message<JsonObject> message) {
        try {
            int idPersona = message.body().getInteger("id");
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
            int idUsuario = message.body().getInteger("id_usuario");
            int idProducto = message.body().getInteger("id_producto");
            int cantidad = message.body().getInteger("cantidad");

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
            CompletableFuture<JsonObject> resta = this.dao.restarValorCanasta(idOrderItem);

            resta.whenComplete((okResta, errorResta) -> {
                if(okResta != null){
                    CompletableFuture<JsonObject> data = this.dao.eliminarProductoCanasta(idOrderItem);
                    data.whenComplete((ok, error) -> {
                        if (ok != null) {
                            message.reply(ok);
                        } else {
                            error.printStackTrace();
                            message.fail(0, "ERROR in data");
                        }
                    });
                } else {
                    errorResta.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }


    public void confirmarCanasta(Message<JsonObject> message) {
        try {
            int idUsuario = message.body().getInteger("id_usuario");
            CompletableFuture<List<JsonObject>> productosNoDiponible = this.dao.validarDisponibilidadProductos(idUsuario);

            productosNoDiponible.whenComplete((ok, error) -> {
                if(ok != null){
                    if(ok.isEmpty()){
                        this.cambioEstadoCanasta(message);
                    } else {
                        JsonArray arr = new JsonArray();
                        ok.forEach(o -> arr.add(o));
                        message.reply(arr);
                    }
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

    private void cambioEstadoCanasta(Message<JsonObject> message){
        try {
            int idUsuario = message.body().getInteger("id_usuario");
            CompletableFuture<JsonObject> descontarCantidad = this.dao.descontarCantidadProductos(idUsuario);
            descontarCantidad.whenComplete((ok, error) -> {
                if(ok != null){
                    CompletableFuture<JsonObject> data = this.dao.confirmarCanasta(idUsuario);
                    data.whenComplete((okConf, errorConf) -> {
                        if (okConf != null) {
                            message.reply(okConf);
                        } else {
                            errorConf.printStackTrace();
                            message.fail(0, "ERROR in data");
                            }
                        });
                    } else {
                        JsonArray arr = new JsonArray();
                        ok.forEach(o -> arr.add(o));
                        message.reply(arr);
                    }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }
}
