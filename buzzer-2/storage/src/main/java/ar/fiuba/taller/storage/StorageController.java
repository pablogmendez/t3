package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;

public class StorageController {
	private Map<String, String> config;
	private Storage storage;
	private ReadingRemoteQueue storageQueue;
	private Map<String, WritingRemoteQueue> usersMap;
	final static Logger logger = Logger.getLogger(StorageController.class);

	public StorageController(Map<String, String> config,
			ReadingRemoteQueue storageQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.config = config;
		storage = new Storage(
				Integer.parseInt(config.get(Constants.SHARDING_FACTOR)),
				Integer.parseInt(config.get(Constants.QUERY_COUNT_SHOW_POSTS)),
				Integer.parseInt(config.get(Constants.TT_COUNT_SHOW)));
		this.storageQueue = storageQueue;
		usersMap = new HashMap<String, WritingRemoteQueue>();
	}

	public void run() {
		Command command;
		List<byte[]> messageList = null;

		logger.info("Consumiendo de la storageQueue");
		try {
			while (!Thread.interrupted()) {
				messageList = storageQueue.pop();
				for (byte[] message : messageList) {
					try {
						command = new Command();
						command.deserialize(message);
						analyzeCommand(command);

					} catch (ClassNotFoundException | IOException e) {
						logger.error("No se ha podido deserializar el mensaje");
					}
				}
			}
		} catch (InterruptedException e) {
			// Do nothing
			logger.error("Error al analizar comando: " + e);
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
		logger.info("Storgae Controller terminado");
	}

	private void analyzeCommand(Command command) throws InterruptedException {
		String error_message = "Error al crear el mensaje";
		Response response = new Response();
		
		logger.info("Comando recibido con los siguientes parametros: "
				+ "\nUUID: " + command.getUuid() + "\nUsuario: "
				+ command.getUser() + "\nComando: " + command.getCommand()
				+ "\nMensaje: " + command.getMessage());

		response.setUuid(UUID.randomUUID());
		response.setUser(command.getUser());
		try {
			switch (command.getCommand()) {
			case PUBLISH:
				logger.info(
						"Comando recibido: PUBLISH. Insertando en la cola de creacion.");
				storage.saveMessage(command);
				response.setMessage("Creacion exitosa");
				response.setResponse_status(RESPONSE_STATUS.OK);
				break;
			case QUERY:
				logger.info(
						"Comando recibido: QUERY. Insertando en la cola de consultas.");
				response.setMessage(storage.query(command));
				logger.debug(response.getMessage());
				response.setResponse_status(RESPONSE_STATUS.OK);
				break;
			case DELETE:
				logger.info(
						"Comando recibido: DELETE. Insertando en la cola de borrado.");
				storage.delete(command);
				response.setMessage("Borrado exitoso");
				response.setResponse_status(RESPONSE_STATUS.OK);
				break;
			default:
				logger.info("Comando recibido invalido. Comando descartado.");
			}
		} catch (IOException e) {
			response.setResponse_status(RESPONSE_STATUS.ERROR);
			response.setMessage(error_message);
			logger.error(e);
		} catch (ParseException e) {
			response.setResponse_status(RESPONSE_STATUS.ERROR);
			response.setMessage(error_message);
			logger.error(e);
		} finally {
			if (response != null) {
				sendResponse(response);
				response = null;
			}
		}
	}
	
	private void sendResponse(Response response) {
		logger.info("Siguiente respuesta");
		WritingRemoteQueue currentUserRemoteQueue;
		currentUserRemoteQueue = usersMap.get(response.getUser());
		if (currentUserRemoteQueue == null) {
			// Creo la cola
			try {
				currentUserRemoteQueue = new WritingRemoteQueue(
						response.getUser(), config.get(Constants.KAFKA_WRITE_PROPERTIES));
			} catch (IOException e) {
				logger.error("No se han podido crear las colas de kafka: " + e);
				System.exit(1);
			}
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
			logger.info("Respuesta enviada: " + response.getUser() + " : " + response.getMessage()
			+ " : " + response.getResponse_status() + " : " + response.getUuid());
		} catch (IOException e) {
			logger.error(
					"No se ha podido enviar la respuesta al usuario "
							+ response.getUser());
		}
	}
}
