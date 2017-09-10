package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class CommandController implements Runnable {
	private BlockingQueue<Command> commandQueue;
	private WritingRemoteQueue dispatcherQueue;
	private int maxlengthMsg;
	private Timestamp timestamp;

	final static Logger logger = Logger.getLogger(CommandController.class);

	public CommandController(BlockingQueue<Command> commandQueue,
			WritingRemoteQueue dispatcherQueue, int maxlengthMsg) {
		this.commandQueue = commandQueue;
		this.dispatcherQueue = dispatcherQueue;
		this.maxlengthMsg = maxlengthMsg;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;

		logger.debug("Iniciando el command controller");
		try {
			while (!Thread.interrupted()) {
				try {
					logger.debug("Obteniendo comando de la cola");
					command = commandQueue.take();
					logger.debug("Comando obtenido");
					logger.debug("Comando recibido: " + command.getCommand());
					logger.debug("Mensaje: " + command.getMessage());
					if (command.getMessage().length() <= maxlengthMsg) {
						logger.debug("Generando UUID");
						command.setUuid(UUID.randomUUID());
						logger.debug("Generando timestamp");
						timestamp = new Timestamp(System.currentTimeMillis());
						command.setTimestamp(Constants.SDF.format(timestamp));
						logger.debug("UUID generado: " + command.getUuid());
						logger.debug("Enviando el mensaje al dispatcher");
						dispatcherQueue.push(command);
						logger.debug("Mensaje enviado");
						System.out.printf(
								"Comando enviado - UUID: {%s} - Comando: {%s} - Usuario: {%s} - Mensaje: {%s} - Timestamp: {%s}",
								command.getUuid().toString(),
								command.getCommand().toString(),
								command.getUser(), command.getMessage(),
								command.getTimestamp());
					} else {
						logger.error(
								"El mensaje contiene mas de 141 caracteres");
					}
				} catch (IOException e) {
					logger.error("Error al enviar el mensaje al dispatcher");
					logger.debug(e);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Error al sacar un comando de la cola commandQueue");
			logger.debug(e);
		}
		logger.debug("Command controller terminado");
	}
}