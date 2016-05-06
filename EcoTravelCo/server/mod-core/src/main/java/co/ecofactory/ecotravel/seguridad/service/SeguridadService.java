package co.ecofactory.ecotravel.seguridad.service;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

//Decorador
public abstract class SeguridadService extends AbstractVerticle {

    public abstract void autenticar(Message<JsonObject> message);

}
