package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.emaku.tools.db.DataBaseManager;

public class SearchDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField pattern;
	private JComboBox searchSpace; 
	private JButton search,clean,cancel;
	private ResultPanel dynamicPanel;
	private boolean dynamicPanelIsVisible = false;
	
	public SearchDialog(JFrame frame) {
		super(frame);
		setTitle("Search Tool");
		setLayout(new BorderLayout());
		setInterface();

		setSize(new Dimension(500,140));
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
	private void setInterface() {
		pattern = new JTextField(30);
		pattern.setActionCommand("SEARCH");
		pattern.addActionListener(this);
		
		search = new JButton("Search");
		search.setActionCommand("SEARCH");
		search.addActionListener(this);
		
		JPanel searchPanel = new JPanel(new FlowLayout());
		searchPanel.add(pattern);
		searchPanel.add(search);
		
		JLabel label = new JLabel("Look for");
		Object[] spaces = {"Everything","Forms","Queries","Reports"};
		searchSpace = new JComboBox(spaces);
		
		JPanel detailsPanel = new JPanel(new FlowLayout());
		detailsPanel.add(label);
		detailsPanel.add(searchSpace);
		
		JPanel northPanel = new JPanel(new GridLayout(2,1));
		northPanel.add(searchPanel);
		northPanel.add(detailsPanel);
				
		clean = new JButton("Clean");
		clean.setActionCommand("CLEAN");
		clean.addActionListener(this);
		clean.setEnabled(false);
		
		cancel = new JButton("Close");
		cancel.setActionCommand("CLOSE");
		cancel.addActionListener(this);
		
		JPanel south = new JPanel(new FlowLayout());
		south.add(clean);
		south.add(cancel);
		
		add(northPanel,BorderLayout.NORTH);
		add(south,BorderLayout.SOUTH);
	}
	
	public void collapseInternalPanel()  {
		//  Remove the details panel from the dialog
		getContentPane().remove (dynamicPanel);

		//  Shrink the dialog the height of the removed panel
		Dimension detailsPanelSize = dynamicPanel.getSize ();
		Dimension dialogSize = getSize ();
		dialogSize.height -= detailsPanelSize.height;
		setSize (dialogSize);

		//  Cause the new layout to take effect
		invalidate ();
		validate ();
		
		if (dynamicPanelIsVisible) {
			dynamicPanelIsVisible = false;
		}
	}
	
	public void expandInternalPanel(Vector<Vector<String>> result)  {
				
		dynamicPanel = new ResultPanel(result);

		//  Add the details panel to the dialog
		getContentPane().add (dynamicPanel, BorderLayout.CENTER);
		//  Resize the dialog to accomidate the added size
		Dimension dialogSize = getSize ();
		Dimension detailSize = null;
		
		if (dynamicPanelIsVisible)  {
			//  The details area already has a size from the last time it was displayed
			detailSize = dynamicPanel.getSize ();
		}  else  {
			//  The details area has yet to be made visible
			detailSize = dynamicPanel.getPreferredSize ();
		}

		//  Increase the height of the dialog to fit the added details area
		dialogSize.height += detailSize.height; 
		setSize(dialogSize);
		//  Cause the new layout to take effect
		invalidate ();
		validate ();
		
		if (!dynamicPanelIsVisible) {
			dynamicPanelIsVisible = true;
		}
	}
	
	public boolean isPanelVisible() {
		return dynamicPanelIsVisible;
	}
	
	private void doSearch() {
		if (dynamicPanelIsVisible) {
			collapseInternalPanel();
		}
		String space = searchSpace.getSelectedItem().toString();
		String keywords = pattern.getText();
		Vector<Vector<String>> data = DataBaseManager.getSearchResult(space,keywords);
		clean.setEnabled(true);
		expandInternalPanel(data);
	}
	
	private void clean() {
		pattern.setText("");
		clean.setEnabled(false);
		collapseInternalPanel();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("SEARCH".equals(action)) {
			doSearch();
		} else if ("CLEAN".equals(action)) {
			clean();
		} else if ("CLOSE".equals(action)) {
			setVisible(false);
		} 
	}
}
