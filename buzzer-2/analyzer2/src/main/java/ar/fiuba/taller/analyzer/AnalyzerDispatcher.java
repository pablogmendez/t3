package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;

public class AnalyzerDispatcher implements Runnable {

	private BlockingQueue<Response> responseQueue;
	private Response response;
	private Map<String, WritingRemoteQueue> usersMap;
	private WritingRemoteQueue remoteQueue;
	private UserRegistry userRegistry;
	private List<String> userFollowers;
	private List<String> hashtagFollowers;
	private Set<String> usersSet;
	private Map<String, String> config;
	final static Logger logger = Logger.getLogger(AnalyzerDispatcher.class);

	public AnalyzerDispatcher(BlockingQueue<Response> responseQueue,
			UserRegistry userRegistry, Map<String, String> config) {
		this.responseQueue = responseQueue;
		this.userRegistry = userRegistry;
		usersMap = new HashMap<String, WritingRemoteQueue>();
		this.config = config;
	}

	public void run() {
		logger.info("Iniciando el Analyzer dispatcher");
		try {
			while (!Thread.interrupted()) {
				try {
					response = responseQueue.take();
					logger.info("Nueva respuesta para enviar");
					logger.info("Nueva respuesta para enviar");
					logger.info("UUID: " + response.getUuid());
					logger.info("User: " + response.getUser());
					logger.info("Status: " + response.getResponse_status());
					logger.info("Message: " + response.getMessage());
					// Reviso si es un user register o un mensaje
					// Si da error o es una registracion, se lo devuelvo
					// solamente
					// al usuario que envio el request
					if (response
							.getResponse_status() == RESPONSE_STATUS.REGISTERED
							|| response
									.getResponse_status() == RESPONSE_STATUS.ERROR) {
						logger.info("Enviando respuesta");
						remoteQueue = getUserQueue(response.getUser());
						remoteQueue.push(response);
					} else {
						// Por Ok, hago anycast a los followers
						logger.info("Anycast a los followers");
						usersSet = new HashSet<String>();
						userFollowers = userRegistry
								.getUserFollowers(response.getUser());
						hashtagFollowers = userRegistry
								.getHashtagFollowers(response.getMessage());
						for (String follower : userFollowers) {
							usersSet.add(follower);
						}
						for (String follower : hashtagFollowers) {
							usersSet.add(follower);
						}
						// Fowardeo el mensaje a los followers
						Iterator<String> it = usersSet.iterator();
						while (it.hasNext()) {
							(getUserQueue(it.next())).push(response);
						}
					}
				} catch (IOException | ParseException | TimeoutException e) {
					logger.error(
							"Error al insertar respuesta en la cola remota del "
									+ "usuario:" + response.getUser());
					logger.error(e);
				}
			}
		} catch (InterruptedException e) {
			logger.info("Analyzer dispatcher interrumpido");
		}
		logger.info("Analyzer dispatcher finalizado");
	}

	private WritingRemoteQueue getUserQueue(String username)
			throws IOException, TimeoutException {
		WritingRemoteQueue tmpQueue;
		logger.info("Ususario a fowardear: " + username);
		tmpQueue = usersMap.get(username);

		if (tmpQueue == null) {
			tmpQueue = new WritingRemoteQueue(username, "localhost:9092",
					config);
			usersMap.put(username, tmpQueue);
		}
		return usersMap.get(username);
	}
}
