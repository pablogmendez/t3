package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;
import ar.fiuba.taller.common.Response;

public class QueryController implements Runnable {
	private BlockingQueue<Command> queryQueue;
	private BlockingQueue<Response> responseQueue;
	private Storage storage;
	private Command command;
	private Response response;
	final static Logger logger = Logger.getLogger(QueryController.class);
	
	public QueryController(BlockingQueue<Command> queryQueue, BlockingQueue<Response> responseQueue, Storage storage) {
		super();
		this.queryQueue		= queryQueue;
		this.responseQueue	= responseQueue;
		this.storage		= storage;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		String queryResult;
		// Este mensaje deberia ser configurable
		String error_message = "Error al consultar";
		logger.info("Iniciando el query controller");
		while(true) {
			try {
				command = queryQueue.take();
				response = new Response();
				response.setUuid(UUID.randomUUID());
				response.setUser(command.getUser());
				response.setMessage(storage.query(command));
				logger.debug(response.getMessage());
				response.setResponse_status(RESPONSE_STATUS.OK);
			} catch (InterruptedException e) {
				response.setResponse_status(RESPONSE_STATUS.ERROR);
				response.setMessage(error_message);
				logger.error("Error al sacar comando de la cola removeQueue");
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
