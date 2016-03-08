package co.ecofactory.ecotravel.init;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String... args) {

        Services.init();
        Modulos.init();

    }
}
