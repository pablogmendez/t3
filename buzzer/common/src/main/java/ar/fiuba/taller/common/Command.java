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
	private String timestamp;
	
	public Command() {
		this.command = null;
		this.user = null;
		this.message = null;
		this.uuid = null;
		this.timestamp = null;
	}
	
	public Command(String command, String user, String message, UUID uuid, String timestamp) {
		this.command = Constants.COMMAND_MAP.get(command);
		this.user = user;
		this.message = message;
		this.uuid = uuid;
		this.timestamp = timestamp;
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
        timestamp = tmp.getTimestamp();
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String toJson() {
		String tmp;
		
		tmp = "{command:" + command.toString() +
				",user:" + user +  ",message:" + message + ",timestamp:" +
				timestamp + "}";
		return tmp;
	}
	
	public void fromJson(String jsonString) {
		
	}
}
