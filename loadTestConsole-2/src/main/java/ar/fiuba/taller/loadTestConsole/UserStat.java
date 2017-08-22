package ar.fiuba.taller.loadTestConsole;

public class UserStat extends SummaryStat {
	private int usersAmount;
	
	@Override
	public void updateSumary(Summary summary) {
		summary.setUsers(usersAmount);

	}

	public UserStat(int usersAmount) {
		super();
		this.usersAmount = usersAmount;
	}

}
