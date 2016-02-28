package co.ecofactory.ecotravel.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Created by samuel on 2/28/16.
 */
public class JsonUtils {
    public static JsonArray add(JsonArray in, Object data) {
        if (data == null) {
            in.addNull();
        } else {
            in.add(data);

        }
        return in;
    }

    public static JsonArray add(JsonArray in, Instant data) {
        if (data == null) {
            in.addNull();
        } else {
            in.add(data);

        }
        return in;
    }
}
