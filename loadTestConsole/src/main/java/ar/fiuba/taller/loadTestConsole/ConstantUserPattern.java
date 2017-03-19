package ar.fiuba.taller.loadTestConsole;

public class ConstantUserPattern extends UserPattern {

	public ConstantUserPattern(Integer numberOfUseres, Integer upperBound) {
		super(numberOfUseres, upperBound);
	}

	@Override
	public Integer getUsers(Integer tick) {
		return getNumberOfUsers() <= getUpperBound() ? getNumberOfUsers() : getUpperBound();
	}

}
