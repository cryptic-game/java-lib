package net.cryptic_game.microservice.wrapper;

import java.util.Date;
import java.util.UUID;

public class User {

    UUID uuid;
    String name;
    String mail;
    Date created;
    Date last;

    public User(UUID uuid, String name, String mail, Date created, Date last) {
        this.uuid = uuid;
        this.name = name;
        this.mail = mail;
        this.created = created;
        this.last = last;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public Date getCreated() {
        return created;
    }

    public Date getLast() {
        return last;
    }

}
