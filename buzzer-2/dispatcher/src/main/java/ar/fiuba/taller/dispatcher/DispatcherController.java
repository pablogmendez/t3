package ar.fiuba.taller.dispatcher;

import java.io.IOException;
import java.util.Iterator;
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

public class DispatcherController implements Runnable {

	private ReadingRemoteQueue dispatcherQueue;
	private BlockingQueue<Command> storageCommandQueue;
	private BlockingQueue<Command> analyzerCommandQueue;
	private BlockingQueue<Command> loggerCommandQueue;
	private Thread analyzerControllerThread;
	private Thread storageControllerThread;
	private Thread loggerControllerThread;
	final static Logger logger = Logger.getLogger(DispatcherController.class);

	public DispatcherController(Map<String, String> config,
			ReadingRemoteQueue dispatcherQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		analyzerCommandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		storageCommandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		loggerCommandQueue = new ArrayBlockingQueue<Command>(
				Constants.COMMAND_QUEUE_SIZE);
		analyzerControllerThread = new Thread(
				new AnalyzerController(analyzerCommandQueue, config));
		storageControllerThread = new Thread(
				new StorageController(storageCommandQueue, config));
		loggerControllerThread = new Thread(
				new LoggerController(loggerCommandQueue, config));
		this.dispatcherQueue = dispatcherQueue;
	}

	public void run() {
		Command command = new Command();
		List<byte[]> messageList = null;

		analyzerControllerThread.start();
		storageControllerThread.start();
		loggerControllerThread.start();

		logger.info("Iniciando el dispatcher controller");
		try {
			while (!Thread.interrupted()) {
				messageList = dispatcherQueue.pop();
				Iterator<byte[]> it = messageList.iterator();
				while(it.hasNext()) {
				//for (byte[] message : messageList) {
					try {
						command = new Command();
						command.deserialize(it.next());
						logger.info(
								"Comando recibido con los siguientes parametros: "
										+ "\nUsuario: " + command.getUser()
										+ "\nComando: " + command.getCommand()
										+ "\nMensaje: " + command.getMessage());
						switch (command.getCommand()) {
						case PUBLISH:
							storageCommandQueue.put(command);
							analyzerCommandQueue.put(command);
							loggerCommandQueue.put(command);
							logger.info(
									"Comando enviado al publish: "
											+ "\nUsuario: " + command.getUser()
											+ "\nComando: " + command.getCommand()
											+ "\nMensaje: " + command.getMessage());
							break;
						case QUERY:
							storageCommandQueue.put(command);
							loggerCommandQueue.put(command);
							logger.info(
									"Comando enviado al query: "
											+ "\nUsuario: " + command.getUser()
											+ "\nComando: " + command.getCommand()
											+ "\nMensaje: " + command.getMessage());
							break;
						case DELETE:
							logger.info(
									"Comando enviado al delete: "
											+ "\nUsuario: " + command.getUser()
											+ "\nComando: " + command.getCommand()
											+ "\nMensaje: " + command.getMessage());
							storageCommandQueue.put(command);
							loggerCommandQueue.put(command);
							break;
						case FOLLOW:
							logger.info(
									"Comando enviado al follow: "
											+ "\nUsuario: " + command.getUser()
											+ "\nComando: " + command.getCommand()
											+ "\nMensaje: " + command.getMessage());
							analyzerCommandQueue.put(command);
							loggerCommandQueue.put(command);
							break;
						default:
							logger.error("Comando invalido");
							break;
						}
					} catch (ClassNotFoundException | IOException e) {
						logger.error("No se ha podido deserializar el mensaje");
						logger.debug(e);
						e.printStackTrace();
					}
				}
			}
		} catch (ReadingRemoteQueueException | InterruptedException e) {
			analyzerControllerThread.interrupt();
			storageControllerThread.interrupt();
			loggerControllerThread.interrupt();
			try {
				analyzerControllerThread.join();
				storageControllerThread.join();
				loggerControllerThread.join();
			} catch (InterruptedException e1) {
				// Do nothing
			}
		}
		logger.info("Dispatcher controller terminado");
	}
}