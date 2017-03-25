package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class Monitor implements Runnable {

	Report report;
	TerminateSignal terminateSignal;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public Monitor(Report report, TerminateSignal terminateSignal) {
		super();
		this.report = report;
		this.terminateSignal = terminateSignal;
	}

	public void run() {
		logger.info("Iniciando Monitor");
		while(!terminateSignal.hasTerminate()) {
			
			try{
			    PrintWriter writer = new PrintWriter(Constants.REPORT_FILE, "UTF-8");
			    writer.println("Load Test Console: Monitor de reportes");
			    writer.println("--------------------------------------");
			    writer.println("URLs analizadas....................: " + report.getAnalyzedUrl());
			    writer.println("SCRIPTS descargados................: " + report.getDownloadedScripts());
			    writer.println("LINKS descargados..................: " + report.getDownloadedLinks());
			    writer.println("IMGs descargadas...................: " + report.getDownloadedImages());
			    writer.println("Hilos ejecutando script............: " + report.getExecutionScriptThreads());
			    writer.println("Hilos descargando recurso..........: " + report.getDownloadResourceThreads());
			    writer.close();
			} catch (IOException e) {
			   // do something
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Finalizando Monitor");	
	}

}
