package ar.fiuba.taller.loadTestConsole;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class UsersController implements Runnable {

	final static Logger logger = Logger.getLogger(UsersController.class);
	int maxUsers;
	int maxDownloaders;
	Map<Integer, Integer> usersPatternMap;
	
	public UsersController(int maxUsers, int maxDownloaders,
			Map<Integer, Integer> usersPatternMap) {
		super();
		this.maxUsers = maxUsers;
		this.maxDownloaders = maxDownloaders;
		this.usersPatternMap = usersPatternMap;
	}
	
	@Override
	public void run() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));

	}

}
