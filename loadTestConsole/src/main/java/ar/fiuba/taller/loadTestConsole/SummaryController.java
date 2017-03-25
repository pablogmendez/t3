package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

public class SummaryController implements Runnable {
	ArrayBlockingQueue<StatTask> pendingStatsQueue;
	ArrayBlockingQueue<StatTask> finishedStatsQueue;
	Summary summary;
	final static Logger logger = Logger.getLogger(App.class);
	
	public SummaryController(ArrayBlockingQueue<StatTask> pendingStatsQueue, ArrayBlockingQueue<StatTask> finishedStatsQueue, Summary summary) {
		super();
		this.pendingStatsQueue = pendingStatsQueue;
		this.finishedStatsQueue = finishedStatsQueue;
		this.summary = summary;
	}
	
	public void run() {
		logger.info("Se inicia el monitor");
		StatTask statTask = null;
		// 
		try {
			do {
				statTask = pendingStatsQueue.take();
				logger.info("Estadistica recibida:\n" + "Id: " + statTask.getId() + "\nStatus: " + statTask.getStatus() + 
						"\nTime elapsed: " + statTask.getTimeElapsed() + "\nAmount of users: " + statTask.getUsersAmount() + 
						"\nSuccessfull request: " + statTask.getSuccessfullRequest());
				if(statTask.getId() != Constants.DISCONNECT_ID) {
					// Actualizo las estadisticas
					// Actualizo la cantidad de usuarios
					if(statTask.getUsersAmount() != 0) {
						summary.setUsers(statTask.getUsersAmount());						
					}
					// Actualizo los requests
					if(statTask.getSuccessfullRequest()) {
						summary.setSuccessfullrequest(summary.getSuccessfullrequest() + 1);						
					}
					else {
						summary.setFailedrequest(summary.getFailedrequest() + 1);
					}
					// Actualizo el tiempo promedio
					summary.addTime(statTask.getTimeElapsed());
					logger.info("Estadisticas actualizadas");
				}
			} while(statTask.getId() == Constants.DISCONNECT_ID);
			logger.info("Finalizando el monitor. Enviando mensje de finalizacion al control principal.");
			finishedStatsQueue.put(statTask);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Monitor finalizado.");
	}
}
