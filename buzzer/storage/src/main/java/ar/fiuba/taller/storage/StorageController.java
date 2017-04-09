package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;
import ar.fiuba.taller.common.Response;

public class StorageController extends DefaultConsumer implements Runnable {

	private Thread createControllerThread;
	private Thread queryControllerThread;
	private Thread removeControllerThread;
	private Thread responseControllerThread;
	private BlockingQueue<Command> queryQueue;
	private BlockingQueue<Command> removeQueue;
	private BlockingQueue<Command> createQueue;
	private BlockingQueue<Response> responseQueue;
	private ConfigLoader configLoader;
	private Storage storage;
	private RemoteQueue storageQueue;
	final static Logger logger = Logger.getLogger(StorageController.class);
	
	public StorageController(RemoteQueue storageQueue) {
		super(storageQueue.getChannel());
		configLoader = ConfigLoader.getInstance();
		storage = new Storage(configLoader.getShardingFactor(), 
				configLoader.getQueryCountShowPosts(), configLoader.getTtCountShowPosts());
		this.storageQueue = storageQueue;
	}

	public void run() {
    	MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
        
    	logger.info("Iniciando el storage controller");
    
        try {
        	logger.info("Cargando la configuracion");
        	configLoader.init(Constants.CONF_FILE);
        	
        	logger.info("Creando las colas de consultas, removes, creates y response");
        	queryQueue 		= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        	removeQueue		= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        	createQueue 	= new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        	responseQueue 	= new ArrayBlockingQueue<Response>(Constants.COMMAND_QUEUE_SIZE);
        	
        	logger.info("Instancio los indices de usuarios y hashtags");
        	
        	logger.info("Creando los threads de query, remove y create");
        	queryControllerThread			= new Thread(new QueryController(queryQueue, responseQueue, storage));
        	removeControllerThread			= new Thread(new RemoveController(removeQueue, responseQueue, storage));
        	createControllerThread 			= new Thread(new CreateController(createQueue, responseQueue,  
        			configLoader.getShardingFactor(), storage));
        	responseControllerThread			= new Thread(new ResponseController(responseQueue));
        	
        	logger.info("Lanzando los threads de query, remove y create");
        	queryControllerThread.start();
        	removeControllerThread.start();
        	createControllerThread.start();
        	responseControllerThread.start();
	    	
        	logger.info("Me pongo a comer de la cola: " + storageQueue.getHost() + " " + storageQueue.getQueueName());
        	storageQueue.getChannel().basicConsume(storageQueue.getQueueName(), true, this);
        	
        } catch (IOException e) {
			logger.error("Error al cargar el archivo de configuracion");
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
					+ "\nUUID: " + command.getUuid()
					+ "\nUsuario: " + command.getUser()
					+ "\nComando: " + command.getCommand()
					+ "\nMensaje: " + command.getMessage());
			
			switch(command.getCommand()) {
				case PUBLISH:
					logger.info("Comando recibido: PUBLISH. Insertando en la cola de creacion.");
					createQueue.put(command);
					break;
				case QUERY:
					logger.info("Comando recibido: QUERY. Insertando en la cola de consultas.");
					queryQueue.put(command);
					break;
				case DELETE:
					logger.info("Comando recibido: DELETE. Insertando en la cola de borrado.");
					removeQueue.put(command);
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
		}
	}

}
