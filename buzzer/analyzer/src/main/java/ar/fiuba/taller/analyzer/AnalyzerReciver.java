package ar.fiuba.taller.analyzer;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ar.fiuba.taller.common.Response;

public class AnalyzerReciver implements Runnable {

	BlockingQueue<Response> responseQueue;
	final static Logger logger = Logger.getLogger(AnalyzerReciver.class);
	
	public AnalyzerReciver(BlockingQueue<Response> responseQueue) {
		this.responseQueue = responseQueue;
	}

	public void run() {

	}

}
