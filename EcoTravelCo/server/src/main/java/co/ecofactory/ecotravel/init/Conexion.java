package co.ecofactory.ecotravel.init;

import io.vertx.core.json.JsonObject;

/**
 * Created by Cristian Huertas on 24/03/16.
 */
public class Conexion {
    private JsonObject conf;

    public JsonObject getConf(){
        conf = new JsonObject();
        conf.put("url", "jdbc:postgresql://107.21.218.93:5432/djf63h0pqr06s?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
       // conf.put("url", "jdbc:postgresql://localhost:5432/djf63h0pqr06s");
        conf.put("driver_class", "org.postgresql.Driver");
        conf.put("user","postgres").put("password","eduardo88$");
        conf.put("max_pool_size", 30);

        return conf;
    }

}
