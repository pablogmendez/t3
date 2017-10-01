package ar.fiuba.taller.ClientConsole;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import ar.fiuba.taller.common.WritingRemoteQueue;

public class BatchUser implements Callable {
	private String userName;
	private int commandAmount;
	private CommandController commandController;
	private Thread eventViewerThread;
	private ReadingRemoteQueue remoteUserResponseQueue;
	private WritingRemoteQueue dispatcherQueue;
	private long delayTime;
	final static Logger logger = Logger.getLogger(BatchUser.class);

	public BatchUser(Map<String, String> config, String userName,
			String userHost) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.userName = userName;
		commandAmount = Integer.parseInt(config.get(Constants.COMMAND_AMOUNT));
		try {
			dispatcherQueue = new WritingRemoteQueue(
					config.get(Constants.DISPATCHER_QUEUE_NAME),
					config.get(Constants.KAFKA_WRITE_PROPERTIES));
			remoteUserResponseQueue = new ReadingRemoteQueue(userName, config.get(Constants.KAFKA_READ_PROPERTIES));
		} catch (IOException e) {
			logger.error("No se han podido inicializar las colas de kafka: " + e);
			System.exit(1);
		}
		commandController =
				new CommandController(dispatcherQueue,
						Integer.parseInt(config.get(Constants.MAX_LENGTH_MSG)),
						Constants.LOGS_DIR + "/" + userName
								+ Constants.COMMANDS_FILE_EXTENSION);
		eventViewerThread = new Thread(new EventWriter(
				Constants.LOGS_DIR + "/" + userName
						+ Constants.EVENT_VIEWER_FILE_EXTENSION, remoteUserResponseQueue));
		delayTime = Long.parseLong(config.get(Constants.BATCH_DELAY_TIME));
	}

	@Override
	public Object call() throws Exception {
		logger.debug("Iniciando el script reader");
		int count = 0;

		eventViewerThread.start();

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(Constants.COMMAND_SCRIPT));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray commandArray = (JSONArray) jsonObject
					.get(Constants.COMMAND_ARRAY);
			JSONObject commandObject;
			Command command;
			List<Integer> commandIndexList = getCommandIndexList(commandAmount,
					commandArray.size());
			Iterator<Integer> iterator = commandIndexList.iterator();

			while (iterator.hasNext()) {
				commandObject = (JSONObject) commandArray.get(iterator.next());
				command = new Command(
						(String) commandObject.get(Constants.COMMAND_KEY),
						userName,
						(String) commandObject.get(Constants.MESSAGE_KEY), null,
						null);
				logger.debug("COMANDO: " + count
						+ ".Se inserto comando con los siguientes parametros: "
						+ "\nUsuario: " + command.getUser() + "\nComando: "
						+ command.getCommand() + "\nMensaje: "
						+ command.getMessage());
				commandController.sendMessage(command);
				++count;
			}
		} catch (ParseException | IOException e) {
			logger.error("Error al tratar el script de comandos: " + e);
		}
		return null;
	}

	private List<Integer> getCommandIndexList(int commandListIndexSize,
			int maxCommandsAvailable) {
		List<Integer> commandIndexList = new ArrayList<Integer>();

		for (int i = 0; i < commandListIndexSize; i++) {
			commandIndexList.add((int) (Math.random() * maxCommandsAvailable));
		}

		return commandIndexList;
	}

}
