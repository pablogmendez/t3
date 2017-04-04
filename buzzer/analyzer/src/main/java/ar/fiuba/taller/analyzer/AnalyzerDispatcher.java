package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class AnalyzerDispatcher implements Runnable {

	BlockingQueue<Response> responseQueue;
	Response response;
	Map<String, RemoteQueue> usersMap;
	RemoteQueue remoteQueue;
	UserRegistry userRegistry;
	final static Logger logger = Logger.getLogger(AnalyzerDispatcher.class);
	
	public AnalyzerDispatcher(BlockingQueue<Response> responseQueue, UserRegistry userRegistry) {
		this.responseQueue = responseQueue;
		this.userRegistry = userRegistry;
	}
	
	public void run() {
		while(true) {
			try {
				response = responseQueue.take();
				// Me fijo si tengo al usuario en el map
				remoteQueue = usersMap.get(response.getUser());
				if(remoteQueue == null) {
					// Si  no esta, le pregunto al registry si el usuario esta registrado
					if(userRegistry.validUser(response.getUser())) {
						// si el usuario esta registrado, creo la cola
						usersMap.put(response.getUser(), new RemoteQueue(response.getUser(), "loclahost"));
						remoteQueue = usersMap.get(response.getUser());
					}
				}
				if(remoteQueue != null) {
					remoteQueue.put(response);
				}

			} catch (InterruptedException e) {
				logger.error("Error al tomar respuestas de la cola responseQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar respuesta en la cola remota del usuario:" + response.getUser());
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}

}
