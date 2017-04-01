package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import ar.fiuba.taller.common.Response;

public class ResponseController implements Runnable {
	
	BlockingQueue<Response> responseQueue;
	final static Logger logger = Logger.getLogger(App.class);
	
	public ResponseController(BlockingQueue<Response> responseQueue) {
		this.responseQueue = responseQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		byte responseArray[] = null;
		Response response;
		logger.info("Iniciando el response controller");
		while(true) {
			// Leo el array del broker
			response = new Response();
			try {
				response.deserialize(responseArray);
				responseQueue.put(response);
			} catch (ClassNotFoundException e) {
				logger.error("Error al deserializar la respuesta");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al deserializar la respuesta");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (InterruptedException e) {
				logger.error("Error al insertar la respuesta en la cola responseQueue");
				logger.info(e.toString());
				e.printStackTrace();
			}
			
		}
	}

}
