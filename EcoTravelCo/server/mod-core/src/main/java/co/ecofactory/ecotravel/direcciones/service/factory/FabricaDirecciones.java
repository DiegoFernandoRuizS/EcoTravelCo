/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import co.ecofactory.ecotravel.direcciones.service.factory.products.*;

public class FabricaDirecciones {


    public static Direccion getDireccion(String tipo,Vertx vertx, JsonObject conf) {

        if (tipo.equals("Ruta")) {

            return new Direccion_Ruta(vertx, conf);
        }
        else {
            return new Direccion_Basica(vertx, conf);
        }

    }



}
