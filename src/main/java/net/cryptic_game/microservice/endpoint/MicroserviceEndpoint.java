package net.cryptic_game.microservice.endpoint;

import java.util.HashMap;

import org.json.simple.JSONObject;

public abstract class MicroserviceEndpoint extends Endpoint {
	
	public MicroserviceEndpoint(HashMap<String, Class<?>> required, String... path) {
		super(path, required);
	}
	
	public MicroserviceEndpoint(String... path) {
		super(path);
	}

	public abstract JSONObject execute(JSONObject data, String ms);
	
}
