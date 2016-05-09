/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.concurrent.CompletableFuture;


public abstract class Direccion {

    public JDBCClient dataAccess;

    private int tipo;

    public abstract int getType();

    public abstract CompletableFuture<JsonObject> insertarDireccion(JsonObject nuevoProducto) ;

}