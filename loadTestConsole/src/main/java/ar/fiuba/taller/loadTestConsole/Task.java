package ar.fiuba.taller.loadTestConsole;

public class Task {
	
	private Integer type;
	private String method;
	private String uri;
	
	
	public Task(Integer type, String method, String uri) {
		this.type = type;
		this.method = method;
		this.uri = uri;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
