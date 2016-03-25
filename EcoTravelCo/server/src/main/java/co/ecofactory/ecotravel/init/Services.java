package co.ecofactory.ecotravel.init;

import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.impl.JWTUser;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by samuel on 2/15/16.
 */
public class Services {
    private static Router mainRouter;

    static void init() {
        HttpServer server = VertxFactory.getVertxInstance().createHttpServer();
        mainRouter = Router.router(VertxFactory.getVertxInstance());
        server.requestHandler(mainRouter::accept);
        server.listen(8181);

        mainRouter.route().handler(BodyHandler.create());
        mainRouter.route("/*").handler(r -> {
            r.response().headers().add("Access-Control-Allow-Headers", "token,access-control-allow-origin,origin, authorization, accept, content-type, x-requested-with,user,password,Content-Type,Origin,X-Requested-With,application/json")
                    .add("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS")
                    .add("Access-Control-Max-Age", "3600")
                    .add("Access-Control-Allow-Credentials", "true")
                    .add("Access-Control-Allow-Origin", "http://localhost:9291");

            String token = r.request().getHeader("token");

            if (!r.request().method().equals(HttpMethod.OPTIONS)) {
                System.out.println(r.request().absoluteURI());
                System.out.println("token->" + token);

                if (token == null) {
                    r.next();
                } else {
                    JsonObject authInfo = new JsonObject().put("jwt", token);

                    JWTAuth provider = SeguridadService.generateJWTAuthProvider(VertxFactory.getVertxInstance());

                    provider.authenticate(authInfo, response -> {
                        if (response.failed()) {
                            r.response().end("ERROR");
                        } else {
                            System.out.println(response.result());
                            JWTUser user = (JWTUser) response.result();

                            System.out.println(user.principal());

                            try {
                                r.request().params().add("user-id", new String(user.principal().getInteger("id").toString()));
                                r.request().params().add("user-tipo", "" + user.principal().getString("tipo").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            r.next();
                        }

                    });

                }

            }

        });

        mainRouter.options("/*").handler(r -> {

            r.response().end("OK");


        });

    }

    public static Router getMainRouter() {
        return mainRouter;
    }
}
