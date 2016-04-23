package co.ecofactory.ecotravel.seguridad.auth;

import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class Twitter extends SeguridadService {

    @Override
    public void autenticar(Message<JsonObject> message) {

    }
}
