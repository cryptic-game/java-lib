package net.cryptic_game.microservice;

import java.util.Arrays;
import java.util.UUID;

import org.json.simple.JSONObject;

public class EchoMicroService extends MicroService {

	public EchoMicroService() {
		super("echo");
	}

	@Override
	public JSONObject handle(String[] endpoint, final JSONObject data, final UUID user) {
		System.out.println("endpoint: " + Arrays.toString(endpoint));
		System.out.println("data: " + data.toString());
		System.out.println("user: " + user.toString());
		System.out.println("");
		
		return data;
	}

	@Override
	public JSONObject handleFromMicroService(JSONObject data) {
		System.out.println("data: " + data.toString());
		
		return data;
	}

}
