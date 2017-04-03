package ar.fiuba.taller.storage;

import java.util.concurrent.BlockingQueue;

import ar.fiuba.taller.common.Command;

public class RemoveController implements Runnable {
	private BlockingQueue<Command> removeQueue;
	private UserIndex userIndex;
	private HashtagIndex hashtagIndex;
	
	public RemoveController(BlockingQueue<Command> removeQueue, UserIndex userIndex, HashtagIndex hashtagIndex) {
		super();
		this.removeQueue 	= removeQueue;
		this.userIndex 		= userIndex;
		this.hashtagIndex 	= hashtagIndex;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
