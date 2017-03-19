package ar.fiuba.taller.loadTestConsole;

public class RampUserPattern extends UserPattern {

	public RampUserPattern(Integer numberOfUseres, Integer upperBound) {
		super(numberOfUseres, upperBound);
	}

	@Override
	public Integer getUsers(Integer tick) {
		return getNumberOfUsers()*tick <= getUpperBound() ? getNumberOfUsers()*tick : getUpperBound();
	}

}
