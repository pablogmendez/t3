package ar.fiuba.taller.loadTestConsole;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;


public class UsersController implements Runnable {

	final static Logger logger = Logger.getLogger(UsersController.class);
	private int maxUsers; 
	private Map<Integer, Integer> usersPatternMap;
	private Map<String, String> propertiesMap;
	private AtomicInteger patternTime;
	private ArrayBlockingQueue<SummaryStat> summaryQueue;
	private ArrayBlockingQueue<REPORT_EVENT> reportQueue;
	
	public UsersController(Map<String, String> propertiesMap,
			Map<Integer, Integer> usersPatternMap, AtomicInteger patternTime,
			ArrayBlockingQueue<SummaryStat> summaryQueue,
			ArrayBlockingQueue<REPORT_EVENT> reportQueue) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.propertiesMap = propertiesMap;
		this.maxUsers = Integer.parseInt(this.propertiesMap.get(Constants.MAX_USERS));
		this.usersPatternMap = usersPatternMap;
		this.patternTime = patternTime;
		this.summaryQueue = summaryQueue;
		this.reportQueue = reportQueue;
	}
	
	@Override
	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		int totalUsersCount = 0; // Usuarios totales corriendo
		int sleepTime = 0; 	  // Tiempo a esperar
		int deltaUsers = 0;	  // Usuarios que se deben agregar o quitar
		ExecutorService executorService = Executors.newFixedThreadPool(maxUsers);
		Set<Callable<User>> usersSet = new HashSet<Callable<User>>();
		List<Future<User>> futures = null;
		Iterator<Map.Entry<Integer, Integer>> it = usersPatternMap.entrySet().iterator();
		Map.Entry<Integer, Integer> pair;
		
		logger.info("Iniciando UsersController");
		
		if(it.hasNext()) {
			pair = it.next();
			deltaUsers = pair.getValue() - totalUsersCount;
		}
		
		try {
			while(!Thread.interrupted()) {
				logger.info("Usuarios corriendo en el pool: " + totalUsersCount);
				logger.info("Usuarios que se deben ingresar al pool: " + deltaUsers);
				totalUsersCount += updateUsers(totalUsersCount, deltaUsers, executorService, futures);
				if(it.hasNext()) {
					pair = it.next();
					sleepTime = pair.getKey();
					deltaUsers = totalUsersCount - pair.getValue();
				} else {
					deltaUsers = 0;
				}
				summaryQueue.put(new UserStat(totalUsersCount));
				logger.info("Tiempo a dormir hasta el proximo pulso: " + sleepTime);
				patternTime.set(sleepTime);
				Thread.sleep(sleepTime*Constants.SLEEP_UNIT);
			}
		} catch (InterruptedException e) {
			logger.info("Senial de interrupcion recibida. Eliminado los Usuarios.");
			executorService.shutdownNow();
		}
	}
	
	/* Actualiza el pool de threads con los usuarios pasados
	   Retorna la cantidad de usuarios agregados o eliminados */
	private int updateUsers(int totalUsersCount, int deltaUsers, ExecutorService executorService,
			List<Future<User>> futures) {
		int usersToAdd = 0;
		
		if(totalUsersCount + deltaUsers >= maxUsers) {
			usersToAdd = maxUsers - totalUsersCount;
		} else if (totalUsersCount + deltaUsers < 0) {
			usersToAdd = 0;
		} else {
			usersToAdd = deltaUsers;
		}
		
		logger.info("Usuarios a agregar: " + usersToAdd);
		if(usersToAdd > 0) {
			logger.info("Agregando usuarios");
			Set<Callable<User>> usersSet = new HashSet<Callable<User>>();
			for(int i = 0; i < usersToAdd; i++) {
				usersSet.add(new User(propertiesMap, summaryQueue, reportQueue));
			}
			// Disparo los users
			try {
				if(futures == null) {
						futures = executorService.invokeAll(usersSet);
				} else {				
					futures.addAll(executorService.invokeAll(usersSet));			
				}
			} catch (InterruptedException e) {
				// Do nothing
			}
		} else if (usersToAdd < 0) {
			logger.info("Eliminando usuarios");
			int tmpUsersToAdd = Math.abs(usersToAdd);
			for(int i = 0; i < tmpUsersToAdd; i++) {
				futures.remove(0).cancel(true);
			}
		}
		return usersToAdd;
	}
}
