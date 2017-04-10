package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

import java.io.IOException;
import java.util.*;

public class ResponseController implements Runnable {
	
	private BlockingQueue<Response> responseQueue;
	Response response;
	RemoteQueue remoteQueue;
	Map<String, RemoteQueue> usersMap;
	final static Logger logger = Logger.getLogger(ResponseController.class);
	
	public ResponseController(BlockingQueue<Response> responseQueue) {
		this.responseQueue = responseQueue;
		usersMap = new HashMap<String, RemoteQueue>();
	}

	public void run() {
		logger.info("Iniciando el response controller");
		while(true) {
			try {
				logger.info("Esperando siguiente respuesta");
				response = responseQueue.take();
				remoteQueue = usersMap.get(response.getUser());
				if(remoteQueue == null) {
					// Creo la cola
					remoteQueue = new RemoteQueue(response.getUser(), "localhost");
					remoteQueue.init();
					usersMap.put(response.getUser(), remoteQueue);
				}
				logger.info("Enviando respuesta al usuario: " + response.getUser());
				logger.info("UUID: " + response.getUuid());
				logger.info("Status de la respuesta: " + response.getResponse_status());
				logger.info("Contenido de la respuesta: " + response.getMessage());
				logger.info("Esperando siguiente respuesta");
				usersMap.get(response.getUser()).put(response);
				logger.info("Respuesta enviada");
			} catch (InterruptedException e) {
				logger.error("Error al tomar respuestas de la cola responseQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar respuesta en la cola remota");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (TimeoutException e) {
				logger.error("Error al iniciar la cola remota del usuario");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}

	}

}

