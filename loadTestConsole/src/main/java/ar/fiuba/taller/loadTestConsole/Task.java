package ar.fiuba.taller.loadTestConsole;

public abstract class Task {
	
	private Integer id;
	private Constants.TASK_STATUS status;

	public Task(Integer id, Constants.TASK_STATUS status) {
		this.id = id;
		this.status = status;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Constants.TASK_STATUS getStatus() {
		return status;
	}

	public void setStatus(Constants.TASK_STATUS status) {
		this.status = status;
	}
	
}
