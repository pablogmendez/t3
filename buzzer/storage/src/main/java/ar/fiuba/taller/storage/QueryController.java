package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class QueryController implements Runnable {
	private BlockingQueue<Command> queryQueue;
	private UserIndex userIndex;
	private HashtagIndex hashtagIndex;

	public QueryController(BlockingQueue<Command> queryQueue, UserIndex userIndex, HashtagIndex hashtagIndex) {
		super();
		this.queryQueue		= queryQueue;
		this.userIndex 		= userIndex;
		this.hashtagIndex 	= hashtagIndex;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
