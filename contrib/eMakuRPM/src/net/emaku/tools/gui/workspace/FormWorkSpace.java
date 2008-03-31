package net.emaku.tools.gui.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.emaku.tools.Run;
import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.structures.FormsData;

/* 
 * This class represents the whole right side of the application GUI for the Form module
*/

public class FormWorkSpace extends JSplitPane implements ActionListener {

	private static final long serialVersionUID = 1L;
	private FormEditor editor;
	private JTextField nameField;
	private JTextField descField;
	private JTextField driverField;
	private JTextField argsDriverField;
	private JTextField methodField;
	private JTextField argsMethodField;
	private JPanel northPanel = new JPanel(new BorderLayout());
	private FormsData data;
	private String formCode;
	private JFrame externalFrame;
	private FormEditor fe;
		
	public FormWorkSpace(String formCode, FormsData data) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.formCode = formCode;
		this.data = data;
		setDataPanel();
		
		editor = new FormEditor(data.getProfile());
		NumbersPanel panel = new NumbersPanel(editor.getLinesTotal());
		//editor.setNumberPanel(panel);
		
		setDividerLocation(140);
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar("Maximize");
		JButton button = new JButton();
		String path = Run.root + "/lib/images/max.png";
		button.setIcon(new ImageIcon(path));
		button.setToolTipText("Maximize");
		button.setActionCommand("MAX");
		button.addActionListener(this);
		toolBar.setFloatable(false);
		toolBar.add(button);
		
		JPanel toolPanel = new JPanel(new BorderLayout());
		toolPanel.add(toolBar,BorderLayout.EAST);
					
		JPanel maximizer = new JPanel();
		maximizer.setLayout(new BorderLayout());
		maximizer.add(toolPanel,BorderLayout.NORTH);

		JPanel xmlZone = new JPanel(new BorderLayout());
		xmlZone.add(editor,BorderLayout.CENTER);
		xmlZone.add(panel,BorderLayout.WEST);
		JScrollPane scroll = new JScrollPane(xmlZone);
		
		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(maximizer,BorderLayout.NORTH);
		editorPanel.add(scroll,BorderLayout.CENTER);
			
		setTopComponent(northPanel);
		setBottomComponent(editorPanel);
	}
	
	private void setDataPanel(){
		JPanel labels = new JPanel(new GridLayout(6, 1));
		JPanel fields = new JPanel(new GridLayout(6, 1));
		JLabel nameLabel = new JLabel("Name: ");
		labels.add(nameLabel);
		JLabel descLabel = new JLabel("Description: ");
		labels.add(descLabel);
		JLabel driverLabel = new JLabel("Driver: ");
		labels.add(driverLabel);
		JLabel argsDriverLabel = new JLabel("Driver Args: ");
		labels.add(argsDriverLabel);
		JLabel methodLabel = new JLabel("Method: ");
		labels.add(methodLabel);
		JLabel argsMethodLabel = new JLabel("Method Args: ");
		labels.add(argsMethodLabel);
		
		nameField = new JTextField(10);
		nameField.setText(data.getName());
		fields.add(nameField);

		descField = new JTextField(10);
		descField.setText(data.getDescription());
		fields.add(descField);

		driverField = new JTextField(10);
		driverField.setText(data.getDriver());
		fields.add(driverField);

		argsDriverField = new JTextField(10);
		argsDriverField.setText(data.getDriverArgs());
		fields.add(argsDriverField);

		methodField = new JTextField(10);
		methodField.setText(data.getMethod());
		fields.add(methodField);

		argsMethodField = new JTextField(10);
		argsMethodField.setText(data.getMethodArgs());
		fields.add(argsMethodField);
		
		northPanel.add(labels,BorderLayout.WEST);
		northPanel.add(fields,BorderLayout.CENTER);
	}
	
	public FormsData getData() {
		return new FormsData(nameField.getText(),descField.getText(),driverField.getText(),
				argsDriverField.getText(),methodField.getText(),argsMethodField.getText(),
				editor.getProfile());
	}
	
	public void reloadForm(FormsData form) {
		nameField.setText(form.getName());
		descField.setText(form.getDescription());
		driverField.setText(form.getDriver());
		argsDriverField.setText(form.getDriverArgs());
		methodField.setText(form.getMethod());
		argsMethodField.setText(form.getMethodArgs());
		
		editor.updateText(form.getProfile());
	}
	
	private void openExternalZoom() {
		externalFrame = new JFrame(formCode + " - " + data.getName());
	    int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        externalFrame.setSize(new Dimension(width,height));
        externalFrame.setLayout(new BorderLayout());
		externalFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		externalFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeZoom();
			}
		});
		fe = new FormEditor(editor.getText());
		NumbersPanel panel = new NumbersPanel(editor.getLinesTotal());
		JPanel xmlZone = new JPanel(new BorderLayout());
		xmlZone.add(fe,BorderLayout.CENTER);
		xmlZone.add(panel,BorderLayout.WEST);
		JScrollPane scroll = new JScrollPane(xmlZone);
		
		ExternalButtonBar bar = new ExternalButtonBar(this);
		JPanel south = new JPanel(new BorderLayout());
		south.add(bar,BorderLayout.EAST);

		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(scroll,BorderLayout.CENTER);
		center.add(south,BorderLayout.SOUTH);
		externalFrame.add(center);
		externalFrame.setAlwaysOnTop(true);
		externalFrame.setVisible(true);
	}
	
	public void closeZoom() {
		String data = fe.getText();
		editor.updateText(data);
		externalFrame.setVisible(false);
	}
	
	public void reloadForm() {
		String data = DataBaseManager.getProfile(formCode);
		fe.updateText(data);
		editor.updateText(data);
	}
	
	public void saveForm() {
		DataBaseManager.updateProfile(formCode,fe.getText());
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();		
		if ("MAX".equals(action)) {
			openExternalZoom();
			return;
		}
	}
}

