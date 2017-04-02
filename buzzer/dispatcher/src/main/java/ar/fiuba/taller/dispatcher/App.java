package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ar.fiuba.taller.common.*;

public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
    
	public static void main( String[] args )
    {
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
        ConfigLoader configLoader = ConfigLoader.getInstance();
        
        logger.info("Iniciando el dispatcher");
        
        try {
        	configLoader.init(Constants.CONF_FILE);
        	
        	
	        analyzerControllerThread = new Thread(new AnalyzerController(analyzerCommandQueue, analyzerQueue));
	    	dispatcherControllerThread = new Thread(new DispatcherController(dispatcherQueue, storageCommandQueue, analyzerCommandQueue, loggerCommandQueue));
	    	storageControllerThread = new Thread(new StorageController(storageCommandQueue, storageQueue));
	    	loggerControllerThread = new Thread(new LoggerController(loggerCommandQueue, loggerQueue));
	    	
	    	analyzerControllerThread.start();
	    	dispatcherControllerThread.start();
	    	storageControllerThread.start();
	    	loggerControllerThread.start();
	    	
			analyzerControllerThread.join();
	    	dispatcherControllerThread.join();
	    	storageControllerThread.join();
	    	loggerControllerThread.join();
        } catch (InterruptedException e) {
			logger.error("Error al joinear los threads");
			logger.info(e.toString());
        	e.printStackTrace();
        } catch (IOException e) {
			logger.error("Error al cargar el archivo de configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		}
    }
}
