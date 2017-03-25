package ar.fiuba.taller.loadTestConsole;

public class DownloaderTask extends Task {

	private String method;
	private String uri;
	private String resourceType;
	
	public DownloaderTask(Integer id, String method, String uri, Constants.TASK_STATUS status, String resourceType) {
		super(id, status);
		this.method = method;
		this.uri = uri;
		this.setResourceType(resourceType);
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

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

}
