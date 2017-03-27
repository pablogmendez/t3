package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.loadTestConsole.Constants.TASK_STATUS;

public class SummaryTask extends Task {
	private Integer usersAmount;
	private Boolean successfullRequest;
	private long timeElapsed;

	public Integer getUsersAmount() {
		return usersAmount;
	}

	public void setUsersAmount(Integer usersAmount) {
		this.usersAmount = usersAmount;
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public SummaryTask(Integer id, TASK_STATUS status, Integer usersAmount,
			Boolean successfullRequest, long timeElapsed) {
		super(id, status);
		this.usersAmount = usersAmount;
		this.successfullRequest = successfullRequest;
		this.timeElapsed = timeElapsed;
	}

	public Boolean getSuccessfullRequest() {
		return successfullRequest;
	}

	public void setSuccessfullRequest(Boolean successfullRequest) {
		this.successfullRequest = successfullRequest;
	}

}
