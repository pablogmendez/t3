package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;

public class Dispatcher implements Runnable {

	Thread analyzerControllerThread;
	Thread dispatcherControllerThread;
	Thread storageControllerThread;
	Thread loggerControllerThread;
	RemoteQueue dispatcherQueue;
	RemoteQueue storageQueue;
	RemoteQueue analyzerQueue;
	RemoteQueue loggerQueue;
	BlockingQueue<Command> storageCommandQueue;
	BlockingQueue<Command> analyzerCommandQueue;
	BlockingQueue<Command> loggerCommandQueue;
	ConfigLoader configLoader;
	final static Logger logger = Logger.getLogger(Dispatcher.class);
	
	public Dispatcher() {
		configLoader = ConfigLoader.getInstance();
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
        
    	logger.info("Iniciando el dispatcher");
    
        try {
        	logger.info("Cargando la configuracion");
        	configLoader.init(Constants.CONF_FILE);
        	
	    	initDispatcher();
	    	startDispatcher();
	    	terminateDispatcher();
	    	
        } catch (InterruptedException e) {
			logger.error("Error al joinear los threads");
			logger.info(e.toString());
        	e.printStackTrace();
        } catch (IOException e) {
			logger.error("Error al cargar el archivo de configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			logger.error("Error al iniciar las colas remotas");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
	
	private void initDispatcher() throws IOException, TimeoutException {

		logger.info("Creando las colas internas");
    	analyzerCommandQueue 	= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
    	storageCommandQueue 	= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
    	loggerCommandQueue 		= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
    	
    	logger.info("Creando las conexiones a los brokers");
    	dispatcherQueue = new RemoteQueue(configLoader.getDispatcherQueueName(), configLoader.getDispatcherQueueHost());
    	dispatcherQueue.init();
    	analyzerQueue 	= new RemoteQueue(configLoader.getAnalyzerQueueName(), configLoader.getAnalyzerQueueHost());
    	analyzerQueue.init();
    	storageQueue 	= new RemoteQueue(configLoader.getStorageRequestQueueName(), configLoader.getStorageResquestQueueHost());
    	storageQueue.init();
    	loggerQueue 	= new RemoteQueue(configLoader.getAuditLoggerQueueName(), configLoader.getAuditLoggerQueueHost());
    	loggerQueue.init();
    	
    	logger.info("Creando los threads de los workers");
    	analyzerControllerThread 	= new Thread(new AnalyzerController(analyzerCommandQueue, analyzerQueue));
    	dispatcherControllerThread 	= new Thread(new DispatcherController(dispatcherQueue, storageCommandQueue, analyzerCommandQueue, loggerCommandQueue));
    	storageControllerThread 	= new Thread(new StorageController(storageCommandQueue, storageQueue));
    	loggerControllerThread 		= new Thread(new LoggerController(loggerCommandQueue, loggerQueue));
		
	}
	
	private void startDispatcher() {
    	
		logger.info("Iniciando los threads de los workers");
    	analyzerControllerThread.start();
    	dispatcherControllerThread.start();
    	storageControllerThread.start();
    	loggerControllerThread.start();
    	
	}
	
	private void terminateDispatcher() throws InterruptedException {
		
		logger.info("Joineando los threads de los workers");
		analyzerControllerThread.join();
    	dispatcherControllerThread.join();
    	storageControllerThread.join();
    	loggerControllerThread.join();		
	}
}
