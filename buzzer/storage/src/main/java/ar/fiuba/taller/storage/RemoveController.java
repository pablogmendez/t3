package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class RemoveController implements Runnable {
	BlockingQueue<Command> removeQueue;
	int shardingFactor;
	
	public RemoveController(BlockingQueue<Command> removeQueue, int shardingFactor) {
		super();
		this.removeQueue = removeQueue;
		this.shardingFactor = shardingFactor;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
