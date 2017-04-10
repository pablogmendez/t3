package ar.fiuba.taller.analyzer;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.Constants;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	PropertyConfigurator.configure(Constants.LOGGER_CONF);
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		try {
			logger.info("Comenzando el analyzer");
			Thread analyzerThread = new Thread(new Analyzer());
			analyzerThread.start();
			analyzerThread.join();
		} catch (InterruptedException e) {
			logger.error("Error al joinear el analyzer");
			logger.info(e.toString());
			e.printStackTrace();
		}
    }
}
