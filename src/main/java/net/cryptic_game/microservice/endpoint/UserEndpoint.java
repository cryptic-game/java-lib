package net.cryptic_game.microservice.endpoint;

import java.util.UUID;

import org.json.simple.JSONObject;

public abstract class UserEndpoint extends Endpoint {

	public abstract JSONObject execute(JSONObject data, UUID user);

}
