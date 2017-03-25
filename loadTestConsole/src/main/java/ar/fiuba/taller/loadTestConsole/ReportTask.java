package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.loadTestConsole.Constants.TASK_STATUS;

public class ReportTask extends Task {

	private Boolean analyzer;
	private String resource;
	
	public Boolean getAnalyzer() {
		return analyzer;
	}
	public void setAnalyzer(Boolean analyzer) {
		this.analyzer = analyzer;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public ReportTask(Integer id, TASK_STATUS status, Boolean analyzer, String resource) {
		super(id, status);
		this.analyzer = analyzer;
		this.resource = resource;
	}
}
