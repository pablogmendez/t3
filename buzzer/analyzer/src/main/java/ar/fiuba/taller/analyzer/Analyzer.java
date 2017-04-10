package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class Analyzer implements Runnable {

	private Thread analyzerDispatcherThread;
	private Thread analyzerReciverThread;
	private Thread responseControllerThread;
	private BlockingQueue<Response> responseQueue;
	private UserRegistry userRegistry;
	private ConfigLoader configLoader;
	private RemoteQueue analyzerQueue;
	final static Logger logger = Logger.getLogger(Analyzer.class);

	
	public Analyzer() {
		configLoader = ConfigLoader.getInstance();
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
    	try {
			configLoader.init(Constants.CONF_FILE);
			// Instancio la cola
			responseQueue = new ArrayBlockingQueue<Response>(Constants.COMMAND_QUEUE_SIZE);
			logger.debug(Constants.ANALYZER_QUEUE_NAME);
			logger.debug(Constants.ANALYZER_QUEUE_HOST);
			// Creo la cola remota en donde el anayzer recibe los comandos
			analyzerQueue = new RemoteQueue(ConfigLoader.getInstance().getAnalyzerQueueName(), ConfigLoader.getInstance().getAnalyzerQueueHost());
			analyzerQueue.init();
			
			// Instancio el registry
			userRegistry = new UserRegistry();
			
			// Hago una carga inicial del user registry
			
			// Instancio los threads
			analyzerReciverThread = new Thread(new AnalyzerReciver(responseQueue, analyzerQueue, userRegistry));
			analyzerDispatcherThread = new Thread(new AnalyzerDispatcher(responseQueue, userRegistry));
			
			// Inicio los threads
			analyzerReciverThread.start();
			analyzerDispatcherThread.start();

			// Me quedo esperando los threads
			analyzerReciverThread.join();
			analyzerDispatcherThread.join();
			
		} catch (IOException e) {
			logger.error("Error al cargar el archivo de configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al dormir el thread");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			logger.error("Error al iniciar la cola remota");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

}
