package ar.fiuba.taller.ClientConsole;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.*;

public class UserConsole implements Runnable {
	private String username;
	private BlockingQueue<Command> commandQueue;
	private BlockingQueue<Response> responseQueue;
	private Thread scriptReaderThread;
	private Thread commandControllerThread;
	private Thread eventViewerThread;
	private Thread responseControllerThread;
	private RemoteQueue remoteUserResponseQueue;
	private RemoteQueue dispatcherQueue;
	private ConfigLoader configLoader = ConfigLoader.getInstance();
	private String mode;
	final static Logger logger = Logger.getLogger(UserConsole.class);

	public UserConsole(String username) {
		this.username = username;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));

		try {
			logger.info("Iniciando usuario.");
			logger.info("Cargando la configuracion");
			configLoader.init(Constants.CONF_FILE);
			initUser();
			startUser();
			terminateUser();
		} catch (InterruptedException e) {
			logger.error("Error al joinear los threads");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(
					"Error al cargar la configuracion o crear las colas remotas");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initUser() throws IOException, TimeoutException {
		logger.info("Creando cola de comandos leidos");
		commandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		logger.info("Creando cola del dispatcher");
		dispatcherQueue = new RemoteQueue(configLoader.getDispatcherQueueName(),
				configLoader.getDispatcherQueueHost());
		dispatcherQueue.init();
		logger.info("Creando lector de scripts");
		scriptReaderThread = new Thread(
				new ScriptReader(commandQueue,
						Constants.COMMAND_SCRIPT_FOLDER + "/" + username
								+ Constants.COMMAND_SCRIPT_EXTENSION,
						username));
		logger.info("Creando controlador de comandos");
		commandControllerThread = new Thread(
				new CommandController(commandQueue, dispatcherQueue));
		logger.info("Creando cola de respuestas");
		responseQueue = new ArrayBlockingQueue<Response>(
				Constants.RESPONSE_QUEUE_SIZE);
		logger.info("Creando cola remota de respuestas");
		remoteUserResponseQueue = new RemoteQueue(username,
				configLoader.getResponseQueueHost());
		remoteUserResponseQueue.init();
		logger.info("Creando el controlador de respuestas");
		responseControllerThread = new Thread(
				new ResponseController(responseQueue, remoteUserResponseQueue));
		logger.info("Creando el visor de eventos");
		eventViewerThread = new Thread(new EventViewer(responseQueue, username,
				Constants.LOGS_DIR + "/" + username
						+ Constants.EVENT_VIEWER_FILE_EXTENSION));
	}

	private void startUser() {
		logger.info("Iniciando el lector de scripts");
		scriptReaderThread.start();
		logger.info("Iniciando el controlador de comandos");
		commandControllerThread.start();
		logger.info("Iniciando el controlador de respuestas");
		responseControllerThread.start();
		logger.info("Iniciando el visor de eventos");
		eventViewerThread.start();
	}

	private void terminateUser()
			throws InterruptedException, IOException, TimeoutException {
		logger.info("Esperando al controlador de comandos");
		commandControllerThread.join();
		logger.info("controller finalizado!");
		logger.info("Esperando al reader");
		scriptReaderThread.join();
		logger.info("Reader finalizado!");
		logger.info("Esperando al controlador de respuestas");
		responseControllerThread.join();
		logger.info("controller controlador de respuestas!");
		logger.info("Esperando al visor de eventos");
		eventViewerThread.join();
		logger.info("visor de eventos finalizado!");
	}
}
