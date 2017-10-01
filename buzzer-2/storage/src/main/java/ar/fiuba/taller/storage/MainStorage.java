package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;

public class MainStorage {
	final static Logger logger = Logger.getLogger(MainStorage.class);

	public static void main(String[] args) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		PropertyConfigurator.configure(Constants.LOGGER_CONF);
		ConfigLoader configLoader = null;

		try {
			configLoader = new ConfigLoader(Constants.CONF_FILE);
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			System.exit(Constants.EXIT_FAILURE);
		}
		ReadingRemoteQueue storageQueue = null;
		try {
			storageQueue = new ReadingRemoteQueue(
					configLoader.getProperties().get(Constants.STORAGE_QUEUE_NAME),
					configLoader.getProperties().get(Constants.KAFKA_READ_PROPERTIES));
		} catch (IOException e1) {
			logger.error("No se han podido inicializar las colas de kafka: " + e1);
			System.exit(1);
		}

		StorageController storageController = new StorageController(
				configLoader.getProperties(), storageQueue);
		
		storageController.run();
		storageQueue.shutDown();
		try {
			storageQueue.close();
		} catch (IOException | TimeoutException e) {
			// Do nothing
			logger.error("No se ha podido cerrar la cola de entrada al storage: " + e);
		}
	}
}
