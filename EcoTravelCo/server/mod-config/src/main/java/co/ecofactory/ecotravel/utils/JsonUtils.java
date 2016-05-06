package co.ecofactory.ecotravel.utils;

import io.vertx.core.json.JsonArray;

import java.time.Instant;


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

