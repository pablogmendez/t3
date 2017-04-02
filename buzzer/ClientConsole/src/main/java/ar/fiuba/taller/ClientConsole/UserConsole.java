package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.Response;

public class UserConsole implements Runnable {
	private String username;
	final static Logger logger = Logger.getLogger(App.class);
	
	public UserConsole(String username) {
		this.username = username;
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		BlockingQueue<Command> commandQueue;
		BlockingQueue<Response> responseQueue;
        Thread commandReaderThread;
        Thread commandControllerThread;
        Thread eventViewerThread;
        Thread responseControllerThread;
        
        logger.info("Iniciando usuario");
        logger.info("Creando cola de comandos leidos");
        commandQueue = new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        logger.info("Creando cola de respuestas");
        responseQueue = new ArrayBlockingQueue<Response>(Constants.RESPONSE_QUEUE_SIZE);
        logger.info("Creando lector de scripts");
        commandReaderThread = new Thread(new ScriptReader(commandQueue, 
        		Constants.COMMAND_SCRIPT_FOLDER + "/" + username + Constants.COMMAND_SCRIPT_EXTENSION,
        		username));
        logger.info("Creando controlador de comandos");
        commandControllerThread = new Thread(new CommandController(commandQueue));
        logger.info("Creando el controlador de respuestas");
        responseControllerThread = new Thread(new ResponseController(responseQueue));
        eventViewerThread = new Thread(new EventViewer(responseQueue, username, Constants.LOGS_DIR + "/" + 
                username + Constants.EVENT_VIEWER_FILE_EXTENSION));
        logger.info("Creando el visor de eventos");
        
        logger.info("Iniciando el lector de scripts");
        commandReaderThread.start();
        logger.info("Iniciando el controlador de comandos");
        commandControllerThread.start();
        logger.info("Iniciando el controlador de respuestas");
        responseControllerThread.start();
        logger.info("Iniciando el visor de eventos");
        eventViewerThread.start();        
        
        try {
        	logger.info("Esperando al reader");
        	commandReaderThread.join();
        	logger.info("Reader finalizado!");
        	logger.info("Esperando al controlador de comandos");
        	commandControllerThread.join();
			logger.info("controller finalizado!");
			logger.info("Esperando al controlador de respuestas");
        	responseControllerThread.join();
			logger.info("controller controlador de respuestas!");
			logger.info("Esperando al visor de eventos");
        	eventViewerThread.join();
			logger.info("visor de eventos finalizado!");
		} catch (InterruptedException e) {
			logger.error("Error al joinear los threads");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
