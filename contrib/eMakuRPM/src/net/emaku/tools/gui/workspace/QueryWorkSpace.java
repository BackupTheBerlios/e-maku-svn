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
import net.emaku.tools.structures.QueriesData;

/* 
 * This class represents the whole right side of the application GUI for the Query module
*/

public class QueryWorkSpace extends JSplitPane implements ActionListener {

	private static final long serialVersionUID = 1L;
	private QueryEditor editor;
	private JTextField nameField;
	private JTextField descField;
	private JPanel northPanel = new JPanel(new BorderLayout());
	private QueriesData data;
	private String queryCode;
	private JFrame externalFrame;
	private QueryEditor qe;
		
	public QueryWorkSpace(String queryCode, QueriesData data) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.queryCode = queryCode;
		this.data = data;
		setDataPanel();
		
		editor = new QueryEditor(data.getSQL());
		NumbersPanel panel = new NumbersPanel(editor.getLinesTotal());
		//editor.setNumberPanel(panel);
		
		setDividerLocation(50);
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
		JPanel labels = new JPanel(new GridLayout(2, 1));
		JPanel fields = new JPanel(new GridLayout(2, 1));
		JLabel nameLabel = new JLabel("Name: ");
		labels.add(nameLabel);
		JLabel descLabel = new JLabel("Description: ");
		labels.add(descLabel);
		
		nameField = new JTextField(10);
		nameField.setText(data.getName());
		fields.add(nameField);

		descField = new JTextField(10);
		descField.setText(data.getDescription());
		fields.add(descField);
		
		northPanel.add(labels,BorderLayout.WEST);
		northPanel.add(fields,BorderLayout.CENTER);
	}
	
	public QueriesData getData() {
		return new QueriesData(nameField.getText(),descField.getText(),editor.getQuery());
	}
	
	public void reloadQuery(QueriesData form) {
		nameField.setText(form.getName());
		descField.setText(form.getDescription());		
		editor.updateText(form.getSQL());
	}
	
	private void openExternalZoom() {
		externalFrame = new JFrame(queryCode + " - " + data.getName());
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
		qe = new QueryEditor(editor.getQuery());
		NumbersPanel panel = new NumbersPanel(editor.getLinesTotal());
		JPanel xmlZone = new JPanel(new BorderLayout());
		xmlZone.add(qe,BorderLayout.CENTER);
		xmlZone.add(panel,BorderLayout.WEST);
		JScrollPane scroll = new JScrollPane(xmlZone);
		
		ExternalQueryButtonBar bar = new ExternalQueryButtonBar(this);
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
		String data = qe.getText();
		editor.updateText(data);
		externalFrame.setVisible(false);
	}
	
	public void reloadQuery() {
		String data = DataBaseManager.getSQLQuery(queryCode);
		qe.updateText(data);
		editor.updateText(data);
	}
	
	public void saveQuery() {
		DataBaseManager.updateEmakuSQL(queryCode,qe.getText());
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();		
		if ("MAX".equals(action)) {
			openExternalZoom();
			return;
		}
	}
}
