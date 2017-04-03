package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class CreateController implements Runnable {
	BlockingQueue<Command> createQueue;
	int shardingFactor;

	public CreateController(BlockingQueue<Command> createQueue, int shardingFactor) {
		super();
		this.createQueue = createQueue;
		this.shardingFactor = shardingFactor;
	}



	public void run() {
		// TODO Auto-generated method stub

	}

}
