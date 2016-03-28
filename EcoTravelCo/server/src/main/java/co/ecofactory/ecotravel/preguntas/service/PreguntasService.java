package co.ecofactory.ecotravel.preguntas.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.preguntas.service.dao.PreguntasDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PreguntasService extends AbstractVerticle {
    private PreguntasDAO dao;

    @Override
    public void start() throws Exception {
        dao = new PreguntasDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarPreguntas", this::listarPreguntas);
        this.getVertx().eventBus().consumer("agregarPregunta", this::agregarPregunta);
        this.getVertx().eventBus().consumer("listarPreguntasByUser", this::listarPreguntasByUser);
        this.getVertx().eventBus().consumer("responderPregunta", this::responderPregunta);

    }

    public void listarPreguntas(Message<JsonObject> message) {
        try {
            int idProducto = Integer.parseInt(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarPreguntas(idProducto);
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


    public void listarPreguntasByUser(Message<JsonObject> message) {
        try {
            int idUsuario = Integer.parseInt(message.body().getString("id"));
            int tipo = Integer.parseInt(message.body().getString("tipo"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarPreguntasByUser(idUsuario,tipo);
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


    public void responderPregunta(Message<JsonObject> message) {
        try {
            int id_pregunta = message.body().getInteger("id_pregunta");
            String respuesta = message.body().getString("respuesta");
            CompletableFuture<JsonObject> data = this.dao.responderPregunta(id_pregunta,respuesta);
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


    public void agregarPregunta(Message<JsonObject> message) {
        try {
            int idUsuario = message.body().getInteger("id_usuario");
            int idProducto = message.body().getInteger("id_producto");
            String preg = message.body().getString("pregunta");
            CompletableFuture<JsonObject> pregunta = this.dao.agregarPregunta(idUsuario, idProducto, preg);
            pregunta.whenComplete((ok, error) -> {
                pregunta.whenComplete((okItem, errorItem) -> {
                    if (okItem != null) {
                        message.reply(okItem);
                    } else {
                        error.printStackTrace();
                        message.fail(0, "ERROR in Item");
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

}
