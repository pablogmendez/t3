package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.utils.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class LoadTestConsole implements Runnable {

	private Integer maxSizeUserPoolThread;
	private String function;
	private List<Integer> functionParamList;
	private UserPattern userPattern;
	private List<Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>>> usersQueuesList;
	private TerminateSignal terminateSignal;

	final static Logger logger = Logger.getLogger(App.class);

	public LoadTestConsole(TerminateSignal terminateSignal) {
		this.terminateSignal = terminateSignal;
		ConfigLoader.getInstance().init(Constants.PROPERTIES_FILE);
		usersQueuesList = new ArrayList<Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>>>();
		function = ConfigLoader.getInstance().getFunction();
		functionParamList = ConfigLoader.getInstance()
				.getFunctionPatternParam();
		maxSizeUserPoolThread = ConfigLoader.getInstance()
				.getMaxsizeUserPoolThread();

		// TODO: Arreglar esto
		if (function.equals("ConstantUserPattern")) {
			userPattern = new ConstantUserPattern(functionParamList);
		} else if (function.equals("StairsUserPattern")) {
			userPattern = new StairsUserPattern(functionParamList,
					ConfigLoader.getInstance().getMaxsizeUserPoolThread());
		} else if (function.equals("RampUserPattern")) {
			userPattern = new RampUserPattern(functionParamList,
					ConfigLoader.getInstance().getMaxsizeUserPoolThread());
		}

	}

	public void run() {
		ArrayBlockingQueue<UserTask> userTaskPendingQueue;
		ArrayBlockingQueue<UserTask> userTaskFinishedQueue;
		ArrayBlockingQueue<SummaryTask> summaryPendingQueue = new ArrayBlockingQueue<SummaryTask>(
				ConfigLoader.getInstance().getTasksQueueSize());
		ArrayBlockingQueue<SummaryTask> summaryFinishedQueue = new ArrayBlockingQueue<SummaryTask>(
				ConfigLoader.getInstance().getTasksQueueSize());
		ArrayBlockingQueue<ReportTask> reportPendingQueue = new ArrayBlockingQueue<ReportTask>(
				ConfigLoader.getInstance().getTasksQueueSize());
		ArrayBlockingQueue<ReportTask> reportFinishedQueue = new ArrayBlockingQueue<ReportTask>(
				ConfigLoader.getInstance().getTasksQueueSize());

		Summary summary = new Summary();
		Report report = new Report();
		TerminateSignal summaryTerminateSignal = new TerminateSignal();
		TerminateSignal reportTerminateSignal = new TerminateSignal();
		Thread summaryControllerThread = new Thread(new SummaryController(
				summaryPendingQueue, summaryFinishedQueue, summary));
		Thread summaryPrinterThread = new Thread(new SummaryPrinter(summary,
				summaryTerminateSignal, terminateSignal));
		Thread reportControllerThread = new Thread(new ReportController(
				reportPendingQueue, reportFinishedQueue, report));
		Thread monitorThread = new Thread(
				new Monitor(report, reportTerminateSignal));

		Integer currentUsers = 0, deltaUseres = 0, tick = 0, userNumber = 0;
		UserTask userTask;
		SummaryTask summaryTask;
		ReportTask reportTask;

		logger.info("Se inicia una nueva instancia de LoadTestConsole");
		logger.info("Creando el pool de threads de usuarios");
		ExecutorService usersThreadPool = Executors
				.newFixedThreadPool(maxSizeUserPoolThread);

		// Reportes
		summaryControllerThread.start();
		summaryPrinterThread.start();
		reportControllerThread.start();
		monitorThread.start();

		try {
			while (!terminateSignal.hasTerminate()) {
				tick++;
				deltaUseres = userPattern.getUsers(tick) - currentUsers;
				logger.info("Se inicia el pulso: " + tick);
				logger.info("Cantidad de usuarios actualmente corriendo: "
						+ currentUsers);
				logger.info(
						"Cantidad de usuarios que deben correr en este pulso: "
								+ userPattern.getUsers(tick));
				logger.info("Cantidad de usuarios que deben ingresar: "
						+ deltaUseres);

				for (int i = 0; i < deltaUseres; i++) {
					currentUsers++;
					logger.info("Creando el usuario: " + currentUsers);
					// Creo las colas para el nuevo usuario
					userTaskPendingQueue = new ArrayBlockingQueue<UserTask>(
							ConfigLoader.getInstance().getTasksQueueSize());
					userTaskFinishedQueue = new ArrayBlockingQueue<UserTask>(
							ConfigLoader.getInstance().getTasksQueueSize());

					// Me guardo las dos colas en la lista de colas de usuarios
					usersQueuesList
							.add(new Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>>(
									userTaskPendingQueue,
									userTaskFinishedQueue));

					// Creo el usuario y les paso las colas para que puedan
					// insertar tareas
					usersThreadPool.submit(new User(userTaskPendingQueue,
							userTaskFinishedQueue, summaryPendingQueue,
							reportPendingQueue));
					logger.info("Usuario creado");
				}
				logger.info("Usuarios creados");

				// Actualizo la cola de estadisticas con la cantidad de usuarios
				// actuales
				summaryPendingQueue.put(new SummaryTask(Constants.DEFAULT_ID,
						Constants.TASK_STATUS.SUBMITTED, currentUsers, null,
						0));

				userNumber = 0;

				// Despierto a los users enviandoles un mensaje para que se
				// pongan a trabajar
				logger.info("Despertando los usuarios para un nuevo pulso");
				for (Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
					logger.info("Despertando al usuario: " + userNumber);
					pair.getFirst().put(new UserTask(Constants.DEFAULT_ID,
							Constants.TASK_STATUS.SUBMITTED));

					++userNumber;
				}

				userNumber = 0;

				logger.info(
						"Me quedo esperando a que los usuarios terminen sus tareas");
				for (Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
					logger.info("Esperando al usuario: " + userNumber);
					userTask = pair.getSecond().take();
					logger.info("Usuario: " + userNumber + " finalizado");
					++userNumber;
				}

			}
			// Termino la simulacion. Tengo que parar a los usuarios

			userNumber = 0;
			// Despierto a los users enviandoles un mensaje de desconeccion
			logger.info("Despertando los usuarios para desconectar");
			for (Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
				logger.info("Desconectando al usuario: " + userNumber);
				pair.getFirst().put(new UserTask(Constants.DISCONNECT_ID,
						Constants.TASK_STATUS.SUBMITTED));
				++userNumber;
			}

			userNumber = 0;
			logger.info("Me quedo esperando a que los usuarios se desconecten");
			for (Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
				logger.info("Esperando al usuario: " + userNumber);
				do {
					userTask = pair.getSecond().take();
				} while (userTask.getId() != Constants.DISCONNECT_ID);
				logger.info("Usuario: " + userNumber + " desconectado");
				++userNumber;
			}

			// Reportes
			logger.info("Parando el summary printer");
			summaryTerminateSignal.terminate();
			summaryPrinterThread.join();
			logger.info("Summary printer finalizado");

			logger.info("Parando el monitor");
			reportTerminateSignal.terminate();
			monitorThread.join();
			logger.info("Monitor finalizado");

			logger.info("Parando el Controlador de reportes");
			reportPendingQueue.put(new ReportTask(Constants.DISCONNECT_ID,
					Constants.TASK_STATUS.SUBMITTED, null, null));
			reportTask = reportFinishedQueue.take();
			reportControllerThread.join();
			logger.info("Controlador de reportes finalizado");

			logger.info("Parando el Controlador de resumen");
			summaryPendingQueue.put(new SummaryTask(Constants.DISCONNECT_ID,
					Constants.TASK_STATUS.SUBMITTED, 0, false, 0));
			summaryTask = summaryFinishedQueue.take();
			summaryControllerThread.join();
			logger.info("Controlador de resumen finalizado");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<Integer> getFunctionParamList() {
		return functionParamList;
	}

	public void setFunctionParamList(ArrayList<Integer> functionParamList) {
		this.functionParamList = functionParamList;
	}

}
