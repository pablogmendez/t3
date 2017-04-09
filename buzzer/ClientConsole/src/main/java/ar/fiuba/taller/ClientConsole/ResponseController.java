package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class ResponseController extends DefaultConsumer implements Runnable {
	
	private BlockingQueue<Response> responseQueue;
	private RemoteQueue remoteResponseQueue;
	final static Logger logger = Logger.getLogger(ResponseController.class);
	
	public ResponseController(BlockingQueue<Response> responseQueue, RemoteQueue remoteResponseQueue) {
		super(remoteResponseQueue.getChannel());
		this.responseQueue = responseQueue;
		this.remoteResponseQueue = remoteResponseQueue;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		Response response = new Response();
		try {
			logger.info("Respuesta recibida con los siguientes valores: "
			+ "\nUUID:" + response.getUuid() 
			+ "\nStatus:" + response.getResponse_status() 
			+ "\nMensaje:" + response.getMessage());
			response.deserialize(body);
			responseQueue.put(response);
			logger.error("Respuesta pusheada en la cola responseQueue");
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

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Iniciando el response controller");
//		while(true) {
			try {
				remoteResponseQueue.getChannel().basicConsume(remoteResponseQueue.getQueueName(), true, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
	}

}
