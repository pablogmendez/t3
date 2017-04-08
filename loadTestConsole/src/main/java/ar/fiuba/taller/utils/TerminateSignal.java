package ar.fiuba.taller.utils;

public class TerminateSignal {

	private boolean terminate = false;

	public boolean hasTerminate() {
		return this.terminate;
	}

	public void terminate() {
		this.terminate = true;
	}
}
