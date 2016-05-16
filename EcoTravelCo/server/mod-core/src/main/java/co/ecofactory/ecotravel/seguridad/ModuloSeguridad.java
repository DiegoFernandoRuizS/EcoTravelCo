package co.ecofactory.ecotravel.seguridad;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.seguridad.auth.basic.Basic;
import co.ecofactory.ecotravel.seguridad.auth.facebookAuth.FacebookAuth;
import co.ecofactory.ecotravel.seguridad.auth.twitterAuth.TwitterAutentication;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ModuloSeguridad implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Seguridad");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);

        //Decorador

        System.getProperties().setProperty("TipoAutenticacion","ALL");

        String getVariability = System.getProperties().getProperty("TipoAutenticacion");

        if(getVariability.equals("ALL")) {
            FacebookAuth facebook = new FacebookAuth(vertx,options);
            TwitterAutentication twitter = new TwitterAutentication(vertx,options);
        }
        else if (getVariability.equals("FACEBOOK")) {
            FacebookAuth facebook = new FacebookAuth(vertx, options);
        }
        else if (getVariability.equals("TWITTER")) {
            TwitterAutentication twitter = new TwitterAutentication(vertx, options);
        }
        else {
            Basic basica = new Basic(vertx, options);
        }


    }

    public String getNombre() {
        return "/seguridad";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);

        rutas.post("/autenticar").handler(rc -> {

            JsonObject _params = rc.getBodyAsJson();

            //vertx.eventBus().send("autenticarUsuario", _params, res -> {
            vertx.eventBus().send("autenticar", _params, res -> {
                if (res.succeeded()) {
                    System.out.println("servidor correcto autenticarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    System.out.println(res.cause());
                    rc.response().setStatusCode(401).end("ERROR al autenticar el usuario");
                }
            });
        });

        //facebook
        rutas.post("/autenticar/fb").handler(rc ->{

            JsonObject _params = rc.getBodyAsJson();
            System.out.println("Lllega? "+_params);

            vertx.eventBus().send("autenticarFB", _params, res -> {
                if (res.succeeded()) {
                    System.out.println("servidor correcto autenticarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    System.out.println(res.cause());
                    rc.response().setStatusCode(401).end("ERROR al autenticar el usuario");
                }
            });
        });



        //twitter
        rutas.get("/autenticar/twitter").handler(rc ->{
            JsonObject _params = new JsonObject();
            vertx.eventBus().send("autenticarTwitter", _params, res -> {
                if (res.succeeded()) {
                    rc.response().end(((JsonObject)res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });

        rutas.get("/twitter/:t/:v").handler(rc ->{
            String oauth_token = rc.request().getParam("t");
            String oauth_verifier = rc.request().getParam("v");
            JsonObject _params = new JsonObject();
            _params.put("oauth_token", oauth_token);
            _params.put("oauth_verifier", oauth_verifier);
            vertx.eventBus().send("getPerfil", _params, res -> {
                if (res.succeeded()) {
                    System.out.println("servidor correcto autenticarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });

        rutas.get("/tweet/:a/:b/:c").handler(rc ->{
            String oauth_token = rc.request().getParam("a");
            String name = rc.request().getParam("b");
            String ven = rc.request().getParam("c");
            JsonObject _params = new JsonObject();
            _params.put("oauth_token", oauth_token);
            _params.put("name", name);
            _params.put("ven", ven);
            vertx.eventBus().send("tweet", _params, res -> {
                if (res.succeeded()) {
                    System.out.println("servidor correcto autenticarUsuario -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo de Direcciones");
                }
            });
        });

        return rutas;
    }



}

