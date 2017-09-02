package ar.fiuba.taller.dispatcher;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.Constants;

public class App {
	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure(Constants.LOGGER_CONF);
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Disparando el dispatcher");
		Thread dispatcherThread = new Thread(new Dispatcher());
		dispatcherThread.start();
		try {
			dispatcherThread.join();
		} catch (InterruptedException e) {
			logger.error("Error al joinear el dispatcher");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
