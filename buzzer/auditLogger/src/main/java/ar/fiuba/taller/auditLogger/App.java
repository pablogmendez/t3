package ar.fiuba.taller.auditLogger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.RemoteQueue;

public class App {
	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure(Constants.LOGGER_CONF);
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		try {
			ConfigLoader.getInstance().init(Constants.CONF_FILE);
			logger.info("Conectando a la cola remota loggerQueue");
			RemoteQueue loggerQueue = new RemoteQueue(
					ConfigLoader.getInstance().getAuditLoggerQueueName(),
					ConfigLoader.getInstance().getAuditLoggerQueueHost());
			loggerQueue.init();
			Thread auditLoggerThread = new Thread(new AuditLogger(loggerQueue));
			logger.info("Disparando el audit logger");
			auditLoggerThread.start();
			auditLoggerThread.join();
		} catch (InterruptedException e) {
			logger.error("Error al joinear el audit logger");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			logger.error("Error iniciar la cola remota");
			logger.info(e.toString());
			e.printStackTrace();
			throw new Exception();
		}
	}
}
