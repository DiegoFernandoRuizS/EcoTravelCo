package co.ecofactory.ecotravel.module.contract;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Created by samuel on 2/15/16.
 */
public interface Modulo {
    void inicializar (Vertx vertx);

    String getNombre();
    Router getRutas (Vertx vertx);

}
