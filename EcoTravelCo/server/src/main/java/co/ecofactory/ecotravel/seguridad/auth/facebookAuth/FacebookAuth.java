package co.ecofactory.ecotravel.seguridad.auth.facebookAuth;


import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;
import facebook4j.auth.AccessToken;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FacebookAuth extends SeguridadService {

    @Override
    public void start() throws Exception {
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("autenticarFB", this::autenticar);
    }

    @Override
    public void autenticar(Message<JsonObject> message){
        System.out.println("FACEBOOK" + message.body());
    }


}


