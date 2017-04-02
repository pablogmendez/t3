package ar.fiuba.taller.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import ar.fiuba.taller.common.Constants.COMMAND;

@SuppressWarnings("serial")
public class Command implements Serializable, ISerialize {

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

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput objOut = new ObjectOutputStream(os);

		objOut.writeObject(this);
		byte byteForm[] = os.toByteArray();
		objOut.close();
		os.close();
		return byteForm;
	}
	
	public void deserialize(byte[] byteForm) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(byteForm);
		ObjectInput objIn = new ObjectInputStream(is);
		Command tmp;
		tmp = (Command) objIn.readObject();
        objIn.close();
        is.close();
        uuid = tmp.getUuid();
        command = tmp.getCommand();
        user = tmp.getUser();
        message = tmp.getMessage();
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
