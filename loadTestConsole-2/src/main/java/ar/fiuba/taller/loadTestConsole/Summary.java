package ar.fiuba.taller.loadTestConsole;

public class Summary {
	private long avgDownloadTime;
	private long successfullrequest;
	private long failedrequest;
	private Integer users;

	public Summary() {
		avgDownloadTime = 0;
		successfullrequest = 0;
		failedrequest = 0;
		users = 0;
	}

	public synchronized long getSuccessfullrequest() {
		return successfullrequest;
	}

	public synchronized void incSuccessfullrequest(long count) {
		successfullrequest += count;
	}
	
	public synchronized long getFailedrequest() {
		return failedrequest;
	}

	public synchronized void incFailedrequest(long count) {
		failedrequest += count;
	}
	
	public synchronized Integer getUsers() {
		return users;
	}

	public synchronized void setUsers(Integer users) {
		this.users = users;
	}

	public synchronized long getAvgDownloadTime() {
		return avgDownloadTime;
	}

	public synchronized void updateAvgDownloadTime(long time) {
		avgDownloadTime = (avgDownloadTime + time)/2;
	}

	public synchronized long getTotalRequests() {
		return successfullrequest + failedrequest;
	}
}
