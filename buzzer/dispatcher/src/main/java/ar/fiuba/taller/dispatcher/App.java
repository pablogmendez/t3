package ar.fiuba.taller.dispatcher;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
    
	public static void main( String[] args )
    {
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
