package ar.fiuba.taller.loadTestConsole;

public class TerminateSignal {

	private boolean terminate = false;

	public synchronized boolean hasTerminate(){
		return this.terminate;
	}

	public synchronized void terminate(){
		this.terminate = true;  
	}
}