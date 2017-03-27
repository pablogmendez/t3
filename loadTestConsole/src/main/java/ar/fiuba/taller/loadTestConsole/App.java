package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class App {
	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException {
		TerminateSignal terminateSignal = new TerminateSignal();
		Thread loadTestConsoleThread = new Thread(
				new LoadTestConsole(terminateSignal));

		logger.info("[*] Se inicia una nueva instancia de LoadTestConsole");
		Runtime.getRuntime().addShutdownHook(
				new Terminator(loadTestConsoleThread, terminateSignal));
		//
		loadTestConsoleThread.start();
		// Thread.sleep(1200000);
		// terminateSignal.terminate();
		//
		// System.out.println("Presione ^c para terminar.");

		logger.info("[*] Finaliza la instancia de LoadTestConsole");

	}
}
