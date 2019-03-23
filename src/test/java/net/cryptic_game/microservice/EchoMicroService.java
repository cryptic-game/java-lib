package net.cryptic_game.microservice;

import java.util.Arrays;
import java.util.UUID;

import org.json.simple.JSONObject;

public class EchoMicroService extends MicroService {

	public EchoMicroService() {
		super("echo");
	}

	@Override
	public JSONObject handle(String[] endpoint, JSONObject data, UUID user) {
		System.out.println("endpoint: " + Arrays.toString(endpoint));
		System.out.println("data: " + data.toString());
		System.out.println("user: " + user.toString());
		System.out.println("");
		return data;
	}

	@Override
	public void handleFromMicroService(String ms, JSONObject data, UUID tag) {
		System.out.println("microservice: " + ms);
		System.out.println("data: " + data.toString());
		System.out.println("tag: " + tag.toString());
		
		this.sendToMicroService(ms, data, tag);
	}

}
