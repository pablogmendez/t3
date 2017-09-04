package ar.fiuba.taller.auditLogger;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import ar.fiuba.taller.common.ConfigLoader;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.ReadingRemoteQueue;

public class MainAuditLogger {
	final static Logger logger = Logger.getLogger(MainAuditLogger.class);

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("/home/pablo/Escritorio/t3/buzzer-2/auditLogger/conf/log4j.properties");
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		ConfigLoader configLoader = null;
		
		try {
			configLoader = new ConfigLoader(Constants.CONF_FILE);
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			System.exit(Constants.EXIT_FAILURE);
		}
		
		final ReadingRemoteQueue loggerQueue = new ReadingRemoteQueue(
				configLoader.getProperties().get(Constants.AUDIT_LOGGER_QUEUE_NAME),
				configLoader.getProperties().get(Constants.AUDIT_LOGGER_QUEUE_HOST),
				configLoader.getProperties());
		
		final Thread auditLoggerThread = new Thread(new AuditLogger(loggerQueue, configLoader.getProperties()));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			  @Override
			  public void run() {
				  loggerQueue.shutDown();
				  auditLoggerThread.interrupt();
				  try {
					  auditLoggerThread.join(Constants.AUDIT_LOGGER_THREAD_WAIT_TIME);
				} catch (InterruptedException e) {
					// Do nothing
				}
			  }
			});
		
		auditLoggerThread.start();
	}
}
