package ar.fiuba.taller.auditLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ar.fiuba.taller.common.*;

public class AuditLogger {
	private Timestamp timestamp;
	private ReadingRemoteQueue loggerQueue;
	private Map<String, String> config;
	final static Logger logger = Logger.getLogger(AuditLogger.class);

	public AuditLogger(ReadingRemoteQueue loggerQueue,
			Map<String, String> config) {
		this.loggerQueue = loggerQueue;
		this.config = config;
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
		List<byte[]> messageList = null;
		Command command = new Command();
		PrintWriter pw = null;

		logger.info("Iniciando el audit logger");

		try {
			// Si no existe el archivo lo creo
			pw = new PrintWriter(config.get(Constants.AUDIT_LOG_FILE), "UTF-8");
			pw.close();

			// Lo abro para realizar append
			pw = new PrintWriter(new BufferedWriter(new FileWriter(
					config.get(Constants.AUDIT_LOG_FILE), true)));

			while (!Thread.interrupted()) {
				messageList = loggerQueue.pop();
				for (byte[] message : messageList) {
					try {
						command.deserialize(message);
						logger.info("Comando recibido: "
								+ getAuditLogEntry(command));
						pw.println(getAuditLogEntry(command));
						pw.flush();
					} catch (ClassNotFoundException | IOException e) {
						logger.error("No se ha podido deserializar el mensaje");
					}
				}
			}
		} catch (IOException e) {
			logger.error("No se ha podido abrir el archivo de log: " + e);
		}
		logger.info("Audit logger terminado");
	}

	private String getAuditLogEntry(Command command) {
		timestamp = new Timestamp(System.currentTimeMillis());
		return Constants.SDF.format(timestamp) + " - " + "UUID: "
				+ command.getUuid() + " - Usuario: " + command.getUser()
				+ " - Comando: " + command.getCommand() + " - Mensaje: "
				+ command.getMessage();
	}

}
