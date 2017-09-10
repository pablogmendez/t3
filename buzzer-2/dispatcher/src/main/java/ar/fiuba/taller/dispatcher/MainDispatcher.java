package ar.fiuba.taller.dispatcher;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;

public class MainDispatcher {
	final static Logger logger = Logger.getLogger(MainDispatcher.class);

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
		
		final ReadingRemoteQueue dispatcherQueue = new ReadingRemoteQueue(
				configLoader.getProperties()
						.get(Constants.DISPATCHER_QUEUE_NAME),
				configLoader.getProperties()
						.get(Constants.DISPATCHER_QUEUE_HOST),
				configLoader.getProperties());
		
		final Thread dispatcherThread = new Thread(
				new DispatcherController(configLoader.getProperties(), dispatcherQueue));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				dispatcherQueue.shutDown();
				dispatcherThread.interrupt();
				try {
					dispatcherThread.join(Constants.STORAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}
		});

		dispatcherThread.start();
	}
}
