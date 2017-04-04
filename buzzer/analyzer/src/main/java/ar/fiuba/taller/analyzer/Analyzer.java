package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.Response;

public class Analyzer implements Runnable {

	Thread analyzerDispatcherThread;
	Thread analyzerReciverThread;
	Thread responseControllerThread;
	BlockingQueue<Response> responseQueue;
	UserRegistry userRegistry;
	ConfigLoader configLoader;
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
			
			// Instancio el registry
			userRegistry = new UserRegistry();
			
			// Hago una carga inicial del user registry
			
			// Instancio los threads
			analyzerReciverThread = new Thread(new AnalyzerReciver(responseQueue));
			analyzerDispatcherThread = new Thread(new AnalyzerDispatcher(responseQueue, userRegistry));
			
			// Inicio los threads
			analyzerDispatcherThread.start();
			analyzerDispatcherThread.start();

			// Duermo y recargo la base de registers
			while(true) {
				Thread.sleep(10000);
				userRegistry.reloadDataBase();
			}
			
		} catch (IOException e) {
			logger.error("Error al cargar el archivo de configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al dormir el thread");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

}
