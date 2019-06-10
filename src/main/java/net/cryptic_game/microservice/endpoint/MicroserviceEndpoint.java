package net.cryptic_game.microservice.endpoint;

import org.json.simple.JSONObject;

public abstract class MicroserviceEndpoint extends Endpoint {

	public abstract JSONObject execute(JSONObject data, String ms);
	
}
