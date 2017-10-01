package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;

public class AnalyzerMain {
	final static Logger logger = Logger.getLogger(AnalyzerMain.class);

	public static void main(String[] args) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		PropertyConfigurator.configure(Constants.LOGGER_CONF);
		ConfigLoader configLoader = null;

		logger.info("Iniciando el analyzer");

		try {
			configLoader = new ConfigLoader(Constants.CONF_FILE);
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			System.exit(Constants.EXIT_FAILURE);
		}

		ReadingRemoteQueue analyzerQueue = null;
		try {
			analyzerQueue = new ReadingRemoteQueue(
					configLoader.getProperties().get(Constants.ANALYZER_QUEUE_NAME),
					configLoader.getProperties().get(Constants.KAFKA_READ_PROPERTIES));
		} catch (IOException e1) {
			logger.error("No se ha podido inicializar la cola de kafka: " + e1);
			System.exit(Constants.EXIT_FAILURE);
		}

		AnalyzerController analyzerController = new AnalyzerController(
				configLoader.getProperties(), analyzerQueue);
		analyzerController.run();
		analyzerQueue.shutDown();
		try {
			analyzerQueue.close();
		} catch (IOException | TimeoutException e) {
			// Do nothing
			logger.error("No se ha podido cerrar la cola del analyzer: " + e);
		}
	}
}
