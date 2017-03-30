package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.BlockingQueue;

public class CommandController implements Runnable {
	BlockingQueue<Command> commandQueue;
	
	public CommandController(BlockingQueue<Command> commandQueue) {
		this.commandQueue = commandQueue;
	}

	public void run() {
		Command command;
		while(true) {
			try {
				command = commandQueue.take();
				switch(command.getCommand()) {
				
				case PUBLISH: {
					break;
				}
				case QUERY:   {
					break;						
				}
				case DELETE:  {
					break;						
				}
				case FOLLOW:  {
					break;						
				}
				default:
					break;
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
