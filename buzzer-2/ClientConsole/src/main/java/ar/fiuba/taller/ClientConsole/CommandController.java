package ar.fiuba.taller.ClientConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.UUID;
import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;
import ar.fiuba.taller.common.WritingRemoteQueue;

public class CommandController {
	private WritingRemoteQueue dispatcherQueue;
	private int maxlengthMsg;
	private Timestamp timestamp;
	private String commandFile;

	public CommandController(
			WritingRemoteQueue dispatcherQueue, int maxlengthMsg,
			String commandFile) {
		this.dispatcherQueue = dispatcherQueue;
		this.maxlengthMsg = maxlengthMsg;
		this.commandFile = commandFile;
	}

	public void sendMessage(Command command) {
		try {
			if (command.getMessage().length() <= maxlengthMsg) {
				command.setUuid(UUID.randomUUID());
				timestamp = new Timestamp(System.currentTimeMillis());
				command.setTimestamp(Constants.SDF.format(timestamp));
				dispatcherQueue.push(command);
				try (PrintWriter pw = new PrintWriter(new BufferedWriter(
						new FileWriter(commandFile, true)))) {
					pw.printf(
							"Evento enviado - UUID: {%s} - Timestamp: {%s} - Comando: {%s} - Mensaje: {%s}%n-----------------------------------------------------%n",
							command.getUuid(), command.getTimestamp(),
							command.getCommand(), command.getMessage());
					System.out.printf(
							"Comando enviado - UUID: {%s} - Comando: {%s} - Usuario: {%s} - Mensaje: {%s} - Timestamp: {%s}",
							command.getUuid().toString(),
							command.getCommand().toString(),
							command.getUser(), command.getMessage(),
							command.getTimestamp());
				} catch(IOException e) {
					System.out.printf("No ha sido posible abrir el archivo de impresion de comandos: " + e);
				}
			} else {
				System.out.printf(
						"El mensaje contiene mas de 141 caracteres");
			}
		} catch (IOException e) {
			System.out.printf("Error al enviar el mensaje al dispatcher");
		}
	}
}
