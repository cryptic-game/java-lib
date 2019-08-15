package net.cryptic_game.microservice.db;

import net.cryptic_game.microservice.config.Config;
import net.cryptic_game.microservice.config.DefaultConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;

public class Database {

	private static Database instance;

	private SessionFactory sessionFactory;

	public Database() {
		instance = this;

		try {
			Configuration cfg = getConfiguration();
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(cfg.getProperties()).build();

			sessionFactory = cfg.buildSessionFactory(serviceRegistry);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	private Configuration getConfiguration() {
		Configuration configuration = new Configuration();

		Properties settings = new Properties();

		settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
		settings.put(Environment.URL, "jdbc:mysql://" + Config.get(DefaultConfig.MYSQL_HOSTNAME) + ":" + Config.get(DefaultConfig.MYSQL_PORT)
				+ "/" + Config.get(DefaultConfig.MYSQL_DATABASE) + "?serverTimezone=" + Calendar.getInstance().getTimeZone().getID());
		settings.put(Environment.USER, Config.get(DefaultConfig.MYSQL_USERNAME));
		settings.put(Environment.PASS, Config.get(DefaultConfig.MYSQL_PASSWORD));
		settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
		settings.put(Environment.SHOW_SQL, "true");
		settings.put(Environment.HBM2DDL_AUTO, "update");

		configuration.setProperties(settings);

		addEntityClassesFromPackage("net.cryptic_game.microservice", configuration);

		return configuration;
	}

	private void addEntityClassesFromPackage(String pkg, Configuration cfg) {
		Reflections reflections = new Reflections(pkg);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Entity.class);

		for(Class entities : annotated) {
			cfg.addAnnotatedClass(entities);
		}
	}

	public static Database getInstance() {
		return instance;
	}
}
