package net.cryptic_game.microservice.error;

import org.json.simple.JSONObject;

import static net.cryptic_game.microservice.utils.JSONBuilder.error;

public enum ServerError {

    UNSUPPORTED_FORMAT("unsupported format"),
    MISSING_PARAMETERS("missing parameters"),
    INTERNAL_ERROR("internal error"),
    UNKNOWN_SERVICE("unknown service");


    private JSONObject response;
    private String message;

    ServerError(String message) {
        this.response = error(message);
        this.message = message;
    }

    public JSONObject getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return message;
    }

}
