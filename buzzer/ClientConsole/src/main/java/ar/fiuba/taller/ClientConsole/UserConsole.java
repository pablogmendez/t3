package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UserConsole implements Runnable {

	public UserConsole() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		BlockingQueue<Command> commandQueue = new ArrayBlockingQueue<Command>(Constants.COMMAND_QUEUE_SIZE);
        Thread commandReaderThread = new Thread(new ScriptReader(commandQueue));
        Thread commandControllerThread = new Thread(new CommandController(commandQueue));
        
        try {
        	commandReaderThread.join();
			commandControllerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
