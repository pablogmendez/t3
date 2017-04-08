package ar.fiuba.taller.loadTestConsole;

import java.util.List;

public class StairsUserPattern extends UserPattern {

	private Integer stepLength;

	private Integer ticksLeft;

	private Integer heightOfStep;

	public StairsUserPattern(List<Integer> paramList, Integer upperBound) {
		super(paramList.get(0), upperBound);
		this.stepLength = paramList.get(1);
		this.ticksLeft = paramList.get(1);
		this.heightOfStep = paramList.get(0);
	}

	@Override
	public Integer getUsers(Integer tick) {
		if (ticksLeft == 0) {
			ticksLeft = stepLength;
			setNumberOfUsers(getNumberOfUsers() + heightOfStep);
		}
		ticksLeft--;
		return getNumberOfUsers() <= getUpperBound() ? getNumberOfUsers()
				: getUpperBound();
	}
}
