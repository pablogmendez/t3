package ar.fiuba.taller.loadTestConsole;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class UsersControl implements Runnable {
	
	private BlockingQueue useresQueue;
	private Integer simulationTime;
	final static Logger logger = Logger.getLogger(App.class);
	
	
	public UsersControl (Integer simulationTime) {
		this.setSimulationTime(simulationTime);
	}

	public void run() {
		// TODO Auto-generated method stub

	}

	public Integer getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(Integer simulationTime) {
		this.simulationTime = simulationTime;
	}

}
