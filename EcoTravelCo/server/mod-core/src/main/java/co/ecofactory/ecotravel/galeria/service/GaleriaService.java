package co.ecofactory.ecotravel.galeria.service;

import co.ecofactory.ecotravel.galeria.service.dao.GaleriaDAO;
import co.ecofactory.ecotravel.init.Conexion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GaleriaService extends AbstractVerticle {
    private GaleriaDAO dao;

    @Override
    public void start() throws Exception {
        dao = new GaleriaDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("listarGaleria", this::listarGaleria);
        this.getVertx().eventBus().consumer("insertarImagen", this::insertarImagen);
        this.getVertx().eventBus().consumer("eliminarImagen", this::eliminarImagen);
    }

    public void listarGaleria(Message<JsonObject> message) {
        try {
            int idProducto = Integer.parseInt(message.body().getString("id"));
            CompletableFuture<List<JsonObject>> data = this.dao.listarGaleria(idProducto);
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

    public void insertarImagen(Message<JsonObject> message) {
        try {
            String imagen = message.body().getString("imagen");
            int idProducto = message.body().getInteger("idProducto");
            int principal = message.body().getInteger("principal");
            CompletableFuture<JsonObject> data = this.dao.insertarImagen(imagen, idProducto, principal);
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


    public void eliminarImagen(Message<JsonObject> message) {
        try {
            int idImagen = message.body().getInteger("idImagen");
            //Consultamos la información de la imagen para saber si es principal
            CompletableFuture<JsonObject> imagen = this.dao.consultarImagen(idImagen);
            imagen.whenComplete((ok, error) -> {
                if (ok != null) {
                    int principal = ((JsonObject) ok.getJsonArray("rows").getValue(0)).getInteger("foto_principal");
                    int idProducto = ((JsonObject) ok.getJsonArray("rows").getValue(0)).getInteger("producto_id");
                    //Eliminamos la imagen
                    CompletableFuture<JsonObject> data = this.dao.eliminarImagen(idImagen);
                    data.whenComplete((okDel, errorDel) -> {
                        if (okDel != null) {
                            if(principal == 1){
                                //Eliminamos la imagen
                                CompletableFuture<JsonObject> cambiarPrin = this.dao.asignarImagenPrincipal(idProducto);
                                cambiarPrin.whenComplete((okCamb, errorCamb) -> {
                                    if (okCamb != null) {
                                        message.reply(okDel);
                                    } else {
                                        errorCamb.printStackTrace();
                                        message.fail(0, "ERROR in data");
                                    }
                                });
                            }
                            message.reply(okDel);
                        } else {
                            errorDel.printStackTrace();
                            message.fail(0, "ERROR in data");
                        }
                    });

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