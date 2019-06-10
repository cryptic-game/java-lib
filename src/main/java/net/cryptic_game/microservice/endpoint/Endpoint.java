package net.cryptic_game.microservice.endpoint;

import java.util.HashMap;

import org.json.simple.JSONObject;

public abstract class Endpoint {
	
	private HashMap<String, Class<?>> required = new HashMap<String, Class<?>>();
	
	public boolean checkData(JSONObject obj) {
		for(String key : required.keySet()) {
			if(!obj.containsKey(key) || obj.get(key).getClass() != required.get(key)) {
				return false;
			}
		}
		return true;
	}
	
	public abstract HashMap<String, Class<?>> updateRequired();
	
	public void setRequired(HashMap<String, Class<?>> required) {
		this.required = required;
	}

}
