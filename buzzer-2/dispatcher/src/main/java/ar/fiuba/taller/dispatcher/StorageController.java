package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class StorageController implements Runnable {
	private BlockingQueue<Command> storageCommandQueue;
	private WritingRemoteQueue storageQueue;

	final static Logger logger = Logger.getLogger(StorageController.class);

	public StorageController(BlockingQueue<Command> storageCommandQueue,
			Map<String, String> config) {
		this.storageCommandQueue = storageCommandQueue;
		this.storageQueue = new WritingRemoteQueue(
				config.get(Constants.STORAGE_QUEUE_NAME),
				config.get(Constants.STORAGE_QUEUE_HOST), config);
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;

		logger.info("Iniciando el storage controller");
		try {
			while (!Thread.interrupted()) {
				try {
					command = storageCommandQueue.take();
					logger.info("Comando recibido con los siguientes parametros: "
							+ "\nUsuario: " + command.getUser() + "\nComando: "
							+ command.getCommand() + "\nMensaje: "
							+ command.getMessage());
					storageQueue.push(command);
					logger.info("Comando enviado al storage");
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (InterruptedException e) {
			logger.info("Storage controller interrumpido");
		}
	}
}
