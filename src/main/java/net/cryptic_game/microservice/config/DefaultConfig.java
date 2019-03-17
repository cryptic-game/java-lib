package net.cryptic_game.microservice.config;

import java.util.HashMap;
import java.util.Map;

public enum DefaultConfig {
	
	MSSOCKET_HOST("127.0.0.1"),
	MSSOCKET_PORT(1239),
	STORAGE_LOCATION("data/");
	
	
	private Object value;
	
	DefaultConfig(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public static Map<String, String> defaults() {
		Map<String, String> defaults = new HashMap<String, String>();
		
		for(DefaultConfig e : DefaultConfig.values()) {
			defaults.put(e.toString(), e.getValue().toString());
		}
		
		return defaults;
	}

}
