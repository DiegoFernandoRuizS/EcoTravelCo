package co.ecofactory.ecotravel.init;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
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
        mainRouter.route("/*").handler(r->{
            r.response().headers().add("Access-Control-Allow-Headers" ,"access-control-allow-origin,origin, authorization, accept, content-type, x-requested-with,user,password,Content-Type,Origin,X-Requested-With,application/json")
                    .add("Access-Control-Allow-Methods","GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS")
                    .add("Access-Control-Max-Age" ,"3600")
                    .add("Access-Control-Allow-Credentials","true")
                    .add("Access-Control-Allow-Origin", "http://localhost:9291");

            r.next();
        });

        mainRouter.options("/*").handler(r->{

            r.response().end("OK");


        });

    }
    public static Router getMainRouter(){
        return mainRouter;
    }
}
