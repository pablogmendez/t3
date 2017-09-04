package ar.fiuba.taller.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class RemoteQueue {

	public abstract void close() throws IOException, TimeoutException;
	
}
