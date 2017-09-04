package ar.fiuba.taller.analyzer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;
import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class AnalyzerReciver extends DefaultConsumer implements Runnable {

	BlockingQueue<Response> responseQueue;
	Command command;
	Response response;
	UserRegistry userRegistry;
	RemoteQueue analyzerQueue;
	final static Logger logger = Logger.getLogger(AnalyzerReciver.class);

	public AnalyzerReciver(BlockingQueue<Response> responseQueue,
			RemoteQueue analyzerQueue, UserRegistry userRegistry) {
		super(analyzerQueue.getChannel());
		this.responseQueue = responseQueue;
		this.userRegistry = userRegistry;
		this.analyzerQueue = analyzerQueue;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		logger.info("Iniciando el analyzer reciver");
		logger.info("Me pongo a comer de la cola: " + analyzerQueue.getHost()
				+ " " + analyzerQueue.getQueueName());
		try {
			analyzerQueue.getChannel()
					.basicConsume(analyzerQueue.getQueueName(), true, this);
		} catch (IOException e) {
			logger.error("Error al comer de la cola");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		Command command = new Command();
		try {
			command.deserialize(body);
			logger.info("Comando recibido con los siguientes parametros: "
					+ "\nUUID: " + command.getUuid() + "\nUsuario: "
					+ command.getUser() + "\nComando: " + command.getCommand()
					+ "\nMensaje: " + command.getMessage());

			switch (command.getCommand()) {
			case PUBLISH:
				logger.info(
						"Comando recibido: PUBLISH. Insertando en la cola del "
						+ "analyzer dispatcher.");
				response = new Response();
				response.setUuid(command.getUuid());
				response.setUser(command.getUser());
				// Puede ser que de error en caso de hacer el update, entonces
				// hay que
				// mandarle error al usuario
				response.setResponse_status(RESPONSE_STATUS.OK);
				response.setMessage(command.getTimestamp() + "\n"
						+ command.getUser() + "\n" + command.getMessage());
				responseQueue.put(response);
				break;
			case FOLLOW:
				logger.info(
						"Comando recibido: FOLLOW. Actualizando el user "
						+ "registry.");
				userRegistry.update(command.getUser(), command.getMessage());
				response = new Response();
				response.setUuid(command.getUuid());
				response.setUser(command.getUser());
				response.setResponse_status(RESPONSE_STATUS.REGISTERED);
				response.setMessage("Seguidor registrado");
				responseQueue.put(response);
				break;
			default:
				logger.info("Comando recibido invalido. Comando descartado.");
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error al insertar el comando en alguna de las colas");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (ParseException e) {
			logger.error("Error al actualizar la base de usuarios");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

}
