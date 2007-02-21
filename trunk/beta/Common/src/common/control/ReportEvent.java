package common.control;

import java.util.EventObject;

import org.jdom.Element;

public class ReportEvent extends EventObject {
	
	private static final long serialVersionUID = 8650130090783824936L;
	private String report;
	private Element data;
	private String titleReport;
	private boolean plainReport;
	
	public ReportEvent(Object source,String report, String titleReport, Element data,boolean plainReport) {
		super(source);
		this.report = report;
		this.data = data;
		this.titleReport = titleReport;
		this.plainReport=plainReport;
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

	public boolean isPlainReport() {
		return plainReport;
	}
}
