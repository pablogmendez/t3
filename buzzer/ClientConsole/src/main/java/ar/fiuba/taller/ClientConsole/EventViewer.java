package ar.fiuba.taller.ClientConsole;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Response;

public class EventViewer implements Runnable {
	BlockingQueue<Response> responseQueue;
	String username;
	String eventFile;
	final static Logger logger = Logger.getLogger(App.class);
	
	public EventViewer(BlockingQueue<Response> responseQueue, String username, String eventFile) {
		this.responseQueue = responseQueue;
		this.username = username;
		this.eventFile = eventFile;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Response response = null;
		FileWriter responseFile = null;
	    PrintWriter pw = null;
	    String event = null;
		
	    logger.info("Iniciando el event viewer");
	    try {
	    	pw = new PrintWriter(eventFile, "UTF-8");			
			while(true) {
					response = responseQueue.take();
					pw.println("Evento recibido:\nUUID: " + response.getUuid()  + "\nResponse Status: " + response.getResponse_status() 
					+ "\nMessage: " + response.getMessage());
					pw.println("-----------------------------------------------------------------------------------------------------");
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
