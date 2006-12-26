package common.control;

import java.util.EventObject;

import org.jdom.Element;

public class ReportEvent extends EventObject {
	
	private static final long serialVersionUID = 8650130090783824936L;
	private String report;
	private Element data;
	private String titleReport;
	public ReportEvent(Object source,String report, String titleReport, Element data) {
		super(source);
		this.report = report;
		this.data = data;
		this.titleReport = titleReport;
	}

	public String getIdReport() {
		return this.report;
	}
	
	public Element getData() {
		return data;
	}

	public String getTitleReport() {
		return titleReport;
	}
}
