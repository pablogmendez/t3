package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.RemoteQueue;

public class AnalyzerController implements Runnable {
	private BlockingQueue<Command> analyzerCommandQueue;
	private RemoteQueue analyzerQueue;
	
	final static Logger logger = Logger.getLogger(AnalyzerController.class);
	
	public AnalyzerController(BlockingQueue<Command> analyzerCommandQueue, 
			RemoteQueue analyzerQueue) {
		this.analyzerCommandQueue = analyzerCommandQueue;
		this.analyzerQueue = analyzerQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;
		
		logger.info("Iniciando el analyzer controller");
		while(true) {
			try {
				command = analyzerCommandQueue.take();
				logger.info("Comando recibido con los siguientes parametros: " 
						+ "\nUsuario: " + command.getUser()
						+ "\nComando: " + command.getCommand()
						+ "\nMensaje: " + command.getMessage());
				analyzerQueue.put(command);
				logger.info("Comando enviado al analyzer");
			} catch (InterruptedException e) {
				logger.error("Error al obtener el comando de la cola analyzerCommandQueue");
				logger.info(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error al insertar el comando de la cola analyzerQueue");
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
}
