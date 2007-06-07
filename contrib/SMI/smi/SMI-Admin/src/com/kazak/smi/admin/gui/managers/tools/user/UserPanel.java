package com.kazak.smi.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.Operation;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;
import com.kazak.smi.admin.gui.managers.tools.Transactions.UserDocument;
import com.kazak.smi.admin.gui.managers.tools.user.UserData;
import com.kazak.smi.admin.gui.managers.tools.ButtonBar;
import com.kazak.smi.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.smi.admin.gui.misc.GUIFactory;

public class UserPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private MainForm mainFrame;
	private int action;
	private String target;
	private ButtonBar buttonBar;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private AutoCompleteComboBox nameField;
	private JButton searchButton;
	private JPanel tmp = new JPanel();
	
	public UserPanel(MainForm mainFrame, ButtonBar buttonBar,int action, String target) {
		this.setLayout(new BorderLayout());
		this.mainFrame = mainFrame;
		this.buttonBar = buttonBar;
		this.action = action;
		this.target = target;

		initInterface();
		packPanels();		
	}
	
	private void initInterface() {
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);

		nameField = new AutoCompleteComboBox(Cache.getUsersList(),false,50,searchButton);
		
		setInitMode();
		this.setVisible(true);
	}
	
	private void setInitMode(){
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			mainFrame.setTitle("Nuevo Usuario");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			mainFrame.setTitle("Editar Usuario");
			break;
			// Edit pre-filled
		case ToolsConstants.EDIT_PREFILLED:
			mainFrame.setTitle("Editar Usuario");
			nameField.setSelectedItem(target);
			break;
			// To Delete
		case ToolsConstants.DELETE:
			mainFrame.setTitle("Borrar Usuario");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// Delete pre-filled
		case ToolsConstants.DELETE_PREFILLED:
			nameField.setSelectedItem(target);
			mainFrame.setTitle("Borrar Usuario");
			break;
			// To Search
		case ToolsConstants.SEARCH:
			mainFrame.setTitle("Buscar Usuario");
			break;
			// Search pre-filled
		case ToolsConstants.SEARCH_PREFILLED:
			mainFrame.setTitle("Buscar Usuario");			
			break;
		}			
	}
	
	private void packPanels() {
		Border border = BorderFactory.createEtchedBorder();
		
		JPanel labelsPanel = new JPanel(new GridLayout(1,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(1,0));
		
		labelsPanel.add(new JLabel("Login: "));
		fieldsPanel.add(nameField);
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		//searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.Y_AXIS));
		searchPanel.setPreferredSize(new Dimension(50,20));
		//searchPanel.add(new JPanel(),BorderLayout.WEST);
		searchPanel.add(searchButton); //,BorderLayout.CENTER);
		//searchPanel.setBounds(new Rectangle(90,90));
		searchPanel.setBorder(border);
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(200,25));
		leftPanel.add(labelsPanel,BorderLayout.WEST);
		leftPanel.add(fieldsPanel,BorderLayout.CENTER);
		leftPanel.setBorder(border);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(leftPanel,BorderLayout.CENTER);
		centerPanel.add(searchPanel,BorderLayout.EAST);
		centerPanel.setPreferredSize(new Dimension(260,30));
		
        /* JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(centerPanel);
        split.setBottomComponent(new JPanel());
        split.setDividerLocation(160); */
		
		this.add(centerPanel,BorderLayout.CENTER);
	}
	
	private String[] getFormData(){
		String[] data = new String[5];
		data[0] = target;
		data[1] = "";
		data[2] = "";
		data[3] = "";
		data[4] = "";
		
		return data;
	}
	
	public void executeOperation() {
		UserData user = new UserData();
		if (!user.verifyData()) {
			return;
		}

		UserDocument doc = new UserDocument(getFormData());
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			Operation.execute(doc.getDocumentToAdd());
			mainFrame.dispose();
			break;
			// To Edit
		case ToolsConstants.EDIT:
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			Operation.execute(doc.getDocumentToEdit());
			mainFrame.dispose();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
		}	   		
	}
	
	public void clean() {
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			break;
			// To Edit
		case ToolsConstants.EDIT:
			break;
			// To Delete
		case ToolsConstants.DELETE:
			break;
			// To Search
		case ToolsConstants.SEARCH:
			break;
		}	
	}
	
	private void doSearch() {
		target = nameField.getText();
		fillForm();		
	}

	private void fillForm() {
		if (Cache.containsUser(target)) {
			User user = Cache.getUser(target);
			
			switch(action) {
			case ToolsConstants.ADD:
				JOptionPane.showMessageDialog(mainFrame,"El usuario " + target + " ya existe. ");
				nameField.blankTextField();
				nameField.requestFocus();
				break;
			case ToolsConstants.EDIT:
			case ToolsConstants.EDIT_PREFILLED:
				break;
			case ToolsConstants.DELETE:
			case ToolsConstants.DELETE_PREFILLED:
				buttonBar.setEnabledAcceptButton(true);
			case ToolsConstants.SEARCH:
			case ToolsConstants.SEARCH_PREFILLED:
				nameField.requestFocus();
				tmp.add(new JLabel("Bullet proof!"));
				tmp.updateUI();
				break;
			}
		} else {
			if (action != ToolsConstants.ADD) {
				JOptionPane.showMessageDialog(mainFrame,"El usuario " + target + " no existe. ");
				//resetPanel();
			} else {
				buttonBar.setEnabledAcceptButton(true);
			}
		}			
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			doSearch();
		}
	}

}
