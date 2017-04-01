package ar.fiuba.taller.ClientConsole;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;

public class CommandController implements Runnable {
	BlockingQueue<Command> commandQueue;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public CommandController(BlockingQueue<Command> commandQueue) {
		this.commandQueue = commandQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		Command command;
		
		logger.info("Iniciando el command controller");
		
		while(true) {
			try {
				logger.info("Obteniendo comando de la cola");
				command = commandQueue.take();
				logger.info("Comando obtenido");
				logger.info("Comando recibido: " + command.getCommand());
				switch(command.getCommand()) {
					case PUBLISH: {
						logger.info("Generando UUID");
						command.setUuid(UUID.randomUUID());
						logger.info("UUID generado: " + command.getUuid());
						logger.info("Enviando el mensaje al broker");
						break;
					}
					case QUERY:   {
						break;						
					}
					case DELETE:  {
						break;						
					}
					case FOLLOW:  {
						break;						
					}
					default:
						break;
					}				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
