package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.loadTestConsole.Constants.REPORT_EVENT;

public class ReportController implements Runnable {

	private ArrayBlockingQueue<REPORT_EVENT> reportQueue;
	private Report report;
	final static Logger logger = Logger.getLogger(ReportController.class);

	public ReportController(ArrayBlockingQueue<REPORT_EVENT> reportQueue,
			Report report) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		this.reportQueue = reportQueue;
		this.report = report;
	}

	public void run() {
		logger.info("Se inicia el report controller");
		REPORT_EVENT reportEvent;
		
		while(!Thread.interrupted()) {
			try {
				reportEvent = reportQueue.take();
				switch (reportEvent) {
				case URL_ANALYZED:
					report.incAnalyzedUrl();
					break;
				case SCRIPT_DOWNLOADED:
					report.incDownloadedScripts();
					break;
				case LINK_DOWNLOADED:
					report.incDownloadedLinks();
					break;
				case IMG_DOWNLOADED:
					report.incDownloadedImages();
					break;
				case SCRIPT_EXECUTING:
					report.incExecutionScriptThreads();
					break;
				case SCRIPT_EXECUTED:
					report.decExecutionScriptThreads();
					break;
				case RESOURCE_DOWNLOAD:
					report.incDownloadResourceThreads();
					break;
				case RESOURCE_DOWNLOADED:
					report.decDownloadResourceThreads();
					break;
				default:
					break;
				}
			} catch (InterruptedException e) {
				logger.info("Report Controller interrumpido.");
			}
		}
		logger.info("Report Controller finalizado.");
	}
}
