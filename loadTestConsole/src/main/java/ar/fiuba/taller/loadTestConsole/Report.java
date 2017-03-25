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

	public synchronized void setAnalyzedUrl(Integer analyzedUrl) {
		this.analyzedUrl = analyzedUrl;
	}

	public synchronized Integer getDownloadedScripts() {
		return downloadedScripts;
	}

	public synchronized void setDownloadedScripts(Integer downloadedScripts) {
		this.downloadedScripts = downloadedScripts;
	}

	public synchronized Integer getDownloadedLinks() {
		return downloadedLinks;
	}

	public synchronized void setDownloadedLinks(Integer downloadedLinks) {
		this.downloadedLinks = downloadedLinks;
	}

	public synchronized Integer getDownloadedImages() {
		return downloadedImages;
	}

	public synchronized void setDownloadedImages(Integer downloadedImages) {
		this.downloadedImages = downloadedImages;
	}

	public synchronized Integer getExecutionScriptThreads() {
		return executionScriptThreads;
	}

	public synchronized void setExecutionScriptThreads(Integer executionScriptThreads) {
		this.executionScriptThreads = executionScriptThreads;
	}

	public synchronized Integer getDownloadResourceThreads() {
		return downloadResourceThreads;
	}

	public synchronized void setDownloadResourceThreads(Integer downloadResourceThreads) {
		this.downloadResourceThreads = downloadResourceThreads;
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
