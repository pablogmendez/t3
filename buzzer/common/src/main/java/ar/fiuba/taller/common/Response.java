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

import ar.fiuba.taller.common.Constants.RESPONSE_STATUS;

public class Response implements Serializable {

	private UUID uuid;
	private RESPONSE_STATUS response_status;
	private String user;
	
	public Response(UUID uuid, RESPONSE_STATUS response_status, String user) {
		super();
		this.uuid = uuid;
		this.response_status = response_status;
		this.user = user;
	}

	public Response() {
		super();
		this.uuid = null;
		this.response_status = null;
		this.user = null;
	}
	
	public byte[] serialize() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput objOut = new ObjectOutputStream(os);

		objOut.writeObject(this);
		byte responseArray[] = os.toByteArray();
		objOut.close();
		os.close();
		return responseArray;
	}
	
	public void deserialize(byte[] responseArray) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(responseArray);
		ObjectInput objIn = new ObjectInputStream(is);
		Response tmp;
		tmp = (Response) objIn.readObject();
        objIn.close();
        is.close();
        uuid = tmp.getUuid();
        response_status = tmp.getResponse_status();
        user = tmp.getUser();
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public RESPONSE_STATUS getResponse_status() {
		return response_status;
	}

	public void setResponse_status(RESPONSE_STATUS response_status) {
		this.response_status = response_status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}