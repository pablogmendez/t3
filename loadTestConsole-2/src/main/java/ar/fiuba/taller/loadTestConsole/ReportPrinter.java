package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class ReportPrinter implements Runnable {

	Report report;

	final static Logger logger = Logger.getLogger(Main.class);

	public ReportPrinter(Report report) {
		super();
		this.report = report;
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	public void run() {
		logger.info("Iniciando Monitor");
		while (!Thread.interrupted()) {

			try {
				PrintWriter writer = new PrintWriter(Constants.REPORT_FILE,
						"UTF-8");
				writer.println("Load Test Console: Monitor de reportes");
				writer.println("--------------------------------------");
				writer.println("URLs analizadas....................: "
						+ report.getAnalyzedUrl());
				writer.println("SCRIPTS descargados................: "
						+ report.getDownloadedScripts());
				writer.println("LINKS descargados..................: "
						+ report.getDownloadedLinks());
				writer.println("IMGs descargadas...................: "
						+ report.getDownloadedImages());
				writer.println("Hilos ejecutando script............: "
						+ report.getExecutionScriptThreads());
				writer.println("Hilos descargando recurso..........: "
						+ report.getDownloadResourceThreads());
				writer.close();
			} catch (IOException e) {
				// do something
			}
		}
		logger.info("Finalizando Monitor");
	}

}
