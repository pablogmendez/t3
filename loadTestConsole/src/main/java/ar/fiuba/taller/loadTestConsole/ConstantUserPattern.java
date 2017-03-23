package ar.fiuba.taller.loadTestConsole;

import java.util.List;

public class ConstantUserPattern extends UserPattern {
	
	public ConstantUserPattern(List<Integer> paramList) {
		
		super(paramList.get(0), paramList.get(0));
	}

	@Override
	public Integer getUsers(Integer tick) {
		return getNumberOfUsers() <= getUpperBound() ? getNumberOfUsers() : getUpperBound();
	}

}
