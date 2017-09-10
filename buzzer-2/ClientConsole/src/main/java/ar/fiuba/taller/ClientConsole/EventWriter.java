package ar.fiuba.taller.ClientConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Response;

public class EventWriter implements Runnable {
	BlockingQueue<Response> responseQueue;
	String username;
	String eventFile;
	final static Logger logger = Logger.getLogger(EventWriter.class);

	public EventWriter(BlockingQueue<Response> responseQueue, String username,
			String eventFile) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.responseQueue = responseQueue;
		this.username = username;
		this.eventFile = eventFile;
	}

	public void run() {
		Response response = null;
		FileWriter responseFile = null;
		PrintWriter pw;

		logger.debug("Iniciando el event viewer");
		try {
			while (!Thread.interrupted()) {
				logger.debug("Esperando respuesta");
				response = responseQueue.take();
				try {
					pw = new PrintWriter(new BufferedWriter(
							new FileWriter(eventFile, true)));
					logger.debug("Respuesta obtenida");
					pw.printf(
							"Evento recibido - UUID: {%s} - Status: {%s} - Mensaje: {%s}%n-----------------------------------------------------%n",
							response.getUuid(), response.getResponse_status(),
							response.getMessage());
					pw.close();
				} catch (IOException e) {
					logger.error("No se ha podido escribir la respuesta");
					logger.debug(e);
				}
			}
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			try {
				if (null != responseFile)
					responseFile.close();
			} catch (Exception e2) {
				logger.error("Error al cerrar el archivo " + eventFile);
				logger.debug(e2);
			}
		}

	}

}
