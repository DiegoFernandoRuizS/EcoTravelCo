package co.ecofactory.ecotravel.seguridad.service;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;


//Decorador
public abstract class SeguridadService extends AbstractVerticle {

    public static JWTAuth provider;
    public Vertx vertx;

    public SeguridadService(Vertx ver) {
        vertx =ver;
    }

    public synchronized static JWTAuth generateJWTAuthProvider(Vertx vertx) {
        if (provider == null) {
            JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                    .put("path", "./mod-core/src/main/java/keystore.jceks")
                    .put("type", "jceks")
                    .put("password", "secret"));

            provider = JWTAuth.create(vertx, config);
        }

        return provider;
    }


    public abstract void start() throws Exception;
    public abstract void autenticar(Message<JsonObject> message);

}
