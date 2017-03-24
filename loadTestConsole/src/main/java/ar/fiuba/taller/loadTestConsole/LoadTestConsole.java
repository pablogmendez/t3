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
	private Integer tasksQueuesListSize;
	private String function;
	private List<Integer> functionParamList;
	private UserPattern userPattern;
	
	private List<ExecutorService> downloadersThreadPoolList;
	
	private BlockingQueue<Result> resultQueue;
	private BlockingQueue<Stat> statsQueue;
	
	private List<Pair<BlockingQueue<UserTask>,BlockingQueue<UserTask>>> usersQueuesList;
	private TerminateSignal terminateSignal;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public LoadTestConsole(TerminateSignal terminateSignal) {
		this.terminateSignal = terminateSignal;
		ConfigLoader.getInstance().init(Constants.PROPERTIES_FILE);
		usersQueuesList = new ArrayList<Pair<BlockingQueue<UserTask>,BlockingQueue<UserTask>>>();
		
		//useresQueue = new ArrayBlockingQueue(ConfigLoader.getInstance().getUsersQueueSize());
		function = ConfigLoader.getInstance().getFunction();
		functionParamList = ConfigLoader.getInstance().getFunctionPatternParam();
		maxSizeUserPoolThread = ConfigLoader.getInstance().getMaxsizeUserPoolThread();
		
		// TODO: Arreglar esto
		if (function.equals("constant")) {
			userPattern = new ConstantUserPattern(functionParamList);
		} else if (function.equals("stairs")) {
			userPattern = new StairsUserPattern(functionParamList);
		} else if (function.equals("ramp")) {
			userPattern = new RampUserPattern(functionParamList);
		}
		
	}

	public void run() {
		ArrayBlockingQueue<UserTask> userTaskPendingQueue;
		ArrayBlockingQueue<UserTask> userTaskFinishedQueue;
		Integer currentUsers = 0, deltaUseres = 0, tick = 0, userNumber = 0;
		UserTask userTask;
		
		logger.info("Se inicia una nueva instancia de LoadTestConsole");		
		logger.info("Creando el pool de threads de usuarios");
		ExecutorService usersThreadPool = Executors.newFixedThreadPool(maxSizeUserPoolThread);
		try {
				while(!terminateSignal.hasTerminate()) {
					deltaUseres = userPattern.getUsers(tick) - currentUsers;
					logger.info("Se inicia el pulso: " + tick);
					logger.info("Cantidad de usuarios actualmente corriendo: " + currentUsers);
					logger.info("Cantidad de usuarios que deben correr en este pulso: " + userPattern.getUsers(tick));
					logger.info("Cantidad de usuarios que deben ingresar: " + deltaUseres);
					for(int i = 0; i < deltaUseres; i++) {
						currentUsers++;
						logger.info("Creando el usuario: " + currentUsers);
						// Creo las colas para el nuevo usuario
						userTaskPendingQueue = new ArrayBlockingQueue<UserTask>(ConfigLoader.getInstance().getTasksQueueSize());
						userTaskFinishedQueue = new ArrayBlockingQueue<UserTask>(ConfigLoader.getInstance().getTasksQueueSize());
						
						// Me guardo las dos colas en la lista de colas de usuarios
						usersQueuesList.add(new Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>>(userTaskPendingQueue, userTaskFinishedQueue));
						
						// Creo el usuario y les paso las colas para que puedan insertar tareas
						usersThreadPool.submit(new User(userTaskPendingQueue, userTaskFinishedQueue));
						logger.info("Usuario creado");
					}
					logger.info("Usuarios creados");
					
					userNumber = 0;
					
					// Despierto a los users enviandoles un mensaje para que se pongan a trabajar
					logger.info("Despertando los usuarios para un nuevo pulso");
					for(Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
						logger.info("Despertando al usuario: " + userNumber);
							pair.getFirst().put(new UserTask(Constants.DEFAULT_ID, Constants.TASK_STATUS.SUBMITTED));
		
						++userNumber;
					}
					
					userNumber = 0;
					
					logger.info("Me quedo esperando a que los usuarios terminen sus tareas");
					for(Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
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
				for(Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
					logger.info("Desconectando al usuario: " + userNumber);
					pair.getFirst().put(new UserTask(Constants.DISCONNECT_ID, Constants.TASK_STATUS.SUBMITTED));
					++userNumber;
				}
				
				userNumber = 0;
				logger.info("Me quedo esperando a que los usuarios se desconecten");
				for(Pair<BlockingQueue<UserTask>, BlockingQueue<UserTask>> pair : usersQueuesList) {
					logger.info("Esperando al usuario: " + userNumber);
					do {
						userTask = pair.getSecond().take();
					}while(userTask.getId() != Constants.DISCONNECT_ID);
					logger.info("Usuario: " + userNumber + " desconectado");
					++userNumber;
				}
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
