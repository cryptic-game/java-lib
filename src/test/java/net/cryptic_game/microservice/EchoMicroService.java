package net.cryptic_game.microservice;

import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONObject;

import net.cryptic_game.microservice.endpoint.UserEndpoint;

public class EchoMicroService extends MicroService {

	public EchoMicroService() {
		super("echo");

		addUserEndpoint(new UserEndpoint() {

			@Override
			public JSONObject execute(JSONObject data, UUID user) {
				Echo echo = Echo.create((String) data.get("message"));
				
				System.out.println(echo);
				
				return null; // empty json object
			}

			@Override
			public HashMap<String, Class<?>> updateRequired() {
				HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();

				map.put("message", String.class);

				return map;
			}

		}, "echo");
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();

		new EchoMicroService();
	}

}
