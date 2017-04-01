package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;

public class UserConsole implements Runnable {
	private String username;
	final static Logger logger = Logger.getLogger(App.class);
	
	public UserConsole(String username) {
		this.username = username;
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		BlockingQueue<Command> commandQueue;
        Thread commandReaderThread;
        Thread commandControllerThread;
        
        logger.info("Iniciando usuario");
        logger.info("Creando cola de comandos leidos");
        commandQueue = new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        logger.info("Creando lector de scripts");
        commandReaderThread = new Thread(new ScriptReader(commandQueue, 
        		Constants.COMMAND_SCRIPT_FOLDER + username + Constants.COMMAND_SCRIPT_EXTENSION,
        		username));
        logger.info("Creando controlador de comandos");
        commandControllerThread = new Thread(new CommandController(commandQueue));
        
        try {
        	logger.info("Esperando al reader y al controler");
        	commandReaderThread.join();
        	logger.info("Reader finalizado!");
			commandControllerThread.join();
			logger.info("controller finalizado!");
		} catch (InterruptedException e) {
			logger.error("Error al joinear los threads de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
