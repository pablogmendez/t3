package ar.fiuba.taller.loadTestConsole;

public class DownloaderTask {

	private String method;
	private String uri;

	public DownloaderTask(String method, String uri) {
		this.method = method;
		this.uri = uri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
