package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;

public class CreateController implements Runnable {
	private BlockingQueue<Command> createQueue;
	private BlockingQueue<Response> responseQueue;
	private Command command;
	private Storage storage;
	private Response response;
	final static Logger logger = Logger.getLogger(CreateController.class);

	public CreateController(BlockingQueue<Command> createQueue,
			BlockingQueue<Response> responseQueue, int shardingFactor,
			Storage storage) {
		super();
		this.createQueue = createQueue;
		this.responseQueue = responseQueue;
		this.storage = storage;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Iniciando el create controller");

		try {
			while (!Thread.interrupted()) {
				String error_message = "Error al crear el mensaje";
				try {
					command = createQueue.take();
					response = new Response();
					response.setUuid(UUID.randomUUID());
					response.setUser(command.getUser());
					storage.saveMessage(command);
					response.setMessage("Creacion exitosa");
					response.setResponse_status(RESPONSE_STATUS.OK);
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
						responseQueue.put(response);
						response = null;
					}
				}
			}
		} catch (InterruptedException e) {
			logger.info("Create controller interrumpido");
		}
	}
}
