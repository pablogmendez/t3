package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

public class Downloader implements Runnable {

	private ArrayBlockingQueue<Task> taskQueue;
	
	final static Logger logger = Logger.getLogger(App.class);
	
	public Downloader(ArrayBlockingQueue<Task> tasksQueueList) {
		this.taskQueue = tasksQueueList;
	}
	
	public void run() {
		logger.info("Estoy en el downloader");

	}

}
