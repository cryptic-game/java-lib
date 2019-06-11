package net.cryptic_game.microservice;

import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONObject;

import net.cryptic_game.microservice.endpoint.UserEndpoint;

public class EchoMicroService extends MicroService {
	
	public static HashMap<String, Class<?>> required = new HashMap<String, Class<?>>();
	
	static {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();

		map.put("message", String.class);

		required = map;
	}

	public EchoMicroService() {
		super("echo");
		
		addUserEndpoint(new UserEndpoint(required, "echo") {

			@Override
			public JSONObject execute(JSONObject data, UUID user) {
				Echo echo = Echo.create((String) data.get("message"));
				
				System.out.println(echo); // just to show the result
				
				return data;
			}

		});
		
		start();
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();

		new EchoMicroService();
	}

}
