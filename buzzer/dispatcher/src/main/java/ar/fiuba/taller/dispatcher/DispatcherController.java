package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class DispatcherController extends DefaultConsumer implements Runnable {

    RemoteQueue dispatcherQueue;
    BlockingQueue<Command> storageCommandQueue;
    BlockingQueue<Command> analyzerCommandQueue;
    BlockingQueue<Command> loggerCommandQueue;
    final static Logger logger = Logger.getLogger(DispatcherController.class);
    
	public DispatcherController(RemoteQueue dispatcherQueue, 
			BlockingQueue<Command> storageCommandQueue,
			BlockingQueue<Command> analyzerCommandQueue,
			BlockingQueue<Command> loggerCommandQueue) {
		super(dispatcherQueue.getChannel());
		this.storageCommandQueue = storageCommandQueue;
		this.analyzerCommandQueue = analyzerCommandQueue;
		this.loggerCommandQueue = loggerCommandQueue;
		this.dispatcherQueue = dispatcherQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Iniciando el dispatcher controller");
//		while(true) {
			try {
				dispatcherQueue.getChannel().basicConsume(dispatcherQueue.getQueueName(), true, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		Command command = new Command();
		try {
			command.deserialize(body);
			logger.info("Comando recibido con los siguientes parametros: " 
					+ "\nUsuario: " + command.getUser()
					+ "\nComando: " + command.getCommand()
					+ "\nMensaje: " + command.getMessage());
			logger.info("Enviando mensaje a la cola del storage");
			storageCommandQueue.put(command);
			logger.info("Enviando mensaje a la cola del analyzer");
			analyzerCommandQueue.put(command);
			logger.info("Enviando mensaje a la cola del logger");
			loggerCommandQueue.put(command);
		} catch (ClassNotFoundException e) {
			logger.info("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al insertar el comando en alguna de las colas");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}
