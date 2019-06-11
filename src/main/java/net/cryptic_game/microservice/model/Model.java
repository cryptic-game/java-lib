package net.cryptic_game.microservice.model;

import java.util.UUID;

import net.cryptic_game.microservice.db.Database;

public abstract class Model {

	protected static Database db = Database.getDatabase();
	protected UUID uuid;
	protected String tablename;

	public UUID getUUID() {
		return uuid;
	}

	public Model(String tablename) {
		this.tablename = tablename;
	}

	public void delete() {
		db.update("DELETE FROM `" + tablename + " WHERE `uuid`=?", this.getUUID().toString());
	}

}
