package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

public class Downloader implements Runnable {

	
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public void run() {
		logger.info("Estoy en el downloader");

	}

}
