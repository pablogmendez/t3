package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class RemoveController implements Runnable {
	private BlockingQueue<Command> removeQueue;
	
	public RemoveController(BlockingQueue<Command> removeQueue) {
		super();
		this.removeQueue 	= removeQueue;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
