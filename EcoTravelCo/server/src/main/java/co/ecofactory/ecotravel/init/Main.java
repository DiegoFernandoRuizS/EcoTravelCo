package co.ecofactory.ecotravel.init;

import io.vertx.core.Vertx;

/**
 * Created by samuel on 2/15/16.
 */
public class Main {
    public static void main(String... args) {

        Services.init();
        Modulos.init();

    }
}
