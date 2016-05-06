/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

public class Direccion_Basica extends Direccion{

    public Direccion_Basica(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }


    public int getType() {

        return 0;
    }


}
