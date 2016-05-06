package co.ecofactory.ecotravel.seguridad.auth.facebookAuth;


import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

public class FacebookAuth extends SeguridadService {

    static JWTAuth provider;

    public synchronized static JWTAuth generateJWTAuthProvider(Vertx vertx) {
        if (provider == null) {
            JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                    // .put("path", "./src/main/java/keystore.jceks")
                    .put("path", "C:\\Users\\Asistente\\Documents\\GitHub\\EcoTravelCo\\EcoTravelCo\\server\\src\\main\\java\\keystore.jceks")
                    .put("type", "jceks")
                    .put("password", "secret"));

            provider = JWTAuth.create(vertx, config);
        }

        return provider;
    }


    @Override
    public void start() throws Exception {
        this.getVertx().eventBus().consumer("autenticarFB", this::autenticar);

        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", System.getenv("KEY_STORE") + "/keystore.jceks")
                //.put("path", "C:\\Users\\Asistente\\Documents\\GitHub\\EcoTravelCo\\EcoTravelCo\\server\\src\\main\\java\\keystore.jceks")
                .put("type", "jceks")
                .put("password", "secret"));
    provider = JWTAuth.create(this.getVertx(), config);
        provider = generateJWTAuthProvider(this.getVertx());

    }


    @Override
    public void autenticar(Message<JsonObject> message){
        System.out.println("autenticdar facebook: " + message.body());

        try {

            JsonObject entrada = message.body();
            this.getVertx().eventBus().send("consultarUsuarioPorLogin", entrada.getString("correo"),
                    res -> {
                        if (res.succeeded()) {

                            JsonObject persona = (JsonObject) res.result().body();

                            if (persona != null) {
//                                if (persona.getString("contrasenia").equals(entrada.getString("contrasenia"))){
                                    String token = provider.generateToken(new JsonObject().put("tipo", persona.getString("tipo"))
                                                    .put("id", persona.getInteger("id"))
                                            , new JWTOptions());
                                    JsonObject response = new JsonObject();
                                    response.put("nombre", persona.getString("nombre"));
                                    response.put("apellido", persona.getString("apellido"));
                                    response.put("correo_electronico", persona.getString("correo_electronico"));
                                    //   response.put("foto", persona.getString("foto"));
                                    response.put("tipo", persona.getString("tipo"));
                                    response.put("token", token);
                                    message.reply(response);
                            /*    }
                            else {
                                    message.fail(401, "Error al autenticar el usuario");
                                }*/
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
