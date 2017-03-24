package ar.fiuba.taller.loadTestConsole;

public class DownloaderTask extends Task {

	private String method;
	private String uri;
	
	public DownloaderTask(Integer id, String method, String uri, Constants.TASK_STATUS status) {
		super(id, status);
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
