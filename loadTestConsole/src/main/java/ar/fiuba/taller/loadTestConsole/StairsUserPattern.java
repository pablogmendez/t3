package ar.fiuba.taller.loadTestConsole;

import java.util.List;

public class StairsUserPattern extends UserPattern {
	
	private Integer stepLength;
	
	private Integer ticksLeft;
	
	private Integer heightOfStep;
	
	public StairsUserPattern(List<Integer> paramList) {
		super(paramList.get(0), paramList.get(0));
		this.setStepLength(paramList.get(1));
		this.setTicksLeft(paramList.get(1));
		this.setHeightOfStep(1);
	}

	@Override
	public Integer getUsers(Integer tick) {
		if(getTicksLeft() == 0) {
			setTicksLeft(stepLength);
			setHeightOfStep(getHeightOfStep() + 1);
		}
		setTicksLeft(getTicksLeft() - 1);
		return getNumberOfUsers()*getHeightOfStep() <= getUpperBound() ? getNumberOfUsers()*tick : getUpperBound();
	}

	public Integer getStepLength() {
		return stepLength;
	}

	public void setStepLength(Integer stepLength) {
		this.stepLength = stepLength;
	}

	public Integer getTicksLeft() {
		return ticksLeft;
	}

	public void setTicksLeft(Integer ticksLeft) {
		this.ticksLeft = ticksLeft;
	}

	public Integer getHeightOfStep() {
		return heightOfStep;
	}

	public void setHeightOfStep(Integer heightOfStep) {
		this.heightOfStep = heightOfStep;
	}
}
