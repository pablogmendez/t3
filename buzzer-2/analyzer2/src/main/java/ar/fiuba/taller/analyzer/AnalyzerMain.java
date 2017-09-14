package ar.fiuba.taller.analyzer;

import java.io.IOException;

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

		final ReadingRemoteQueue analyzerQueue = new ReadingRemoteQueue(
				configLoader.getProperties().get(Constants.ANALYZER_QUEUE_NAME),
				configLoader.getProperties().get(Constants.ANALYZER_QUEUE_HOST),
				configLoader.getProperties());

		final Thread analyzerReciverThread = new Thread(new AnalyzerReciver(
				configLoader.getProperties(), analyzerQueue));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				analyzerQueue.shutDown();
				analyzerReciverThread.interrupt();
				try {
					analyzerReciverThread
							.join(Constants.STORAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e) {
					// Do nothing
				} finally {
					logger.info("Analyzer terminado");
				}
			}
		});

		analyzerReciverThread.start();
	}
}
