package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class Terminator extends  Thread {
	private Thread loadTestConsoleThread;
	private TerminateSignal terminateSignal;
	final static Logger logger = Logger.getLogger(App.class);
	
	public Terminator(Thread loadTestConsoleThread, TerminateSignal terminateSignal) {
		this.loadTestConsoleThread = loadTestConsoleThread;
		this.terminateSignal = terminateSignal;
	}
	
	public void run() {
		try {
			logger.info("Terminando la simulacion...");
    		terminateSignal.terminate();
			loadTestConsoleThread.join();
			logger.info("Simulacion terminada.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
