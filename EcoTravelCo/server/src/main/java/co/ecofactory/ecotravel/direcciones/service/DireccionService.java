package co.ecofactory.ecotravel.direcciones.service;

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
        direccion = FabricaDirecciones.getDireccion("Ruta",this.getVertx(), new Conexion().getConf());
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("dirreccionTipo", this::dirreccionTipo);


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


}
