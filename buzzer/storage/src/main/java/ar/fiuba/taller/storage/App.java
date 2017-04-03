package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;


public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		try {
			ConfigLoader.getInstance().init(Constants.CONF_FILE);
			RemoteQueue storageQueue = new RemoteQueue(ConfigLoader.getInstance().getStorageRequestQueueName(),
					ConfigLoader.getInstance().getStorageResquestQueueHost());
			logger.info("Disparando el storage controller");
			Thread storageControllerThread = new Thread(new StorageController(storageQueue));
			storageControllerThread.start();
			storageControllerThread.join();
			storageQueue.close();
		} catch (InterruptedException e) {
			logger.error("Error al joinear el storage controller");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			logger.error("Error al cerrar la cola storageQueue");
			logger.info(e.toString());
			e.printStackTrace();
		}
    }
}
