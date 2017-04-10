package ar.fiuba.taller.ClientConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.Response;

public class EventViewer implements Runnable {
	BlockingQueue<Response> responseQueue;
	String username;
	String eventFile;
	final static Logger logger = Logger.getLogger(EventViewer.class);
	
	public EventViewer(BlockingQueue<Response> responseQueue, String username, String eventFile) {
		this.responseQueue = responseQueue;
		this.username = username;
		this.eventFile = eventFile;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Response response = null;
		FileWriter responseFile = null;
	    PrintWriter pw;
	    
	    logger.info("Iniciando el event viewer");
	    try {
			while(true) {
					logger.info("Esperando respuesta");
					response = responseQueue.take();
					pw = new PrintWriter(new BufferedWriter(new FileWriter(eventFile, true)));
					logger.info("Respuesta obtenida");
					pw.println("Evento recibido:\nUUID: " + response.getUuid()  + "\nResponse Status: " + response.getResponse_status() 
					+ "\nMessage: " + response.getMessage());
					pw.println("-----------------------------------------------------------------------------------------------------");
					pw.close();
			}
		} catch (InterruptedException e) {
			logger.error("Error al tomar la respuesta en la cola responseQueue");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al abrir el archivo " + eventFile);
			logger.info(e.toString());
			e.printStackTrace();
		} finally {
           try {
           		if (null != responseFile) responseFile.close();
           } catch (Exception e2) {
	   			logger.error("Error al cerrar el archivo " + eventFile);
	   			logger.info(e2.toString());
	   			e2.printStackTrace();
           }
		}

	}

}
