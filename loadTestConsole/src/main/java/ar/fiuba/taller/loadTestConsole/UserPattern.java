package ar.fiuba.taller.loadTestConsole;

public abstract class UserPattern {

	private Integer numberOfUsers;
	private Integer upperBound;
	
	public UserPattern(Integer numberOfUseres, Integer upperBound) {
		this.setNumberOfUsers(numberOfUseres);
		this.setUpperBound(upperBound);
	}

	public abstract Integer getUsers(Integer tick);

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(Integer upperBound) {
		this.upperBound = upperBound;
	}
	
}
