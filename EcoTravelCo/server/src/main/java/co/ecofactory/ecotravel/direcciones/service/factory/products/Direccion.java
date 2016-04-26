/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;

import co.ecofactory.ecotravel.utils.JsonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;



public abstract class Direccion {

    public JDBCClient dataAccess;

    private int tipo;

    public abstract int getType();

}