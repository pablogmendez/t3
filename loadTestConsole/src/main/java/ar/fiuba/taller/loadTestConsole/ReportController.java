package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

public class ReportController implements Runnable {

	ArrayBlockingQueue<ReportTask> pendingReportQueue;
	ArrayBlockingQueue<ReportTask> finishedReportQueue;
	Report report;
	final static Logger logger = Logger.getLogger(App.class);
	
	public ReportController(ArrayBlockingQueue<ReportTask> pendingReportQueue, ArrayBlockingQueue<ReportTask> finishedReportQueue, Report report) {
		super();
		this.pendingReportQueue = pendingReportQueue;
		this.finishedReportQueue = finishedReportQueue;
		this.report = report;
	}
	
	public void run() {
		logger.info("Se inicia el report controller");
		ReportTask reportTask = null;
		// 
		try {
			do {
				reportTask = pendingReportQueue.take();
				logger.info("Estadistica recibida:\n" + "Id: " + reportTask.getId() + "\nStatus: " + reportTask.getStatus() + 
						"\nAnalyzer: " + reportTask.getAnalyzer() + "\nResource: " + reportTask.getResource());
				if(reportTask.getId() != Constants.DISCONNECT_ID) {
					// Actualizo el reporte
					if(reportTask.getAnalyzer()) { // Es una task enviada por un user
						if(reportTask.getStatus() == Constants.TASK_STATUS.SUBMITTED) {
							report.incExecutionScriptThreads();
						}
						else if(reportTask.getStatus() == Constants.TASK_STATUS.EXECUTING) {
							report.incAnalyzedUrl();
						}
						else {
							report.decExecutionScriptThreads();
						}
					}
					else { // Es una task enviada por un downloader
						if(reportTask.getStatus() == Constants.TASK_STATUS.SUBMITTED) {
							report.incDownloadResourceThreads();
						}						
						else if(reportTask.getStatus() == Constants.TASK_STATUS.EXECUTING) {
							if(reportTask.getResource().equals(Constants.SCRIPT_TAG)) {
								report.incDownloadedScripts();
							}
							else if(reportTask.getResource().equals(Constants.LINK_TAG)) {
								report.incDownloadedLinks();
							}
							else if(reportTask.getResource().equals(Constants.IMG_TAG)) {
								report.incDownloadedImages();
							}
						}
						else {
							report.decDownloadResourceThreads();
						}
					}
					logger.info("Reporte actualizado");
				}
			} while(reportTask.getId() == Constants.DISCONNECT_ID);
			logger.info("Finalizando el controller. Enviando mensje de finalizacion al control principal.");
			finishedReportQueue.put(reportTask);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Controller finalizado.");
	}

}
