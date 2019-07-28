package net.cryptic_game.microservice.utils;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JSONUtilsTest {

    @Test
    void createRandomJSONObject() {
        Map<String, String> jsonMap = new HashMap<>();

        JSONUtils jsonUtils = JSONUtils.json();

        for(int i = 0; i < new Random().nextInt(10); i++) {
            String key = UUID.randomUUID().toString();
            String value = UUID.randomUUID().toString();

            jsonMap.put(key, value);
            jsonUtils.add(key, value);
        }

        assertEquals(jsonUtils.build(), new JSONObject(jsonMap));
    }

    @Test
    void shouldCreateEmptyJSONObject() {
        JSONObject empty = new JSONObject(new HashMap());

        assertEquals(JSONUtils.empty(), empty);
    }

    @Test
    void shouldCreateErrorJSONObject() {
        String errorMessage = "just a test";

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("error", errorMessage);
        JSONObject error = new JSONObject(jsonMap);

        assertEquals(JSONUtils.error(errorMessage), error);
    }

    @Test
    void shouldCreateSimpleJSONObject() {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("abc", "def");
        JSONObject json = new JSONObject(jsonMap);

        assertEquals(JSONUtils.simple("abc", "def"), json);
    }

}