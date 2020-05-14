package net.cryptic_game.microservice.model;

import net.cryptic_game.microservice.db.Database;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class Model {

    @Id
    @Type(type = "uuid-char")
    protected UUID uuid;

    public UUID getUUID() {
        return uuid;
    }

    public void delete() {
        Session session = Database.getInstance().openSession();
        session.beginTransaction();

        session.delete(this);

        session.getTransaction().commit();
        session.close();
    }
}
