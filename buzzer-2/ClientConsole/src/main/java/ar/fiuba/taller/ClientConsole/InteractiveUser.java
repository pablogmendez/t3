package ar.fiuba.taller.ClientConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;
import ar.fiuba.taller.common.Response;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class InteractiveUser implements Runnable {
	String userName;
	private BlockingQueue<Command> commandQueue;
	private BlockingQueue<Response> responseQueue;
	private Thread commandControllerThread;
	private Thread eventViewerThread;
	private Thread responseControllerThread;
	private ReadingRemoteQueue remoteUserResponseQueue;
	private WritingRemoteQueue dispatcherQueue;

	public InteractiveUser(Map<String, String> config, String userName,
			String userHost) {
		this.userName = userName;
		commandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		dispatcherQueue = new WritingRemoteQueue(
				config.get(Constants.DISPATCHER_QUEUE_NAME),
				config.get(Constants.DISPATCHER_QUEUE_HOST), config);
		commandControllerThread = new Thread(
				new CommandController(commandQueue, dispatcherQueue,
						Integer.parseInt(config.get(Constants.MAX_LENGTH_MSG)),
						Constants.LOGS_DIR + "/" + userName
								+ Constants.COMMANDS_FILE_EXTENSION));
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

	public void run() {
		BufferedReader br = null;
		String[] msgParts;

		commandControllerThread.start();
		eventViewerThread.start();
		responseControllerThread.start();

		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			while (!Thread.interrupted()) {
				try {
					System.out.print("Enter command: ");
					String input = br.readLine();
					msgParts = input.split(":");
					commandQueue.put(new Command(msgParts[0], userName,
							msgParts[1], null, null));
				} catch (IOException e) {
					System.out.println(
							"Error: No se ha podido procesar el comando");
				}
			}
		} catch (InterruptedException e) {
			remoteUserResponseQueue.shutDown();
			commandControllerThread.interrupt();
			eventViewerThread.interrupt();
			responseControllerThread.interrupt();
			try {
				commandControllerThread.join(Constants.USER_THREAD_WAIT_TIME);
				eventViewerThread.join(Constants.USER_THREAD_WAIT_TIME);
				responseControllerThread.join(Constants.USER_THREAD_WAIT_TIME);
			} catch (InterruptedException e1) {
				// Do nothing
			}
		}
	}
}
