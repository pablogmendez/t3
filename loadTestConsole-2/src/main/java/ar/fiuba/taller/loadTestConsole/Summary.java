package ar.fiuba.taller.loadTestConsole;

public class Summary {
	private long totalTime;
	private Integer successfullrequest;
	private Integer failedrequest;
	private Integer users;

	public Summary() {
		super();
		totalTime = 0;
		successfullrequest = 0;
		failedrequest = 0;
		users = 0;
	}

	public synchronized Integer getSuccessfullrequest() {
		return successfullrequest;
	}

	public synchronized void setSuccessfullrequest(Integer successfullrequest) {
		this.successfullrequest = successfullrequest;
	}

	public synchronized Integer getFailedrequest() {
		return failedrequest;
	}

	public synchronized void setFailedrequest(Integer failedrequest) {
		this.failedrequest = failedrequest;
	}

	public synchronized Integer getUsers() {
		return users;
	}

	public synchronized void setUsers(Integer users) {
		this.users = users;
	}

	public synchronized long getAverageTime() {
		if (successfullrequest > 0) {
			return totalTime / successfullrequest;
		}
		return 0;
	}

	public synchronized void addTime(long time) {
		totalTime += time;
	}

	public synchronized Integer getTotalRequests() {
		return successfullrequest + failedrequest;
	}
}
