package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class QueryController implements Runnable {
	private BlockingQueue<Command> queryQueue;

	public QueryController(BlockingQueue<Command> queryQueue) {
		super();
		this.queryQueue		= queryQueue;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
