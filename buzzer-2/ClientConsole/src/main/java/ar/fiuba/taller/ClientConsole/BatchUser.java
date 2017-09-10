package ar.fiuba.taller.ClientConsole;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class BatchUser implements Callable {
	String userName;
	private BlockingQueue<Command> commandQueue;
	private BlockingQueue<Response> responseQueue;
	private Thread commandControllerThread;
	private Thread eventViewerThread;
	private Thread responseControllerThread;
	private ReadingRemoteQueue remoteUserResponseQueue;
	private WritingRemoteQueue dispatcherQueue;
	final static Logger logger = Logger.getLogger(BatchUser.class);
	
	public BatchUser(Map<String, String> config, String userName, String userHost) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.userName = userName;
		commandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		dispatcherQueue = new WritingRemoteQueue(
				config.get(Constants.DISPATCHER_QUEUE_NAME),
				config.get(Constants.DISPATCHER_QUEUE_HOST), config);
		commandControllerThread = new Thread(new CommandController(commandQueue,
				dispatcherQueue,
				Integer.parseInt(config.get(Constants.MAX_LENGTH_MSG))));
		responseQueue = new ArrayBlockingQueue<Response>(
				Constants.RESPONSE_QUEUE_SIZE);
		remoteUserResponseQueue = new ReadingRemoteQueue(userName, userHost,
				config);
		responseControllerThread = new Thread(
				new ResponseController(responseQueue, remoteUserResponseQueue));
		eventViewerThread = new Thread(new EventWriter(responseQueue, userName,
				Constants.LOGS_DIR + "/" + userName
						+ Constants.EVENT_VIEWER_FILE_EXTENSION));
	}

	@Override
	public Object call() throws Exception {
		logger.debug("Iniciando el script reader");
		
		commandControllerThread.start();
		eventViewerThread.start();
		responseControllerThread.start();
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(Constants.COMMAND_SCRIPT));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray commandArray = (JSONArray) jsonObject
					.get(Constants.COMMAND_ARRAY);
			Iterator<JSONObject> iterator = commandArray.iterator();
			JSONObject commandObject;
			Command command;
			
			while (iterator.hasNext()) {
				commandObject = iterator.next();
				command = new Command(
						(String) commandObject.get(Constants.COMMAND_KEY),
						userName,
						(String) commandObject.get(Constants.MESSAGE_KEY), null,
						null);
				logger.debug("Se inserto comando con los siguientes parametros: "
						+ "\nUsuario: " + command.getUser() + "\nComando: "
						+ command.getCommand() + "\nMensaje: "
						+ command.getMessage());
				commandQueue.put(command);
			}
		} catch (InterruptedException e) {
			logger.error("Thread interrumpido");
			logger.debug(e);
		} catch (ParseException | IOException e) {
			logger.error("Error al tratar el script de comandos");
			logger.debug(e);
		}
		return null;
	}
}