package ar.fiuba.taller.ClientConsole;

import java.util.UUID;

import ar.fiuba.taller.ClientConsole.Constants.COMMAND;

public class Command {

	private UUID uuid;
	private COMMAND command;
	private String user;
	private String message;
	
	public Command(String command, String user, String message, UUID uuid) {
		this.command = Constants.COMMAND_MAP.get(command);
		this.user = user;
		this.message = message;
		this.uuid = uuid;
	}

	public COMMAND getCommand() {
		return command;
	}
	
	public void setCommand(COMMAND command) {
		this.command = command;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
