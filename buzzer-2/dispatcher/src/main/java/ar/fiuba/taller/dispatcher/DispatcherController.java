package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class DispatcherController {

	private ReadingRemoteQueue dispatcherQueue;
	private WritingRemoteQueue storageQueue;
	private WritingRemoteQueue analyzerQueue;
	private WritingRemoteQueue loggerQueue;
	
	final static Logger logger = Logger.getLogger(DispatcherController.class);

	public DispatcherController(Map<String, String> config,
			ReadingRemoteQueue dispatcherQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.dispatcherQueue = dispatcherQueue;
		try {
			this.storageQueue = new WritingRemoteQueue(
					config.get(Constants.STORAGE_QUEUE_NAME),
					config.get(Constants.KAFKA_WRITE_PROPERTIES));
			this.loggerQueue = new WritingRemoteQueue(
					config.get(Constants.AUDIT_LOGGER_QUEUE_NAME),
					config.get(Constants.KAFKA_WRITE_PROPERTIES));
			this.analyzerQueue = new WritingRemoteQueue(
					config.get(Constants.ANALYZER_QUEUE_NAME),
					config.get(Constants.KAFKA_WRITE_PROPERTIES));
		} catch (IOException e) {
			logger.error("No se han podido inicializar las colas de kafka: " + e);
			System.exit(1);
		}
	}

	public void run() {
		Command command = new Command();
		List<byte[]> messageList = null;


		logger.info("Iniciando el dispatcher controller");
		try {
			while (!Thread.interrupted()) {
				messageList = dispatcherQueue.pop();
				Iterator<byte[]> it = messageList.iterator();
				while (it.hasNext()) {
					try {
						command = new Command();
						command.deserialize(it.next());
						logger.info(
								"Comando recibido con los siguientes parametros: "
										+ "\nUsuario: " + command.getUser()
										+ "\nComando: " + command.getCommand()
										+ "\nMensaje: " + command.getMessage());
						switch (command.getCommand()) {
						case PUBLISH:
							storageQueue.push(command);
							analyzerQueue.push(command);
							loggerQueue.push(command);
							logger.info("Comando enviado al publish: "
									+ "\nUsuario: " + command.getUser()
									+ "\nComando: " + command.getCommand()
									+ "\nMensaje: " + command.getMessage());
							break;
						case QUERY:
							storageQueue.push(command);
							loggerQueue.push(command);
							logger.info("Comando enviado al query: "
									+ "\nUsuario: " + command.getUser()
									+ "\nComando: " + command.getCommand()
									+ "\nMensaje: " + command.getMessage());
							break;
						case DELETE:
							logger.info("Comando enviado al delete: "
									+ "\nUsuario: " + command.getUser()
									+ "\nComando: " + command.getCommand()
									+ "\nMensaje: " + command.getMessage());
							storageQueue.push(command);
							loggerQueue.push(command);
							break;
						case FOLLOW:
							logger.info("Comando enviado al follow: "
									+ "\nUsuario: " + command.getUser()
									+ "\nComando: " + command.getCommand()
									+ "\nMensaje: " + command.getMessage());
							analyzerQueue.push(command);
							loggerQueue.push(command);
							break;
						default:
							logger.error("Comando invalido");
							break;
						}
					} catch (ClassNotFoundException | IOException e) {
						logger.error("No se ha podido deserializar el mensaje: " + e);
					}
				}
			}
		} finally {
			try {
				storageQueue.close();
				dispatcherQueue.close();
				analyzerQueue.close();
			} catch (IOException | TimeoutException e) {
				// Do nothing
				logger.error("No se ha podido cerrar alguna de las colas");
				logger.debug(e);
			}
		}
		logger.info("Dispatcher controller terminado");
	}
}