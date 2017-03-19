package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.BlockingQueue;

public class UsersControl implements Runnable {
	
	private BlockingQueue useresQueue = null;
	private int simulationTime = 0;
	
	
	public UsersControl (BlockingQueue colaUsuarios) {
		this.useresQueue = colaUsuarios;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
