package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class LoggerController implements Runnable {
	private BlockingQueue<Command> loggerCommandQueue;
	private WritingRemoteQueue loggerQueue;
	final static Logger logger = Logger.getLogger(LoggerController.class);

	public LoggerController(BlockingQueue<Command> loggerCommandQueue,
			Map<String, String> config) {
		this.loggerCommandQueue = loggerCommandQueue;
		loggerQueue = new WritingRemoteQueue(config.get(Constants.AUDIT_LOGGER_QUEUE_NAME),
				config.get(Constants.AUDIT_LOGGER_QUEUE_HOST), config);
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;

		logger.info("Iniciando el logger controller");
		try {
			while (!Thread.interrupted()) {
				try {
					command = loggerCommandQueue.take();
					logger.info("Comando recibido con los siguientes parametros: "
							+ "\nUsuario: " + command.getUser() + "\nComando: "
							+ command.getCommand() + "\nMensaje: "
							+ command.getMessage());
					loggerQueue.push(command);
					logger.info("Comando enviado al logger");
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (InterruptedException e) {
			logger.info("Logger controller interrumpido");
		}
		logger.info("Logger controller terminado");
	}
}

