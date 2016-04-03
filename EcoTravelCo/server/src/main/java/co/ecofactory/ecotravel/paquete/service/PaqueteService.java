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
        this.getVertx().eventBus().consumer("insertarPaquete", this::insertarPaquete);

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

    public void insertarPaquete(Message<JsonObject> message) {
        System.out.println("Service insertarPaquete" + message.body());
        final int[] llave = {0};
        final int[] idProducto = {0};
        try {
            CompletableFuture<JsonObject> data2 = this.dao.insertarPaquete(message.body());
            data2.whenComplete((ok2, error2) -> {
                if (ok2 != null) {
                    idProducto[0] = (int) ok2.getJsonArray("keys").getValue(0);
                    //Asociando los productos al padre
                    int padre = idProducto[0];
                    CompletableFuture<JsonObject> data = this.dao.asociarProductosAPaquete(message.body(), padre);
                    data.whenComplete((ok, error) -> {
                        if (ok != null) {
                            message.reply(ok);
                        } else {
                            error2.printStackTrace();
                            message.fail(0, "ERROR in data asociando al padre");
                        }
                    });
                    message.reply(ok2);
                } else {
                    error2.printStackTrace();
                    message.fail(0, "ERROR in data producto");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

}


