package net.cryptic_game.microservice.endpoint;

import java.util.HashMap;
import java.util.UUID;

import org.json.simple.JSONObject;

public abstract class UserEndpoint extends Endpoint {

	public UserEndpoint(HashMap<String, Class<?>> required, String... path) {
		super(path, required);
	}
	
	public UserEndpoint(String... path) {
		super(path);
	}

	public abstract JSONObject execute(JSONObject data, UUID user);

}
