package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.TerminateSignal;

public class App {
	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException {
		TerminateSignal terminateSignal = new TerminateSignal();
		LoadTestConsole loadTestConsole = new LoadTestConsole(terminateSignal);

		logger.info("[*] Se inicia una nueva instancia de LoadTestConsole");
		Runtime.getRuntime().addShutdownHook(
				new Terminator(terminateSignal));

		loadTestConsole.start();

		logger.info("[*] Finaliza la instancia de LoadTestConsole");

	}
}
