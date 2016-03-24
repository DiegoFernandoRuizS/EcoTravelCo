package co.ecofactory.ecotravel.cliente.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.usuario.service.dao.UsuarioDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

/**
 * Created by samuel on 2/15/16.
 */
public class ClienteService extends AbstractVerticle {
    private UsuarioDAO dao;

    @Override
    public void start() throws Exception {
        dao = new UsuarioDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus

        this.getVertx().eventBus().consumer("insertarCliente", this::insertarCliente);
        this.getVertx().eventBus().consumer("insertarProveedor", this::insertarProveedor);

    }

    //Insertar usuario
    public void insertarProveedor(Message<JsonObject> message) {
        System.out.println("Service insertarUsuario" + message.body());
        try {

            CompletableFuture<JsonObject> data = this.dao.insertarUsuario(message.body());
            data.whenComplete((ok, error) -> {
                System.out.println("insertarUsuario");
                if (ok != null) {
                    System.out.println("insertarProducto:OK" + ok);
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

    //Insertar usuario
    public void insertarCliente(Message<JsonObject> message) {
        System.out.println("Service insertarUsuario" + message.body());
        try {

            JsonObject datos = message.body();
            datos.put("tipo","CLIENTE");

            CompletableFuture<JsonObject> data = this.dao.insertarUsuario(datos);
            data.whenComplete((ok, error) -> {
                System.out.println("insertarUsuario");
                if (ok != null) {
                    System.out.println("insertarProducto:OK" + ok);
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
}
