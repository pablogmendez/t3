package ar.fiuba.taller.common;

import java.io.IOException;

public interface ISerialize {

	public byte[] serialize() throws IOException;
	
	public void deserialize(byte[] byteForm) throws IOException, ClassNotFoundException;
	
}
