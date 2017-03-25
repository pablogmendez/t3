package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class SummaryPrinter implements Runnable {

	Summary summary;
	TerminateSignal terminateSignal;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public SummaryPrinter(Summary summary, TerminateSignal terminateSignal) {
		super();
		this.summary = summary;
		this.terminateSignal = terminateSignal;
	}

	public void run() {
		logger.info("Iniciando SummaryPrinter");
		while(!terminateSignal.hasTerminate()) {
			try {
				Runtime.getRuntime().exec("clear");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Load Test Console: Resumen de ejecucion");
			System.out.println("---------------------------------------");
			System.out.println("Tiempo de descarga promedio...: " + summary.getAverageTime());
			System.out.println("Requests exitosos.............: " + summary.getSuccessfullrequest() + "/" + summary.getTotalRequests());
			System.out.println("Requests fallidos.............: " + summary.getFailedrequest() + "/" + summary.getTotalRequests());
			System.out.println("Cantidad de usuarios..........: " + summary.getUsers());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Finalizando SummaryPrinter");	
	}
}
