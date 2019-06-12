package net.cryptic_game.microservice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.cryptic_game.microservice.db.Database;
import net.cryptic_game.microservice.model.Model;

public class Echo extends Model {

	static {
		Database.getDatabase().update("CREATE TABLE IF NOT EXISTS `echo` (uuid VARCHAR(36), "
				+ "message VARCHAR(255), PRIMARY KEY(uuid));");
	}

	private String message;

	private Echo(UUID uuid, String message) {
		super("echo");
		this.uuid = uuid;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public static Echo create(String message) {
		UUID uuid = UUID.randomUUID();

		Echo echo = new Echo(uuid, message);

		db.update("INSERT INTO `echo` (`uuid`, `message`) VALUES (?, ?)", uuid.toString(), message);

		return echo;
	}

	public static Echo get(UUID uuid) {
		ResultSet rs = db.getResult("SELECT * FROM `echo` WHERE `uuid`=?", uuid.toString());
		
		try {
			if(rs.next()) {
				return new Echo(uuid, rs.getString("message"));
			}
		} catch (SQLException e) {
		}
		
		return null;
	}

}
