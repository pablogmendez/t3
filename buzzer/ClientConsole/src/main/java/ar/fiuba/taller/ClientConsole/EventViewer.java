package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ar.fiuba.taller.common.Response;

public class EventViewer implements Runnable {
	BlockingQueue<Response> responseQueue;
	String username;
	String eventFile;
	final static Logger logger = Logger.getLogger(App.class);
	
	public EventViewer(BlockingQueue<Response> responseQueue, String username, String eventFile) {
		this.responseQueue = responseQueue;
		this.username = username;
		this.eventFile = eventFile;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

}
