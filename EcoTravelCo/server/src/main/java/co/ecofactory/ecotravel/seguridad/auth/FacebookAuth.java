package co.ecofactory.ecotravel.seguridad.auth;


import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

public class FacebookAuth extends SeguridadService {

    @Override
    public void start() throws Exception{
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("autenticarFB", this::autenticar);
    }

    @Override
    public void autenticar(Message<JsonObject> message) {
        System.out.println("---------------- Facebook "+message);
    }

}
