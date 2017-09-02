package ar.fiuba.taller.loadTestConsole;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class ReportPrinter implements Runnable {

	private Report report;
	private Map<String, String> propertiesMap;
	final static Logger logger = Logger.getLogger(ReportPrinter.class);

	public ReportPrinter(Report report, Map<String, String> propertiesMap) {
		this.report = report;
		this.propertiesMap = propertiesMap;
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	public void run() {
		logger.info("Iniciando Monitor");
		while (!Thread.interrupted()) {
			try {
				PrintWriter writer = new PrintWriter(
						propertiesMap.get(Constants.REPORT_FILE), "UTF-8");
				writer.printf(
						"Load Test Console: Monitor de reportes%n--------------------------------------%nURLs analizadas....................: %d%nSCRIPTS descargados................: %d%nLINKS descargados..................: %d%nIMGs descargadas...................: %d%nHilos ejecutando script............: %d%nHilos descargando recurso..........: %d%n",
						report.getAnalyzedUrl(), report.getDownloadedScripts(),
						report.getDownloadedLinks(),
						report.getDownloadedImages(),
						report.getExecutionScriptThreads(),
						report.getDownloadResourceThreads());
				writer.close();
				Thread.sleep((Integer
						.parseInt(propertiesMap.get(Constants.REPORT_TIMEOUT))
						* Constants.SLEEP_UNIT)/2);
			} catch (IOException e) {
				logger.error("No se pudo abrir el report file");
			} catch (NumberFormatException e) {
				logger.error("Tiempo del sleep mal seteado");
			} catch (InterruptedException e) {
				logger.info("Report interrumpido");
			}
		}
		logger.info("Finalizando Monitor");
	}
}
