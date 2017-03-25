package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.loadTestConsole.Constants.TASK_STATUS;

public class StatTask extends Task {
	private Integer usersAmount;
	private Boolean sucessfullRequest;
	private long timeElapsed;
	
	
	public Integer getUsersAmount() {
		return usersAmount;
	}


	public void setUsersAmount(Integer usersAmount) {
		this.usersAmount = usersAmount;
	}


	public Boolean getSucessfullRequest() {
		return sucessfullRequest;
	}


	public void setSucessfullRequest(Boolean sucessfullRequest) {
		this.sucessfullRequest = sucessfullRequest;
	}


	public long getTimeElapsed() {
		return timeElapsed;
	}


	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}


	public StatTask(Integer id, TASK_STATUS status, Integer usersAmount, 
			Boolean sucessfullRequest, long timeElapsed) {
		super(id, status);
		this.usersAmount = usersAmount;
		this.sucessfullRequest = sucessfullRequest;
		this.timeElapsed = timeElapsed;
	}

}
