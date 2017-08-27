package ar.fiuba.taller.loadTestConsole;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class SummaryPrinter implements Runnable {
	private Summary summary;
	private Map<String, String> propertiesMap;
	final static Logger logger = Logger.getLogger(SummaryPrinter.class);

	public SummaryPrinter(Summary summary, Map<String, String> propertiesMap) {
		this.summary = summary;
		this.propertiesMap = propertiesMap;
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	public void run() {
		logger.info("Iniciando SummaryPrinter");
		final String ANSI_CLS = "\u001b[2J";
		final String ANSI_HOME = "\u001b[H";
		while(!Thread.interrupted()) {
				// Limpio la pantalla
				System.out.print(ANSI_CLS + ANSI_HOME);
				System.out.flush();

				// Imprimo el resumen
				System.out.printf("Load Test Console: Resumen de ejecucion%n---------------------------------------%nTiempo de descarga promedio...: %d ms%nRequests exitosos.............: %d / %d %nRequests fallidos.............: %d / %d %nCantidad de usuarios..........: %d%nPresione ^C para terminar...%n",
						summary.getAvgDownloadTime(), 
						summary.getSuccessfullrequest(),
						summary.getTotalRequests(),
						summary.getFailedrequest(),
						summary.getTotalRequests(),
						summary.getUsers());
				try {
					Thread.sleep(Long.parseLong(propertiesMap.get(Constants.SUMMARY_TIMEOUT))
							*Constants.SLEEP_UNIT);
				} catch (InterruptedException e) {
					logger.info("SummaryPrinter interrumpido");
				}
		}
		logger.info("SummaryPrinter finalizado");
	}
}

