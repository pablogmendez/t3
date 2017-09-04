package ar.fiuba.taller.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.ReadingRemoteQueueException;
import ar.fiuba.taller.common.Response;

public class StorageController implements Runnable {
	private Thread createControllerThread;
	private Thread queryControllerThread;
	private Thread removeControllerThread;
	private Thread responseControllerThread;
	private BlockingQueue<Command> queryQueue;
	private BlockingQueue<Command> removeQueue;
	private BlockingQueue<Command> createQueue;
	private BlockingQueue<Response> responseQueue;
	private Storage storage;
	private ReadingRemoteQueue storageQueue;
	final static Logger logger = Logger.getLogger(StorageController.class);

	public StorageController(Map<String, String> config,
			ReadingRemoteQueue storageQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		storage = new Storage(
				Integer.parseInt(config.get(Constants.SHARDING_FACTOR)),
				Integer.parseInt(config.get(Constants.QUERY_COUNT_SHOW_POSTS)),
				Integer.parseInt(config.get(Constants.TT_COUNT_SHOW)));
		this.storageQueue = storageQueue;
		queryQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		removeQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		createQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		responseQueue = new ArrayBlockingQueue<Response>(
				Constants.RESPONSE_QUEUE_SIZE);
		queryControllerThread = new Thread(
				new QueryController(queryQueue, responseQueue, storage));
		removeControllerThread = new Thread(
				new RemoveController(removeQueue, responseQueue, storage));
		createControllerThread = new Thread(
				new CreateController(createQueue, responseQueue,
						Integer.parseInt(config.get(Constants.SHARDING_FACTOR)),
						storage));
		responseControllerThread = new Thread(
				new ResponseController(responseQueue, config));
	}

	public void run() {
		Command command = new Command();
		List<byte[]> messageList = null;

		logger.info("Lanzando los threads de query, remove y create");
		queryControllerThread.start();
		removeControllerThread.start();
		createControllerThread.start();
		responseControllerThread.start();
		
		logger.info("Consumiendo de la storageQueue");
	    try {
	        while (!Thread.interrupted()) {
	          messageList = storageQueue.pop();
	          for(byte[] message : messageList) {
	  			try {
					command.deserialize(message);
					analyzeCommand(command);

				} catch (ClassNotFoundException | IOException  e) {
					logger.error("No se ha podido deserializar el mensaje");
				}
	          }
	        }
	    } catch (ReadingRemoteQueueException | InterruptedException e) {
			queryControllerThread.interrupt();
			removeControllerThread.interrupt();
			createControllerThread.interrupt();
			responseControllerThread.interrupt();
			try {
				queryControllerThread.join(Constants.STORAGE_THREAD_WAIT_TIME);
				removeControllerThread.join(Constants.STORAGE_THREAD_WAIT_TIME);
				createControllerThread.join(Constants.STORAGE_THREAD_WAIT_TIME);
				responseControllerThread.join(Constants.STORAGE_THREAD_WAIT_TIME);
			} catch (InterruptedException e1) {
				logger.error("Fallo el join de alguno de los threads");
				logger.debug(e1);
			}
	    }
	    logger.info("Storgae Controller terminado");
	}

	private void analyzeCommand(Command command) throws InterruptedException {
		logger.info("Comando recibido con los siguientes parametros: "
				+ "\nUUID: " + command.getUuid() + "\nUsuario: "
				+ command.getUser() + "\nComando: " + command.getCommand()
				+ "\nMensaje: " + command.getMessage());

		switch (command.getCommand()) {
		case PUBLISH:
			logger.info(
					"Comando recibido: PUBLISH. Insertando en la cola de creacion.");
			createQueue.put(command);
			break;
		case QUERY:
			logger.info(
					"Comando recibido: QUERY. Insertando en la cola de consultas.");
			queryQueue.put(command);
			break;
		case DELETE:
			logger.info(
					"Comando recibido: DELETE. Insertando en la cola de borrado.");
			removeQueue.put(command);
			break;
		default:
			logger.info("Comando recibido invalido. Comando descartado.");
		}		
	}
	
}
