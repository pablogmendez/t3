package ar.fiuba.taller.loadTestConsole;

public class Report {

	private Integer analyzedUrl;
	private Integer downloadedScripts;
	private Integer downloadedLinks;
	private Integer downloadedImages;
	private Integer executionScriptThreads;
	private Integer downloadResourceThreads;
	
	public synchronized Integer getAnalyzedUrl() {
		return analyzedUrl;
	}

	public synchronized void incAnalyzedUrl() {
		analyzedUrl++;
	}
	
	public synchronized void decAnalyzedUrl() {
		analyzedUrl--;
	}

	public synchronized Integer getDownloadedScripts() {
		return downloadedScripts;
	}

	public synchronized void incDownloadedScripts() {
		downloadedScripts++;
	}

	public synchronized void decDownloadedScripts() {
		downloadedScripts--;
	}
	
	public synchronized Integer getDownloadedLinks() {
		return downloadedLinks;
	}

	public synchronized void incDownloadedLinks() {
		downloadedLinks++;
	}

	public synchronized void decDownloadedLinks() {
		downloadedLinks--;
	}
	
	public synchronized Integer getDownloadedImages() {
		return downloadedImages;
	}

	public synchronized void incDownloadedImages() {
		downloadedImages++;
	}

	public synchronized void decDownloadedImages() {
		downloadedImages--;
	}
	
	public synchronized Integer getExecutionScriptThreads() {
		return executionScriptThreads;
	}

	public synchronized void incExecutionScriptThreads() {
		executionScriptThreads++;
	}


	public synchronized void decExecutionScriptThreads() {
		executionScriptThreads--;
	}
	
	public synchronized Integer getDownloadResourceThreads() {
		return downloadResourceThreads;
	}

	public synchronized void incDownloadResourceThreads() {
		downloadResourceThreads++;
	}

	public synchronized void decDownloadResourceThreads() {
		downloadResourceThreads--;
	}
	
	public Report() {
		super();
		analyzedUrl = 0;
		downloadedScripts = 0;
		downloadedLinks = 0;
		downloadedImages = 0;
		executionScriptThreads = 0;
		downloadResourceThreads = 0;
	}
	


}
