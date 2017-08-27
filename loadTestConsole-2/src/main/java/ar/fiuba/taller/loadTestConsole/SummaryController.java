package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class SummaryController implements Runnable {
	final static Logger logger = Logger.getLogger(SummaryController.class);
	private ArrayBlockingQueue<SummaryStat> summaryQueue;
	private Summary summary;
	

	public SummaryController(ArrayBlockingQueue<SummaryStat> summaryQueue,
			Summary summary) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.summaryQueue = summaryQueue;
		this.summary = summary;
	}

	public void run() {
		logger.info("Se inicia el summary controller");
		SummaryStat summaryStat = null;
		while(!Thread.interrupted()) {
			try {
				summaryStat = summaryQueue.take();
			} catch (InterruptedException e) {
				logger.info("summary controller interrumpido.");
			}
			summaryStat.updateSumary(summary);
		}
		logger.info("summary controller finalizado.");
	}
}
