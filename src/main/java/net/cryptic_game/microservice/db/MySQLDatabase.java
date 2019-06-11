package net.cryptic_game.microservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;

public class MySQLDatabase extends Database {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection createConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + Config.get(DefaultConfig.MYSQL_HOSTNAME) + ":"
				+ Config.getInteger(DefaultConfig.MYSQL_PORT) + "/" + Config.get(DefaultConfig.MYSQL_DATABASE)
				+ "?autoReconnect=true" + "&user=" + Config.get(DefaultConfig.MYSQL_USERNAME) + "&password="
				+ Config.get(DefaultConfig.MYSQL_PASSWORD));
	}

}
