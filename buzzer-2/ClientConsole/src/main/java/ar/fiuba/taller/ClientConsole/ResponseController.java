package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;

public class ResponseController implements Runnable {

	private BlockingQueue<Response> responseQueue;
	private ReadingRemoteQueue remoteResponseQueue;
	final static Logger logger = Logger.getLogger(ResponseController.class);

	public ResponseController(BlockingQueue<Response> responseQueue,
			ReadingRemoteQueue remoteResponseQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.responseQueue = responseQueue;
		this.remoteResponseQueue = remoteResponseQueue;
	}

	public void run() {
		Response response = new Response();
		List<byte[]> messageList = null;

		logger.debug("Iniciando el response controller");
		try {
			while (!Thread.interrupted()) {
				messageList = remoteResponseQueue.pop();
				for (byte[] message : messageList) {
					try {
						response.deserialize(message);
						responseQueue.put(response);
					} catch (IOException | ClassNotFoundException e) {
						logger.error(
								"No se ha podido obtener el mensaje de la cola del usuario");
						logger.debug(e);
					}
				}
			}
		} catch (InterruptedException e) {
			// Do nothing
		}
		logger.debug("Iniciando el response controller");
	}
}
