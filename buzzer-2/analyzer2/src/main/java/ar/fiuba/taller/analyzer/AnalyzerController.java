package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class AnalyzerController {

	private Map<String, String> config;
	private ReadingRemoteQueue analyzerQueue;
	private Map<String, WritingRemoteQueue> usersMap;
	private WritingRemoteQueue remoteQueue;
	private UserRegistry userRegistry;
	private List<String> userFollowers;
	private List<String> hashtagFollowers;
	private Set<String> usersSet;
	final static Logger logger = Logger.getLogger(AnalyzerController.class);

	public AnalyzerController(Map<String, String> config,
			ReadingRemoteQueue analyzerQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.analyzerQueue = analyzerQueue;
		this.usersMap = new HashMap<String, WritingRemoteQueue>();
		this.config = config;
	}

	public void run() {
		Command command = new Command();
		Response response = new Response();
		List<byte[]> messageList = null;
		userRegistry = new UserRegistry();

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
						response = new Response();
						response.setUuid(command.getUuid());
						response.setUser(command.getUser());
						switch (command.getCommand()) {
						case PUBLISH:
							response.setResponse_status(RESPONSE_STATUS.OK);
							response.setMessage(command.getTimestamp() + "\n"
									+ command.getUser() + "\n"
									+ command.getMessage());
							sendResponse(response);
							break;
						case FOLLOW:
							userRegistry.update(command.getUser(),
									command.getMessage());
							response.setResponse_status(
									RESPONSE_STATUS.REGISTERED);
							response.setMessage("Seguidor registrado");
							sendResponse(response);
							break;
						default:
							logger.info(
									"Comando recibido invalido. Comando descartado.");
						}
					} catch (IOException | ParseException
							| ClassNotFoundException | TimeoutException e) {
						logger.error("Error al tratar el mensaje recibido: " + e);
					}
				}
			}
		} finally {
		    Iterator it = usersMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        WritingRemoteQueue userQueue = (WritingRemoteQueue) pair.getValue();
		        try {
					userQueue.close();
				} catch (IOException | TimeoutException e) {
					// Do nothing
					logger.error("Error al cerrar una response user queue: " + e);
				}
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		}
		logger.info("Analyzer reciver finalizado");
	}
	
	private void sendResponse(Response response) throws IOException, TimeoutException, ParseException {
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
	}
	
	private WritingRemoteQueue getUserQueue(String username)
			throws IOException, TimeoutException {
		WritingRemoteQueue tmpQueue;
		logger.info("Ususario a fowardear: " + username);
		tmpQueue = usersMap.get(username);

		if (tmpQueue == null) {
			tmpQueue = new WritingRemoteQueue(username, config.get(Constants.KAFKA_WRITE_PROPERTIES));
			usersMap.put(username, tmpQueue);
		}
		return usersMap.get(username);
	}

}
