package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ReportDialog extends JDialog  implements ActionListener {

	private static final long serialVersionUID = 1L;
	private String reportName;
	private String currentCategory;
	private JComboBox categoriesCombo;
	private JTextField jtextReportName;
	private HashMap<String,String> reportNames = new HashMap<String,String>();
	
	public ReportDialog(JFrame parent, Vector categories, String currentCategory) {
	  
	  super(parent,true);
	  this.currentCategory = currentCategory;
	  setTitle("New Report");
	  
	  reportNames.put("Avanzados","CREXXXX");
	  reportNames.put("Basicos","REPXXXX");
	  
	  JPanel panel = new JPanel();
	  panel.setLayout(new FlowLayout());
	  
	  JPanel JPlabels = new JPanel();
	  JPanel jpFields = new JPanel();
      JPlabels.setLayout(new GridLayout(2, 1));
      jpFields.setLayout(new GridLayout(2, 1));
	  
	  JLabel JLCategory = new JLabel("Category:");
	  JLabel JLReport = new JLabel("Name:");	
	  JPlabels.add(JLCategory);
	  JPlabels.add(JLReport);
	  
      categoriesCombo = new JComboBox(categories);
      
      if (categories.contains(currentCategory)) {
    	  categoriesCombo.setSelectedItem(currentCategory);
      } else {
    	  categoriesCombo.setSelectedIndex(0);
      }
      
      jtextReportName = new JTextField(10); 
      jtextReportName.setText(reportNames.get(categoriesCombo.getSelectedItem()));
      
      categoriesCombo.addActionListener(this);
      categoriesCombo.setActionCommand("combo");
      
      jpFields.add(categoriesCombo);
	  jpFields.add(jtextReportName);
      panel.add(JPlabels);
      panel.add(jpFields);
      
      JPanel jpSur = new JPanel();
      
      JButton jbAccept = new JButton("Accept");
      jbAccept.setToolTipText("Accept");
      JButton jbCancel = new JButton("Cancel");
      jbCancel.setToolTipText("Cancel");        

      jbAccept.addActionListener(this);
      jbAccept.setActionCommand("accept");
      jbCancel.addActionListener(this);
      jbCancel.setActionCommand("cancel");
      
      jpSur.add(jbAccept);
      jpSur.add(jbCancel);
      
      JPanel main = new JPanel();
      main.setLayout(new BorderLayout());
      main.add(panel,BorderLayout.CENTER);
      main.add(jpSur,BorderLayout.SOUTH);
      
      add(main);
	}

	private boolean isAValidFormat(String report) {
		boolean flag = true;
		
		if((report == null) || report.length() != 7) {
		   return false;	
		}
		String number = report.substring(3,7);
		if ((report.indexOf(' ') != -1)	|| (!report.startsWith("REP") 
				&& !report.startsWith("CRE")) || (!isNumber(number))) {
			 flag = false;
		}
		
      return flag;		
	}
	
	private boolean isNumber(String s) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(!Character.isDigit(c))
				return false;
		}
		return true;
	}
	
	private boolean isWellAsigned(String report) {
	  if (currentCategory.equals("Basicos") && report.startsWith("REP")) {
		  return true;
	  }
	  if (currentCategory.equals("Avanzados") && report.startsWith("CRE")) {
		  return true;  
	  }
	  return false;  
	}
	
    public String getReportName() {
        return reportName;	
    }
    
    public String getCategoryName() {
        return currentCategory;	
    }

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("accept".equals(action)) {
			reportName = jtextReportName.getText();
            currentCategory = (String)categoriesCombo.getSelectedItem();
            System.out.println("R: " + reportName);
            
            if (!isAValidFormat(reportName)) {
          	  JOptionPane.showMessageDialog(this,"The report name has not a valid format.", 
          			  						"ERROR!", JOptionPane.ERROR_MESSAGE);	
          	  return;
            }
            
            if(!isWellAsigned(reportName)) {
            	  JOptionPane.showMessageDialog(this,"Report Name must to start with \"REP\" for Category: Basicos\n" + 
            			  "Report Name must to start with \"CRE\" for Category: Avanzados", 
	  						"ERROR!", JOptionPane.ERROR_MESSAGE);	
            	return;
            }
            
            
          	setVisible(false);
		} 
		else if("cancel".equals(action)) {
      	  reportName = null;
    	  setVisible(false);
		} else if("combo".equals(action)) {
  		  jtextReportName.setText(reportNames.get(categoriesCombo.getSelectedItem()));
		}
	}
}
