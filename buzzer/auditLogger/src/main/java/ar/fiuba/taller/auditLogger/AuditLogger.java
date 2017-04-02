package ar.fiuba.taller.auditLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import ar.fiuba.taller.common.*;

public class AuditLogger extends DefaultConsumer implements Runnable {

	private DateFormat dateFormat;
	private PrintWriter pw;
	final static Logger logger = Logger.getLogger(App.class);
	
	public AuditLogger(RemoteQueue loggerQueue) {
		super(loggerQueue.getChannel());
		ConfigLoader.getInstance();
	}

	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	        
		logger.info("Iniciando el audit logger");

		try {
			logger.info("Cargando la configuracion");
			pw = new PrintWriter(Constants.AUDIT_LOG_FILE, "UTF-8");
		} catch (IOException e) {
			logger.error("Error al cargar la configuracion");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		Command command = new Command();
		try {
			command.deserialize(body);
			logger.info("Comando recibido con los siguientes parametros: " 
					+ "\nUsuario: " + command.getUser()
					+ "\nComando: " + command.getCommand()
					+ "\nMensaje: " + command.getMessage());
			logger.info("Escribiendo el mensaje en el archivo de log");
			pw.println(getAuditLogEntry(command));
		} catch (ClassNotFoundException e) {
			logger.error("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error al deserializar el comando");
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
	
	private String getAuditLogEntry(Command command) {
		Date date = new Date();
        return dateFormat.format(date) + " - " + "UUID: " + command.getUuid()
        + " - Comando: " + command.getUser()
		+ " - Comando: " + command.getCommand()
		+ " - Mensaje: " + command.getMessage();
	}

}
