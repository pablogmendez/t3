package ar.fiuba.taller.loadTestConsole;

import java.util.List;

public class RampUserPattern extends UserPattern {

	public RampUserPattern(List<Integer> paramList) {
		super(paramList.get(0), paramList.get(0));
	}

	@Override
	public Integer getUsers(Integer tick) {
		return getNumberOfUsers()*tick <= getUpperBound() ? getNumberOfUsers()*tick : getUpperBound();
	}

}
