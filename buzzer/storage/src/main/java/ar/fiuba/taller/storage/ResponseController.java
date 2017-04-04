package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

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
		while(true) {
			try {
				response = responseQueue.take();
				remoteQueue = usersMap.get(response.getUser());
				if(remoteQueue == null) {
					// Creo la cola
					usersMap.put(response.getUser(), new RemoteQueue(response.getUser(), "loclahost"));
				}
				usersMap.get(response.getUser()).put(response);
			} catch (InterruptedException e) {
				logger.error("Error al tomar respuestas de la cola responseQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar respuesta en la cola remota");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}

	}

}
