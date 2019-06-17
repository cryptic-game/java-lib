package net.cryptic_game.microservice;

import java.util.UUID;

import org.json.simple.JSONObject;

import net.cryptic_game.microservice.endpoint.UserEndpoint;

public class EchoEndpoint {

	@UserEndpoint(path = { "echo" }, keys = { "message" }, types = { String.class })
	public static JSONObject echo(JSONObject data, UUID user) {
		Echo.create((String) data.get("message"));

		return data;
	}

}
