package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class Terminator extends Thread {
	private TerminateSignal terminateSignal;
	final static Logger logger = Logger.getLogger(Terminator.class);

	public Terminator(TerminateSignal terminateSignal) {
		this.terminateSignal = terminateSignal;
	}

	public void run() {
		try {
			TerminateSignal dotSignal = new TerminateSignal();
			Thread dotPrinterThread = new Thread(new DotPrinter(dotSignal));
			logger.info("Finalizando la simulacion...");
			System.out.print("Finalizando la simulacion");
			dotPrinterThread.start();
			terminateSignal.terminate();
			dotSignal.terminate();
			dotPrinterThread.join();
			logger.info("Simulacion Finalizada");
			System.out.println("");
			System.out.println("Simulacion Finalizada");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
