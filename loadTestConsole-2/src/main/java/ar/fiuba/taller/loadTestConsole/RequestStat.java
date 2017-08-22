package ar.fiuba.taller.loadTestConsole;

public class RequestStat extends SummaryStat {
	private Boolean successfullRequest;
	private long timeElapsed;
	
	public RequestStat(Boolean successfullRequest, long timeElapsed) {
		super();
		this.successfullRequest = successfullRequest;
		this.timeElapsed = timeElapsed;
	}

	@Override
	public void updateSumary(Summary summary) { 
		if(successfullRequest) {
			summary.incSuccessfullrequest();
		}
		else {
			summary.incFailedrequest();
		}

		summary.addTime(timeElapsed);		
	}

}
