package ar.fiuba.taller.loadTestConsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
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
	private List<Future<User>> futures = null;
	
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
		this.futures = new ArrayList<>();
	}
	
	@Override
	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		int totalUsersCount = 0; // Usuarios totales corriendo
		int sleepTime = 0; 	  	// Tiempo a esperar
		int deltaUsers = 0;	  	// Usuarios que se deben agregar o quitar
		int oldTime = 0; 		// Tiempo del pulso anterior
		ExecutorService executorService = Executors.newFixedThreadPool(maxUsers);

		Iterator<Map.Entry<Integer, Integer>> it = usersPatternMap.entrySet().iterator();
		Map.Entry<Integer, Integer> pair;
		
		logger.info("Iniciando UsersController");
		
		if(it.hasNext()) {
			pair = it.next();
			deltaUsers = pair.getValue() - totalUsersCount;
			oldTime = pair.getKey();
		}
		
		try {
			while(!Thread.interrupted()) {
				logger.info("Usuarios corriendo en el pool: " + totalUsersCount);
				logger.info("Usuarios que se deben ingresar al pool: " + deltaUsers);
				totalUsersCount += updateUsers(totalUsersCount, deltaUsers, executorService);
				logger.info("Usuarios ingresados");
				if(it.hasNext()) {
					pair = it.next();
					if(pair.getKey() > oldTime) {
						sleepTime = pair.getKey() - oldTime;	
					}
					deltaUsers = pair.getValue() - totalUsersCount;
					oldTime = pair.getKey();
					patternTime.set(oldTime);
					logger.debug("Valores para la proxima corrida: " + pair.getKey() + " - " + pair.getValue());
				} else {
					deltaUsers = 0;
				}
				logger.info("Usuarios totales: " + totalUsersCount);
				summaryQueue.put(new UserStat(totalUsersCount));
				logger.info("Tiempo a dormir hasta el proximo pulso: " + sleepTime);
				Thread.sleep(sleepTime*Constants.SLEEP_UNIT);
			}
		} catch (InterruptedException e) {
			logger.info("Senial de interrupcion recibida. Eliminado los Usuarios.");
			executorService.shutdownNow();
		}
	}
	
	/* Actualiza el pool de threads con los usuarios pasados
	   Retorna la cantidad de usuarios agregados o eliminados */
	@SuppressWarnings("unchecked")
	private int updateUsers(int totalUsersCount, int deltaUsers, ExecutorService executorService) {
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
			// Disparo los users
			for(int i = 0; i < usersToAdd; i++) {
				futures.add((Future<User>) executorService.submit(new User(propertiesMap, summaryQueue, reportQueue)));
			}

		} else if (usersToAdd < 0) {
			logger.info("Eliminando usuarios");
			int tmpUsersToAdd = Math.abs(usersToAdd);
			Iterator<Future<User>> it = futures.iterator();
			Future<User> f;
			for(int i = 0; i < tmpUsersToAdd; i++) {
				f = it.next();
				f.cancel(true);
				try {
					Thread.sleep(100);
					f.cancel(true);
					Thread.sleep(100);
					f.cancel(true);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				it.remove();
				logger.debug("Usuario cancelado");
			}
		}
		return usersToAdd;
	}
}
