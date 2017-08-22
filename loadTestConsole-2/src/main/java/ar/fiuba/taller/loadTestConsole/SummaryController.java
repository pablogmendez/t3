package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

public class SummaryController implements Runnable {
	ArrayBlockingQueue<SummaryStat> summaryQueue;
	Summary summary;
	
	final static Logger logger = Logger.getLogger(SummaryController.class);

	public SummaryController(
			ArrayBlockingQueue<SummaryStat> summaryQueue,
			Summary summary) {
		super();
		this.summaryQueue = summaryQueue;
		this.summary = summary;
	}

	public void run() {
		logger.info("Se inicia el monitor");
		SummaryTask summaryTask = null;
		//
		try {
			do {
				summaryTask = pendingSummaryQueue.take();
				logger.info("Estadistica recibida:\n" + "Id: "
						+ summaryTask.getId() + "\nStatus: "
						+ summaryTask.getStatus() + "\nTime elapsed: "
						+ summaryTask.getTimeElapsed() + "\nAmount of users: "
						+ summaryTask.getUsersAmount()
						+ "\nSuccessfull request: "
						+ summaryTask.getSuccessfullRequest());
				if (summaryTask.getId() != Constants.DISCONNECT_ID) {
					// Actualizo las estadisticas
					// Actualizo la cantidad de usuarios
					if (summaryTask.getUsersAmount() != 0) {
						summary.setUsers(summaryTask.getUsersAmount());
					}
					// Actualizo los requests
					if (summaryTask.getSuccessfullRequest() != null) {
						if (summaryTask.getSuccessfullRequest()) {
							summary.setSuccessfullrequest(
									summary.getSuccessfullrequest() + 1);
						} else {
							summary.setFailedrequest(
									summary.getFailedrequest() + 1);
						}
					}
					// Actualizo el tiempo promedio
					summary.addTime(summaryTask.getTimeElapsed());
					logger.info("Estadisticas actualizadas");
				}
			} while (summaryTask.getId() != Constants.DISCONNECT_ID);
			logger.info(
					"Finalizando el monitor. Enviando mensje de finalizacion"
					+ " al control principal.");
			finishedSummaryQueue.put(summaryTask);
		} catch (InterruptedException e) {
			logger.warn("No se ha podido tomar la tarea de la pendingSummaryQueue");
			e.printStackTrace();
		}
		logger.info("Monitor finalizado.");
	}
}
