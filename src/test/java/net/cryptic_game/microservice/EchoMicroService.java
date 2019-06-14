package net.cryptic_game.microservice;

import org.apache.log4j.BasicConfigurator;

public class EchoMicroService extends MicroService {
	
	public EchoMicroService() {
		super("echo");
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();

		new EchoMicroService();
	}

}
