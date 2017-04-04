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

public class RemoveController implements Runnable {
	private BlockingQueue<Command> removeQueue;
	private BlockingQueue<Response> responseQueue;
	private Storage storage;
	private Command command;
	private Response response;
	final static Logger logger = Logger.getLogger(StorageController.class);
	
	public RemoveController(BlockingQueue<Command> removeQueue, BlockingQueue<Response> responseQueue, Storage storage) {
		super();
		this.removeQueue 	= removeQueue;
		this.storage		= storage;
		this.responseQueue	= responseQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		String error_message = "Error al eliminar el mensaje";
		while(true) {
			try {
				response = new Response();
				response.setUuid(UUID.randomUUID());
				response.setUser(command.getUser());
				command = removeQueue.take();
				storage.delete(command);
				response.setMessage("Borrado exitoso");
				response.setResponse_status(RESPONSE_STATUS.OK);
			} catch (InterruptedException e) {
				response.setResponse_status(RESPONSE_STATUS.ERROR);
				response.setMessage(error_message);
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				response.setResponse_status(RESPONSE_STATUS.ERROR);
				response.setMessage(error_message);
				logger.error("Error borrar el mensaje");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (ParseException e) {
				response.setResponse_status(RESPONSE_STATUS.ERROR);
				response.setMessage(error_message);
				logger.error("Error borrar el mensaje");
				logger.info(e.toString());
				e.printStackTrace();
			} finally {
				try {
					responseQueue.put(response);
				} catch (InterruptedException e) {
					logger.error("No se pudo enviar la respuesta");
					logger.info(e.toString());
					e.printStackTrace();
				}
			}
		}
	}
}
