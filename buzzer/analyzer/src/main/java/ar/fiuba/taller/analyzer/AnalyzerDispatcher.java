package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;

public class AnalyzerDispatcher implements Runnable {

	BlockingQueue<Response> responseQueue;
	Response response;
	Map<String, RemoteQueue> usersMap;
	RemoteQueue remoteQueue;
	UserRegistry userRegistry;
	List<String> userFollowers;
	List<String> hashtagFollowers;
	Set<String> usersSet;
	final static Logger logger = Logger.getLogger(AnalyzerDispatcher.class);
	
	public AnalyzerDispatcher(BlockingQueue<Response> responseQueue, UserRegistry userRegistry) {
		this.responseQueue = responseQueue;
		this.userRegistry = userRegistry;
	}
	
	public void run() {
		while(true) {
			try {
				response = responseQueue.take();
				// Reviso si es un user register o un mensaje
				// Si da error o es una registracion, se lo devuelvo solamente al usuario que envio el request
				if(response.getResponse_status() == RESPONSE_STATUS.REGISTERED ||
						response.getResponse_status() == RESPONSE_STATUS.ERROR) {
						remoteQueue = getUserQueue(response.getUser());
						remoteQueue.put(response);
				}
				else {
					// Por Ok, hago anycast a los followers
					usersSet = new HashSet<String>();
					userFollowers = userRegistry.getUserFollowers(response.getUser());
					hashtagFollowers = userRegistry.getHashtagFollowers(response.getMessage());
					for(String follower : userFollowers) {
						usersSet.add(follower);
					}
					for(String follower : hashtagFollowers) {
						usersSet.add(follower);
					}
					// Fowardeo el mensaje a los followers
					Iterator<String> it = usersSet.iterator();
					while(it.hasNext()) {
						(getUserQueue(it.next())).put(response);
					}
				}
			} catch (InterruptedException e) {
				logger.error("Error al tomar respuestas de la cola responseQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar respuesta en la cola remota del usuario:" + response.getUser());
				logger.info(e.toString());
				e.printStackTrace();
			} catch (ParseException e) {
				logger.error("Error al updatear los indices");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}

	private RemoteQueue getUserQueue(String username) {
		RemoteQueue tmpQueue;
		tmpQueue = usersMap.get(username);
		if(tmpQueue == null) {
			usersMap.put(username, new RemoteQueue(username, "localhost"));
		}
		return usersMap.get(username);
	}
}
