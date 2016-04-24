package co.ecofactory.ecotravel.message.service;

import co.ecofactory.ecotravel.init.Conexion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MessageService extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        // registro los metodos en el bus

        this.getVertx().eventBus().consumer("listarMensajes", this::listarMensajes);
        this.getVertx().eventBus().consumer("agregarMensaje", this::agregarMensaje);

    }

    public void listarMensajes(Message<JsonObject> message) {
        System.out.println("Service listarUsuarios" + message.body());
        try {

            String idOrigen = message.body().getString("origen");
            String idDestino = message.body().getString("destino");
            message.reply(MessageStore.getMensajes(idOrigen,idDestino));

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }
    //Insertar usuario
    public void agregarMensaje(Message<JsonObject> message) {
        System.out.println("Service insertarProveedor" + message.body());
        try {
            JsonObject datos = message.body();
            MessageStore.agregarMensaje(datos);
            message.reply(new JsonObject());
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }
}
