package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;

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
			System.out.println("Load Test Console: Monitor de reportes");
			System.out.println("--------------------------------------");
			System.out.println("URLs analizadas....................: " + report.getAnalyzedUrl());
			System.out.println("SCRIPTS descargados................: " + report.getDownloadedScripts());
			System.out.println("LINKS descargados..................: " + report.getDownloadedLinks());
			System.out.println("IMGs descargadas...................: " + report.getDownloadedImages());
			System.out.println("Hilos ejecutando script............: " + report.getExecutionScriptThreads());
			System.out.println("Hilos descargando recurso..........: " + report.getDownloadResourceThreads());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Finalizando Monitor");	
	}

}
