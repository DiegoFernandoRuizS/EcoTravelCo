package co.ecofactory.ecotravel.init;

import io.vertx.core.Vertx;

/**
 * Created by samuel on 2/15/16.
 */
public class VertxFactory {
    private static Vertx vertx;

    static {
        vertx = Vertx.vertx();
    }

    public static Vertx getVertxInstance() {
        return vertx;
    }

}
