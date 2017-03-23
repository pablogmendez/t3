package ar.fiuba.taller.loadTestConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class LoadTestConsole implements Runnable {
	
	//private BlockingQueue<Task> useresQueue;
	private Integer simulationTime;
	private Integer maxSizeUserPoolThread;
	private Integer tasksQueuesListSize;
	private String function;
	private List<Integer> functionParamList;
	private UserPattern userPattern;
	
	private List<ExecutorService> downloadersThreadPoolList;
	private List<BlockingQueue<Task>> tasksQueuesList;
	
	private BlockingQueue<Result> resultQueue;
	private BlockingQueue<Stat> statsQueue;
	
	
	
	final static Logger logger = Logger.getLogger(App.class);
	
	
	public LoadTestConsole() {
		ConfigLoader.getInstance().init(Constants.PROPERTIES_FILE);
		simulationTime = ConfigLoader.getInstance().getSimulationTime();
		//useresQueue = new ArrayBlockingQueue(ConfigLoader.getInstance().getUsersQueueSize());
		function = ConfigLoader.getInstance().getFunction();
		functionParamList = ConfigLoader.getInstance().getFunctionPatternParam();
		maxSizeUserPoolThread = ConfigLoader.getInstance().getMaxsizeUserPoolThread();
		tasksQueuesListSize = ConfigLoader.getInstance().getTasksQueuesListSize();
		
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

		Integer counter = 0;
		
		// Creo las colas de tareas para cada usuario
		for(int i = 0; i < maxSizeUserPoolThread; ++i) {
			tasksQueuesList.add(new ArrayBlockingQueue<Task>(tasksQueuesListSize));
		}
		
		// Creo el pool de threads de usuarios
		ExecutorService usersThreadPool = Executors.newFixedThreadPool(maxSizeUserPoolThread);
		
		// Inicializo la lista de pool de threads de downloaders
		downloadersThreadPoolList = new ArrayList<ExecutorService>();
		// Creo los pools de threads de los downloaders
		for(int i = 0; i < maxSizeUserPoolThread; ++i) {
			downloadersThreadPoolList.add(Executors.newFixedThreadPool(maxSizeUserPoolThread));
		}
		
		Integer aomuntOfUsers;
		while(true) {
			aomuntOfUsers = userPattern.getUsers(counter);
			for(int i = 0; i < aomuntOfUsers; i++) {
				// Creo los usuarios y les paso las colas para que puedan insertar tareas
				user				
			}

			
			// Creo el pool de downloaders y les paso las colas para que puedan tomar tareas 
			
		}
		
		
		
		
		List<Future<User>> futures = new ArrayList<Future<User>>();
		futures.add(usersThreadPool.submit(myRunnable));
		for (Future<?> future:futures) {
		    future.get();
		}
		
		usersThreadPool.submit(new User());
		usersThreadPool.shutdown();
		// Wait for everything to finish.
		try {
			while (!usersThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
			  logger.info("Awaiting completion of threads.");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Integer getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(Integer simulationTime) {
		this.simulationTime = simulationTime;
	}

//	public BlockingQueue getUseresQueue() {
//		return useresQueue;
//	}
//
//	public void setUseresQueue(BlockingQueue useresQueue) {
//		this.useresQueue = useresQueue;
//	}

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
