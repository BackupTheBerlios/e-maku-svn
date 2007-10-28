package net.emaku.tools.gui.workspace;

import javax.swing.JTextPane;

import net.emaku.tools.db.DataBaseManager;

// This class represents the description for every report in the workspace

public class ReportDescription extends JTextPane {
	
	private static final long serialVersionUID = 1L;
	private String reportCode;
	private String description;

	public ReportDescription(String reportCode) {
		this.reportCode = reportCode;
		DataBaseManager.connect();
		description = DataBaseManager.getReportDescription(reportCode);
		DataBaseManager.close();
		setText(description);
	}
	
	public void updateDescription() {
		String current = getText();
		if(!current.equals(description)) {
			DataBaseManager.connect();
			DataBaseManager.updateReportDescription(reportCode,current);
			DataBaseManager.close();
		}
	}

}
