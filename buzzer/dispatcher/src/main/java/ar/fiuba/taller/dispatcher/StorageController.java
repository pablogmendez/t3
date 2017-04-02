package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.RemoteQueue;

public class StorageController implements Runnable {
	private BlockingQueue<Command> storageCommandQueue;
	private RemoteQueue storageQueue;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public StorageController(BlockingQueue<Command> storageCommandQueue, 
			RemoteQueue storageQueue) {
		this.storageCommandQueue = storageCommandQueue;
		this.storageQueue = storageQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;
		
		logger.info("Iniciando el storage controller");
		while(true) {
			try {
				command = storageCommandQueue.take();
				logger.info("Comando recibido con los siguientes parametros: " 
						+ "\nUsuario: " + command.getUser()
						+ "\nComando: " + command.getCommand()
						+ "\nMensaje: " + command.getMessage());
				storageQueue.put(command);
				logger.error("Comando enviado al storage");
			} catch (InterruptedException e) {
				logger.error("Error al obtener el comando de la cola storageCommandQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar el comando de la cola storageQueue");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
}
