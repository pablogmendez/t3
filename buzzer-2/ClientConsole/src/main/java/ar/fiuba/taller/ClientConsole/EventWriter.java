package ar.fiuba.taller.ClientConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;

public class EventWriter implements Runnable {
	private ReadingRemoteQueue remoteResponseQueue;
	private String eventFile;
	final static Logger logger = Logger.getLogger(EventWriter.class);

	public EventWriter(
			String eventFile, ReadingRemoteQueue remoteResponseQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.remoteResponseQueue = remoteResponseQueue;
		this.eventFile = eventFile;
	}

	@SuppressWarnings("null")
	public void run() {
		Response response = new Response();
		FileWriter responseFile = null;
		PrintWriter pw;
		List<byte[]> messageList = null;

		logger.debug("Iniciando el event viewer");
		try {
			while (!Thread.interrupted()) {
				messageList = remoteResponseQueue.pop();
					try {
						for (byte[] message : messageList) {
							response.deserialize(message);
							pw = new PrintWriter(new BufferedWriter(
									new FileWriter(eventFile, true)));
							pw.printf(
									"Evento recibido - UUID: {%s} - Status: {%s} - Mensaje: {%s}%n-----------------------------------------------------%n",
									response.getUuid(), response.getResponse_status(),
									response.getMessage());
							pw.close();
						}
					} catch (IOException | ClassNotFoundException e) {
						logger.error("No se ha podido escribir la respuesta: " + e);
					}
			}
		} finally {
			try {
				if (null != responseFile)
					try {
						responseFile.close();
					} catch (IOException e) {
						logger.error("No se ha podido cerrar el response file: " + e);
					}
			} catch (Exception e2) {
				logger.error("Error al cerrar el archivo " + eventFile + ": " + e2);
			}
		}

	}

}
