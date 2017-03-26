package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class SummaryPrinter implements Runnable {

	Summary summary;
	TerminateSignal terminateSignal;
	TerminateSignal globalTerminateSignal;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public SummaryPrinter(Summary summary, TerminateSignal terminateSignal, TerminateSignal globalTerminateSignal) {
		super();
		this.summary = summary;
		this.terminateSignal = terminateSignal;
		this.globalTerminateSignal = globalTerminateSignal;
	}

	public void run() {
		logger.info("Iniciando SummaryPrinter");
		final String ANSI_CLS = "\u001b[2J";
		final String ANSI_HOME = "\u001b[H";
		while(!terminateSignal.hasTerminate()) {
			if(!globalTerminateSignal.hasTerminate()) {
				// Limpio la pantalla	       
		        System.out.print(ANSI_CLS + ANSI_HOME);
		        System.out.flush();
				
				// Imprimo el resumen
				System.out.println("Load Test Console: Resumen de ejecucion");
				System.out.println("---------------------------------------");
				System.out.println("Tiempo de descarga promedio...: " + summary.getAverageTime() + " ns");
				System.out.println("Requests exitosos.............: " + summary.getSuccessfullrequest() + "/" + summary.getTotalRequests());
				System.out.println("Requests fallidos.............: " + summary.getFailedrequest() + "/" + summary.getTotalRequests());
				System.out.println("Cantidad de usuarios..........: " + summary.getUsers());
				System.out.println("");
				System.out.println("Presione ^C para terminar...");
			}
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
