package net.cryptic_game.microservice.utils;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSONUtils {

    private Map<Object, Object> jsonMap;

    private JSONUtils() {
        this.jsonMap = new HashMap<>();
    }

    public JSONUtils add(Object key, Object value)  {
        this.jsonMap.put(key, value);
        return this;
    }

    public JSONObject build() {
        return new JSONObject(jsonMap);
    }

    public static JSONUtils json() {
        return new JSONUtils();
    }

    public static JSONObject error(String message) {
        return simple("error", message);
    }

    public static JSONObject simple(Object key, Object value) {
        JSONUtils jsonUtils = new JSONUtils();

        jsonUtils.add(key, value);

        return jsonUtils.build();
    }

    public static JSONObject empty() {
        return new JSONObject(new HashMap());
    }

}
