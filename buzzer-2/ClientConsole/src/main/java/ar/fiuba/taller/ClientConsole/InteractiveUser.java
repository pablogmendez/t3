package ar.fiuba.taller.ClientConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class InteractiveUser {
	String userName;
	private CommandController commandController;
	private Thread eventViewerThread;
	private ReadingRemoteQueue remoteUserResponseQueue;
	private WritingRemoteQueue dispatcherQueue;

	public InteractiveUser(Map<String, String> config, String userName,
			String userHost) {
		this.userName = userName;
		try {
			dispatcherQueue = new WritingRemoteQueue(
					config.get(Constants.DISPATCHER_QUEUE_NAME),
					config.get(Constants.KAFKA_WRITE_PROPERTIES));
			remoteUserResponseQueue = new ReadingRemoteQueue(userName, config.get(Constants.KAFKA_READ_PROPERTIES));
		} catch (IOException e) {
			System.out.printf("No se han podido inicializar las colas de kafka: %s", e);
			System.exit(1);
		}
		commandController =
				new CommandController(dispatcherQueue,
						Integer.parseInt(config.get(Constants.MAX_LENGTH_MSG)),
						Constants.LOGS_DIR + "/" + userName
								+ Constants.COMMANDS_FILE_EXTENSION);
		eventViewerThread = new Thread(new EventWriter(
				Constants.LOGS_DIR + "/" + userName
						+ Constants.EVENT_VIEWER_FILE_EXTENSION, 
						remoteUserResponseQueue));
	}

	public void run() {
		BufferedReader br = null;
		String[] msgParts;
		
		eventViewerThread.start();
		br = new BufferedReader(new InputStreamReader(System.in));
		while (!Thread.interrupted()) {
			try {
				System.out.print("Enter command: ");
				String input = br.readLine();
				msgParts = input.split(":");
				commandController.sendMessage(new Command(msgParts[0], userName,
						msgParts[1], null, null));
			} catch (IOException e) {
				System.out.println(
						"Error: No se ha podido procesar el comando");
			}
		}

		remoteUserResponseQueue.shutDown();
		try {
			remoteUserResponseQueue.close();
		} catch (IOException | TimeoutException e) {
			// Do nothing
		}
		eventViewerThread.interrupt();
		try {
			eventViewerThread.join(Constants.USER_THREAD_WAIT_TIME);
		} catch (InterruptedException e1) {
			// Do nothing
		}
	}
}
