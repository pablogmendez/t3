package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;

public class CommandController implements Runnable {
	private BlockingQueue<Command> commandQueue;
	private RemoteQueue dispatcherQueue;
	Timestamp timestamp;

	final static Logger logger = Logger.getLogger(CommandController.class);

	public CommandController(BlockingQueue<Command> commandQueue,
			RemoteQueue dispatcherQueue) {
		this.commandQueue = commandQueue;
		this.dispatcherQueue = dispatcherQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;

		logger.info("Iniciando el command controller");

		try {
			logger.info("Obteniendo comando de la cola");
			command = commandQueue.take();
			logger.info("Comando obtenido");
			logger.info("Comando recibido: " + command.getCommand());
			logger.info("Mensaje: " + command.getMessage());
			if (command.getMessage().length() <= 141) {
				logger.info("Generando UUID");
				command.setUuid(UUID.randomUUID());
				logger.info("Generando timestamp");
				timestamp = new Timestamp(System.currentTimeMillis());
				command.setTimestamp(Constants.SDF.format(timestamp));
				logger.info("UUID generado: " + command.getUuid());
				logger.info("Enviando el mensaje al dispatcher");
				dispatcherQueue.put(command);
				logger.info("Mensaje enviado");
			} else {
				logger.error("El mensaje contiene mas de 141 caracteres");
			}
		} catch (InterruptedException e) {
			logger.error("Error al sacar un comando de la cola commandQueue");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al enviar el mensaje al dispatcher");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
