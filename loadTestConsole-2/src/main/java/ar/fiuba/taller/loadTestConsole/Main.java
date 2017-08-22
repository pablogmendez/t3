package ar.fiuba.taller.loadTestConsole;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class Main {
	final static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		
		LoadTestConsole loadTestConsole;
		try {
			loadTestConsole = new LoadTestConsole();
			logger.info("[*] Se inicia una nueva instancia de LoadTestConsole");			
			loadTestConsole.start();
			logger.info("[*] Finaliza la instancia de LoadTestConsole");
		} catch (Exception e) {
			System.exit(Constants.EXIT_FAILURE);
		}
		System.exit(Constants.EXIT_SUCCESS);
	}
}
