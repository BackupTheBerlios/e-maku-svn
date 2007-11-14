/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens
 *
 * (C) Copyright 2007, IDRsolutions and Contributors.
 *
 * 	This file is part of JPedal
 *
     This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * Preferences.java
 * ---------------
 * (C) Copyright 2007, by IDRsolutions and Contributors.
 *
 * Original Author:  Mark Stephens
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.popups;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jpedal.examples.simpleviewer.*;
import org.jpedal.examples.simpleviewer.gui.*;
import org.jpedal.utils.*;

/**
 * provides a popup preferences option in SimpleViewer
 *
 */
public class Preferences {
	
	//Window Components
	JFrame jf = new JFrame("JPedal PDF Preferences");
	
	JLabel title = new JLabel("Preferences");
	
	JPanel mainPane = new JPanel(new GridBagLayout());
	
	JPanel[] settings = new JPanel[2];
	
	JButton confirm = new JButton("OK");
	
	JButton cancel = new JButton("Cancel");
	
	
	//Settings Fields Components
	
	//DPI viewer value
	JTextField dpi_Input = new JTextField("96");
	String dpiDefaultValue = "96";
	
	//Search window display style
	JComboBox searchStyle = new JComboBox(new String[]{"Ext. Window","Tab Pane","Menu Bar"});
	int searchStyleDefaultValue = 1;
	
	//Show border around page
	JCheckBox border = new JCheckBox();
	int borderDefaultValue = 1;
	
	//Set autoScroll when mouse at the edge of page
	JCheckBox autoScroll = new JCheckBox();
	boolean scrollDefaultValue = false;
	
	//Set default page layout
	JComboBox pageLayout = new JComboBox(new String[]{"Single Page","Continuous","Continuous Facing", "Facing"});
	int pageLayoutDefaultValue = 1;
	
	JList settingsList = new JList(new String[]{"Display","Viewer"});
	
	Box setButtons = Box.createHorizontalBox();
	
	
	//Keep track of which settings panel to remove before adding new
	int currentListSelection = 0;
	
	
	/**
	 * createPreferanceWindow(final GUI gui)
	 * Set up all settings fields then call the required methods to build the window
	 * 
	 * @param gui - Used to allow any changed settings to be saved into an external properties file.
	 * 
	 */
	public void createPreferanceWindow(final GUI gui){
		jf = new JFrame(Messages.getMessage("PageLayoutViewMenu.PreferencesWindowTitle"));
		
		/*
		 * Ensure current values have been set into the fields
		 */
		for(int i=0; i!=settings.length;i++){
			settings[i] = new JPanel(new BorderLayout());
		}
		
		settingsList = new JList(new String[]{"Display","Viewer"});
		settingsList.setSelectedIndex(currentListSelection);
		settingsList.setSize(50, 100);
		
		dpi_Input = new JTextField(dpiDefaultValue);
		dpi_Input.setPreferredSize(new  Dimension(dpi_Input.getFont().getSize()*4,dpi_Input.getFont().getSize()+10));
		
		searchStyle = new JComboBox(new String[]{Messages.getMessage("PageLayoutViewMenu.WindowSearch"),Messages.getMessage("PageLayoutViewMenu.TabbedSearch"),Messages.getMessage("PageLayoutViewMenu.MenuSearch")});
		pageLayout = new JComboBox(new String[]{Messages.getMessage("PageLayoutViewMenu.SinglePage"),Messages.getMessage("PageLayoutViewMenu.Continuous"),Messages.getMessage("PageLayoutViewMenu.ContinousFacing"),Messages.getMessage("PageLayoutViewMenu.Facing")});
		
		
		border = new JCheckBox();
		autoScroll = new JCheckBox();

		confirm = new JButton("OK");
		cancel = new JButton("Cancel");
		
		title = new JLabel(Messages.getMessage("PageLayoutViewMenu.Preferences"));
		title.setFont(new Font(null,Font.BOLD,14));
		
		setButtons.add(confirm);
		setButtons.add(Box.createHorizontalStrut(30));
		setButtons.add(cancel);
		
		
		/*
		 * Build the Settings panels
		 */
		for(int i=0; i!=settingsList.countComponents()+1;i++){
			switch(i){
			case 0 : createDisplaySettings(settings[0]);break;
			case 1 : createViewerSettings(settings[1]);break;
			default : break;
			}
		}
		
		
		//Build the preferences window
		mainPane = buildMainPane(mainPane);
		
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(mainPane,BorderLayout.CENTER);
		jf.setSize(400, 300);
		jf.setResizable(false);
		
		/*
		 * Listeners that are reqired for each setting field
		 */
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dpiDefaultValue = dpi_Input.getText();
				int dpi = Integer.parseInt(dpi_Input.getText());
				int style = searchStyleDefaultValue = searchStyle.getSelectedIndex();
				
				//Start at single (57 + 0) and add drop box position 
				int pageMode = pageLayoutDefaultValue = pageLayout.getSelectedIndex()+Commands.SINGLE;
				
				int borderStyle = borderDefaultValue = 0;
				if(border.isSelected()){
					borderStyle = borderDefaultValue = 1;
				}

				boolean toggleScroll = scrollDefaultValue = autoScroll.isSelected();

				gui.setPreferences(dpi, style, borderStyle, toggleScroll, pageMode);

				jf.setVisible(false);
			}
		});

		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				jf.setVisible(false);
			}
		});
		
		dpi_Input.addKeyListener(new KeyListener(){

			boolean consume = false;

			public void keyPressed(KeyEvent e) {
				consume = false;
				if((e.getKeyChar()<'0' || e.getKeyChar()>'9') && (e.getKeyCode()!=8 || e.getKeyCode()!=127))
					consume = true;
			}

			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {
				if(consume)
					e.consume();
			}

		});
		
		settingsList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				changeDisplayedSettings();
			}
		});
		
		searchStyle.setSelectedIndex(searchStyleDefaultValue);
		dpi_Input.setText(dpiDefaultValue);
		if(borderDefaultValue==1)
			border.setSelected(true);
		else
			border.setSelected(false);
		autoScroll.setSelected(scrollDefaultValue);
		
	}
	
	/**
	 * buildMainPane(JPanel mainPane)
	 * 
	 * conveniance method to all for easier modification of preference window general layout
	 * 
	 * @param mainPane - The Jpanel that holds all fields for this window
	 * @return mainPane - Return the completed Jpanel to be attatched to a frame.
	 */
	private JPanel buildMainPane(JPanel mainPane){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.insets = new Insets(0,10,0,10);
		c.ipadx = 10;
		c.ipady = 10;
		c.fill = GridBagConstraints.BOTH;
			
		c.gridx = 0;
		c.gridy = 0;
		mainPane.add(title,c);
		
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 5;
		c.gridwidth = 5;
		mainPane.add(settings[0],c);
		
		c.gridx = 5;
		c.gridy = 10;
		c.gridwidth = 2;
		c.gridheight = 1;
		mainPane.add(setButtons,c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 9;
		c.gridwidth = 2;
		mainPane.add(settingsList,c);
		
		return mainPane;
	}
	
	/**
	 * changeDisplayedSettings()
	 * 
	 * Remove the previous settings options and
	 * display the new settings pane
	 */
	private void changeDisplayedSettings() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 5;
		c.gridwidth = 5;
		c.weighty = 1.0;
		c.ipadx = 10;
		c.ipady = 10;
		c.insets = new Insets(0,10,0,10);
		
		mainPane.remove(settings[currentListSelection]);
		
		mainPane.add(settings[settingsList.getSelectedIndex()],c);
		
		mainPane.validate();
		mainPane.repaint();
		
		currentListSelection = settingsList.getSelectedIndex();
	}
	
	/**
	 * showPreferanceWindow()
	 *
	 * Ensure current values are loaded then display window.
	 */
	public void showPreferanceWindow(){
		jf.setVisible(true);
	}
	
	/*
	 * Creates a pane holding all Viewer settings (e.g Search Style, auto scrolling, etc)
	 */
	private JPanel createViewerSettings(JPanel pane){
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		pane.setPreferredSize(new Dimension(250,100));
		pane.setMinimumSize(new Dimension(250,100));
		c.insets = new Insets(10,0,10,0);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(new JLabel(Messages.getMessage("PageLayoutViewMenu.SearchLayout")), c);
		
		c.gridx = 2;
		c.gridy = 0;
		pane.add(searchStyle, c);
		
		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel(Messages.getMessage("PageLayoutViewMenu.PageLayout")), c);
		
		c.gridx = 2;
		c.gridy = 1;
		pane.add(pageLayout, c);

		c.gridx = 0;
		c.gridy = 2;
		pane.add(new JLabel(Messages.getMessage("PdfViewerViewMenuAutoscrollSet.text")), c);
		
		c.gridx = 2;
		c.gridy = 2;
		pane.add(autoScroll, c);

		pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0.3f,0.5f,1f), 1), "Viewer Settings"));
		
		return pane;
	}
	
	/*
	 * Creates a pane holding all PDF display settings (e.g Borders, Dpi, etc)
	 */
	private JPanel createDisplaySettings(JPanel pane){
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		pane.setPreferredSize(new Dimension(250,100));
		pane.setMinimumSize(new Dimension(250,100));
		c.insets = new Insets(10,0,10,0);
//		c.ipadx = 10;
//		c.ipady = 10;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(new JLabel(Messages.getMessage("PdfViewerViewMenu.Dpi")), c);
		
		c.gridx = 2;
		c.gridy = 0;
		pane.add(dpi_Input, c);

		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel(Messages.getMessage("PageLayoutViewMenu.Borders_Show")), c);
		
		c.gridx = 2;
		c.gridy = 1;
		pane.add(border, c);
		
		pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0.3f,0.5f,1f), 1), "Display Settings"));

		return pane;
	}
	

	/*
	 * Following methods used to load default values when application starts.
	 * 
	 */
	public void setAutoScrollDefaultValue(boolean autoScrollDefaultValue) {
		this.scrollDefaultValue = autoScrollDefaultValue;
	}

	public void setBorderDefaultValue(int borderDefaultValue) {
		this.borderDefaultValue = borderDefaultValue;
	}

	public void setDpiDefaultValue(String dpiDefaultValue) {
		this.dpiDefaultValue = dpiDefaultValue;
	}

	public void setSearchStyleDefaultValue(int searchStyleDefaultValue) {
		this.searchStyleDefaultValue = searchStyleDefaultValue;
	}

	

}
