package net.cryptic_game.microservice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import net.cryptic_game.microservice.endpoint.UserEndpoint;

public class EchoEndpoint {

	@UserEndpoint(path = { "echo" }, keys = { "message" }, types = { String.class })
	public static JSONObject echo(JSONObject data, UUID user) {
		Echo.create((String) data.get("message"));

		Map<String, Object> jsonMap = new HashMap<String, Object>();

		jsonMap.put("device_uuid", UUID.randomUUID().toString());

		System.out.println(
				MicroService.instance.contactMicroservice("device", new String[] { "exist" }, new JSONObject(jsonMap)));

		return data;
	}

}
