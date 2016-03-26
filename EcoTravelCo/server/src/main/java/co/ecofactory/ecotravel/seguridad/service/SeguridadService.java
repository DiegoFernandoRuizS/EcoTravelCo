package co.ecofactory.ecotravel.seguridad.service;

import co.ecofactory.ecotravel.usuario.service.dao.UsuarioDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Created by samuel on 2/15/16.
 */
public class SeguridadService extends AbstractVerticle {

    static JWTAuth provider;

    public synchronized static JWTAuth generateJWTAuthProvider(Vertx vertx) {
        if (provider == null) {
            JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                    .put("path", "./src/main/java/keystore.jceks")
                    .put("type", "jceks")
                    .put("password", "secret"));

            provider = JWTAuth.create(vertx, config);
        }

        return provider;
    }

    @Override
    public void start() throws Exception {
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("autenticarUsuario", this::autenticarUsuario);

       // System.out.println(new File("./src/main/java").getAbsolutePath());
      //  System.out.println("VARRR "+System.getenv("KEY_STORE"));
      JsonObject config = new JsonObject().put("keyStore", new JsonObject()
            //    .put("path", System.getenv("KEY_STORE")+"\\keystore.jceks")
              .put("path", "C:\\Users\\Asistente\\Documents\\GitHub\\EcoTravelCo\\EcoTravelCo\\server\\src\\main\\java\\keystore.jceks")
                .put("type", "jceks")
                .put("password", "secret"));

        provider = JWTAuth.create(this.getVertx(), config);
        provider = generateJWTAuthProvider(this.getVertx());

    }


    public void autenticarUsuario(Message<JsonObject> message) {
        System.out.println("Service autenticar usuario" + message.body());
        try {

            JsonObject entrada = message.body();

            this.getVertx().eventBus().send("consultarUsuarioPorLogin", entrada.getString("login"),
                    res -> {
                        if (res.succeeded()) {

                            JsonObject persona = (JsonObject) res.result().body();

                            if (persona != null) {
                                if (persona.getString("contrasenia").equals(entrada.getString("contrasenia"))) {

                                    String token = provider.generateToken(new JsonObject().put("tipo", persona.getString("tipo"))
                                                    .put("id", persona.getInteger("id"))
                                            , new JWTOptions());

                                    JsonObject response = new JsonObject();
                                    response.put("nombre", persona.getString("nombre"));
                                    response.put("apellido", persona.getString("apellido"));
                                    response.put("correo_electronico", persona.getString("correo_electronico"));
                                    response.put("token", token);
                                    message.reply(response);
                                } else {
                                    message.fail(401, "Error al autenticar el usuario");


                                }
                            } else {
                                message.fail(401, "Error al autenticar el usuario");
                            }


                        } else {
                            if (res.cause() != null) {
                                res.cause().printStackTrace();
                            }

                            message.fail(401, "Error al autenticar el usuario");
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");

        }
    }
}
