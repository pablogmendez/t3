package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class AnalyzerController implements Runnable {
	private BlockingQueue<Command> analyzerCommandQueue;
	private WritingRemoteQueue analyzerQueue;
	final static Logger logger = Logger.getLogger(AnalyzerController.class);

	public AnalyzerController(BlockingQueue<Command> analyzerCommandQueue,
			Map<String, String> config) {
		this.analyzerCommandQueue = analyzerCommandQueue;
		this.analyzerQueue = new WritingRemoteQueue(
				config.get(Constants.ANALYZER_QUEUE_NAME),
				config.get(Constants.ANALYZER_QUEUE_HOST), config);
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;

		logger.info("Iniciando el analyzer controller");
		try {
			while (!Thread.interrupted()) {
				try {
					command = analyzerCommandQueue.take();
					logger.info(
							"Comando recibido con los siguientes parametros: "
									+ "\nUsuario: " + command.getUser()
									+ "\nComando: " + command.getCommand()
									+ "\nMensaje: " + command.getMessage());
					analyzerQueue.push(command);
					logger.info("Comando enviado al analyzer");
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} catch (InterruptedException e) {
			logger.info("Analyzer controller interrumpido");
		}
		logger.info("Analyzer controller terminado");
	}
}
