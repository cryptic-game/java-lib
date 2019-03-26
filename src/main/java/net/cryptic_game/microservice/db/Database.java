package net.cryptic_game.microservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;

public class Database {

	private Connection connection;

	public Database(String name) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String path = Config.get(DefaultConfig.STORAGE_LOCATION) + name;

		try {
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet getResult(String query) throws SQLException {
		PreparedStatement statement;
		statement = this.connection.prepareStatement(query);

		return statement.executeQuery();
	}

	public void update(String query) throws SQLException {
		PreparedStatement statement = this.connection.prepareStatement(query);

		statement.executeUpdate();
	}

}