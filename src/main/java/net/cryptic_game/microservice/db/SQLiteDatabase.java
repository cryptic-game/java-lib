package net.cryptic_game.microservice.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;

public class SQLiteDatabase extends Database {

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public SQLiteDatabase(String name) throws SQLException {
		super(DriverManager.getConnection("jdbc:sqlite:" + Config.get(DefaultConfig.STORAGE_LOCATION) + name));
	}

}
