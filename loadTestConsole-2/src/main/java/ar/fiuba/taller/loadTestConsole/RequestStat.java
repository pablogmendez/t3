package ar.fiuba.taller.loadTestConsole;

public class RequestStat extends SummaryStat {
	private long successfullRequest;
	private long failedRequest;
	private long avgDownloadTime;
	
	public RequestStat(long successfullRequest, long failedRequest,
			long avgDownloadTime) {
		super();
		this.successfullRequest = successfullRequest;
		this.failedRequest = failedRequest;
		this.avgDownloadTime = avgDownloadTime;
	}

	@Override
	public void updateSumary(Summary summary) { 
		summary.incSuccessfullrequest(successfullRequest);
		summary.incFailedrequest(failedRequest);
		summary.updateAvgDownloadTime(avgDownloadTime);		
	}

}
