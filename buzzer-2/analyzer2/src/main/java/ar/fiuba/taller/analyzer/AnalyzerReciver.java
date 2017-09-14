package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.ReadingRemoteQueueException;
import ar.fiuba.taller.common.Response;

public class AnalyzerReciver implements Runnable {

	private Map<String, String> config;
	private ReadingRemoteQueue analyzerQueue;
	final static Logger logger = Logger.getLogger(AnalyzerReciver.class);

	public AnalyzerReciver(Map<String, String> config,
			ReadingRemoteQueue analyzerQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.analyzerQueue = analyzerQueue;
		this.config = config;
	}

	public void run() {
		Command command = new Command();
		Response response = new Response();
		List<byte[]> messageList = null;
		BlockingQueue<Response> responseQueue = new ArrayBlockingQueue<Response>(
				Constants.RESPONSE_QUEUE_SIZE);
		UserRegistry userRegistry = new UserRegistry();
		Thread analyzerDispatcherThread = new Thread(
				new AnalyzerDispatcher(responseQueue, userRegistry, config));

		logger.info("Iniciando el analyzer reciver");
		analyzerDispatcherThread.start();

		try {
			while (!Thread.interrupted()) {
				messageList = analyzerQueue.pop();
				for (byte[] message : messageList) {
					try {
						command.deserialize(message);
						logger.info(
								"Comando recibido con los siguientes parametros: "
										+ "\nUUID: " + command.getUuid()
										+ "\nUsuario: " + command.getUser()
										+ "\nComando: " + command.getCommand()
										+ "\nMensaje: " + command.getMessage());
						switch (command.getCommand()) {
						case PUBLISH:
							response = new Response();
							response.setUuid(command.getUuid());
							response.setUser(command.getUser());
							response.setResponse_status(RESPONSE_STATUS.OK);
							response.setMessage(command.getTimestamp() + "\n"
									+ command.getUser() + "\n"
									+ command.getMessage());
							responseQueue.put(response);
							break;
						case FOLLOW:
							userRegistry.update(command.getUser(),
									command.getMessage());
							response = new Response();
							response.setUuid(command.getUuid());
							response.setUser(command.getUser());
							response.setResponse_status(
									RESPONSE_STATUS.REGISTERED);
							response.setMessage("Seguidor registrado");
							responseQueue.put(response);
							break;
						default:
							logger.info(
									"Comando recibido invalido. Comando descartado.");
						}
					} catch (IOException | ParseException
							| ClassNotFoundException e) {
						logger.error("Error al tratar el mensaje recibido.");
					}
				}
			}
		} catch (ReadingRemoteQueueException | InterruptedException e) {
			analyzerDispatcherThread.interrupt();
			try {
				analyzerDispatcherThread.join();
			} catch (InterruptedException e1) {
				// Do nothing
			}
		}
		logger.info("Analyzer reciver finalizado");
	}

}
