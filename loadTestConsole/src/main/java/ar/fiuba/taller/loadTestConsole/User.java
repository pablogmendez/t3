package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

public class User implements Runnable {

	final static Logger logger = Logger.getLogger(App.class);
	
	public void run() {
		logger.info("Estoy en el user");

	}

}
