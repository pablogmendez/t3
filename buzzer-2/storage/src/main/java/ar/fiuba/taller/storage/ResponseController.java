package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;

import java.io.IOException;
import java.util.*;

public class ResponseController implements Runnable {

	private BlockingQueue<Response> responseQueue;
	private Map<String, WritingRemoteQueue> usersMap;
	private Map<String, String> config;
	final static Logger logger = Logger.getLogger(ResponseController.class);

	public ResponseController(BlockingQueue<Response> responseQueue,
			Map<String, String> config) {
		this.responseQueue = responseQueue;
		usersMap = new HashMap<String, WritingRemoteQueue>();
		this.config = config;
	}

	public void run() {
		logger.info("Iniciando el response controller");
		Response response = new Response();
		WritingRemoteQueue currentUserRemoteQueue;

		try {
			while (!Thread.interrupted()) {
				logger.info("Esperando siguiente respuesta");
				response = responseQueue.take();
				currentUserRemoteQueue = usersMap.get(response.getUser());
				if (currentUserRemoteQueue == null) {
					// Creo la cola
					currentUserRemoteQueue = new WritingRemoteQueue(
							response.getUser(), "localhost:9092", config);
					usersMap.put(response.getUser(), currentUserRemoteQueue);
				}
				logger.info(
						"Enviando respuesta al usuario: " + response.getUser());
				logger.info("UUID: " + response.getUuid());
				logger.info("Status de la respuesta: "
						+ response.getResponse_status());
				logger.info(
						"Contenido de la respuesta: " + response.getMessage());
				logger.info("Esperando siguiente respuesta");
				try {
					usersMap.get(response.getUser()).push(response);
					logger.info("Respuesta enviada");
				} catch (IOException e) {
					logger.error(
							"No se ha podido enviar la respuesta al usuario "
									+ response.getUser());
				}
			}
		} catch (InterruptedException e) {
			logger.info("ResponseController interrumpido");
		}

	}

}
