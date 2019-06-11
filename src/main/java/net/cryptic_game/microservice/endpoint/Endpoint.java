package net.cryptic_game.microservice.endpoint;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;

public abstract class Endpoint {
	
	private HashMap<String, Class<?>> required = new HashMap<String, Class<?>>();
	private String[] path;
	
	public Endpoint(String[] path, HashMap<String, Class<?>> required) {
		this.path = path;
		this.required = required;
	}
	
	public Endpoint(String[] path) {
		this(path, new HashMap<String, Class<?>>());
	}
	
	public boolean checkData(JSONObject obj) {
		for(String key : required.keySet()) {
			if(!obj.containsKey(key) || obj.get(key).getClass() != required.get(key)) {
				return false;
			}
		}
		return true;
	}
	
	public String[] getPath() {
		return path;
	}
	
	public void setRequired(HashMap<String, Class<?>> required) {
		this.required = required;
	}
	
	public String toString() {
		return Arrays.deepToString(this.getPath());
	}

}
