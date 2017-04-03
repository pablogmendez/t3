package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class QueryController implements Runnable {
	BlockingQueue<Command> queryQueue;
	int shardingFactor;

	public QueryController(BlockingQueue<Command> queryQueue, int shardingFactor) {
		super();
		this.queryQueue = queryQueue;
		this.shardingFactor = shardingFactor;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
