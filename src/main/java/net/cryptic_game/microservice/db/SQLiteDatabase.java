package net.cryptic_game.microservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;

import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;

public class SQLiteDatabase extends Database {

	private static Properties properties;
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		SQLiteConfig sqLiteConfig = new SQLiteConfig();
		properties = sqLiteConfig.toProperties();
		properties.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss");
	}
	
	private String name;

	public SQLiteDatabase(String name) {
		this.name = name;
	}
	
	public SQLiteDatabase() {
		this.name = Config.get(DefaultConfig.STORAGE_LOCATION);
	}

	@Override
	public Connection createConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + Config.get(DefaultConfig.STORAGE_LOCATION) + name,
				properties);
	}

}
