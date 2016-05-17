package co.ecofactory.ecotravel.direcciones.service;

import co.ecofactory.ecotravel.direcciones.service.factory.products.Direccion_Ruta;
import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.direcciones.service.factory.products.Direccion;
import co.ecofactory.ecotravel.direcciones.service.factory.FabricaDirecciones;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DireccionService extends AbstractVerticle {
    private Direccion direccion;

    @Override
    public void start() throws Exception {
        // Crea Fabrica Segun Variabilidad!!!
        direccion = FabricaDirecciones.getDireccion((System.getProperties().getProperty("Ruta") != null ? "Ruta" : ""),this.getVertx(), new Conexion().getConf());
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("dirreccionTipo", this::dirreccionTipo);
        this.getVertx().eventBus().consumer("dirreccionCoord", this::dirreccionCoord);
        this.getVertx().eventBus().consumer("dirreccionCoordIp", this::dirreccionCoordIp);
        this.getVertx().eventBus().consumer("insertarDireccion", this::insertarDireccion);
        this.getVertx().eventBus().consumer("actualizarDireccion", this::actualizarDireccion);
        this.getVertx().eventBus().consumer("borrarDireccion", this::borrarDireccion);


    }

    public void dirreccionTipo(Message<JsonObject> message) {
        try {
            int tipo = direccion.getType();
            JsonArray arr = new JsonArray();
            JsonObject address = new JsonObject();
            address.put("tipo",tipo);
            arr.add(address);
            message.reply(arr);

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al Obtener el tipo de la direccion");
        }
    }

    public void dirreccionCoord(Message<JsonObject> message) {
        try {
            String dir = message.body().getString("dir");
            String[] coord = ((Direccion_Ruta)direccion).getLatLongPositions(dir);
            JsonArray arr = new JsonArray();
            JsonObject address = new JsonObject();
            address.put("latitude",coord[0]);
            address.put("longitude",coord[1]);
            arr.add(address);
            message.reply(arr);

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al Obtener el tipo de la direccion");
        }
    }

    public void dirreccionCoordIp(Message<JsonObject> message) {
        try {
            String dir = message.body().getString("dir");
            String[] coord = ((Direccion_Ruta)direccion).getLatLongPositionsIp(dir);
            JsonArray arr = new JsonArray();
            JsonObject address = new JsonObject();
            address.put("latitude",coord[0]);
            address.put("longitude",coord[1]);
            arr.add(address);
            message.reply(arr);

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al Obtener el tipo de la direccion");
        }
    }

    public void insertarDireccion(Message<JsonObject> message) {
        try {

            CompletableFuture<JsonObject> data = direccion.insertarDireccion( message.body());
            data.whenComplete((ok, error) -> {
                System.out.println("listarProducto");
                if (ok != null) {
                    message.reply(ok);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al crear la direccion");
        }
    }


    public void actualizarDireccion(Message<JsonObject> message) {
        try {

            CompletableFuture<JsonObject> data = direccion.actualizarDireccion( message.body());
            data.whenComplete((ok, error) -> {
                System.out.println("listarProducto");
                if (ok != null) {
                    message.reply(ok);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al crear la direccion");
        }
    }

    public void borrarDireccion(Message<JsonObject> message) {
        try {

            CompletableFuture<JsonObject> data = direccion.borrarDireccion( message.body().getLong("id"));
            data.whenComplete((ok, error) -> {
                System.out.println("listarProducto");
                if (ok != null) {
                    message.reply(ok);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al crear la direccion");
        }
    }


}
