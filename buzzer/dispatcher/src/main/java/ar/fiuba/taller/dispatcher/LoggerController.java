package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.RemoteQueue;

public class LoggerController implements Runnable {

	private BlockingQueue<Command> loggerCommandQueue;
	private RemoteQueue loggerQueue;
	
	final static Logger logger = Logger.getLogger(LoggerController.class);
	
	public LoggerController(BlockingQueue<Command> loggerCommandQueue, 
			RemoteQueue loggerQueue) {
		this.loggerCommandQueue = loggerCommandQueue;
		this.loggerQueue = loggerQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;
		
		logger.info("Iniciando el logger controller");
		while(true) {
			try {
				command = loggerCommandQueue.take();
				logger.info("Comando recibido con los siguientes parametros: " 
						+ "\nUsuario: " + command.getUser()
						+ "\nComando: " + command.getCommand()
						+ "\nMensaje: " + command.getMessage());
				loggerQueue.put(command);
				logger.error("Comando enviado al logger");
			} catch (InterruptedException e) {
				logger.error("Error al obtener el comando de la cola loggerCommandQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar el comando de la cola loggerQueue");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
}
