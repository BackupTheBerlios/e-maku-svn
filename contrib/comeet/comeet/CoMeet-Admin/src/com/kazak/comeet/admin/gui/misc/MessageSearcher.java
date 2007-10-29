package com.kazak.comeet.admin.gui.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.gui.main.MainWindow;
import com.kazak.comeet.admin.transactions.QuerySender;
import com.kazak.comeet.admin.transactions.QuerySenderException;
import com.kazak.comeet.lib.misc.FixedSizePlainDocument;
import com.toedter.calendar.JDateChooser;

public class MessageSearcher extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JTextField fromField;
	private JTextField toField;
	private JDateChooser dateField;
	private JTextField subjectField;
	
	private JPanel centerPanel;
	private JPanel southPanel;
	
	private JButton cancelButton;
	private JButton searchButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();

	final JFrame frame = this;
	private JDialog dialog;
	
	private String[]
    labels = {
			"Remitente ",
			"Destino ",
			"Fecha",
			"Asunto " };
	
	public MessageSearcher() {
		this.setLayout(new BorderLayout());
		this.setSize(300,160);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
	}
	
	// This method is called from menu.xml
	public void search() {
		this.setTitle("Buscar Mensajes");
		searchButton.setActionCommand("search");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
    private void setDateChooser() {
        //dateField = new JDateChooser();
        //dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setFocusCycleRoot(true);
        dateField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                        dateField.getDateEditor().getUiComponent().requestFocusInWindow();
                }
        });
        dateField.getDateEditor().getUiComponent().addKeyListener(new KeyAdapter() {

                public void keyPressed(final KeyEvent e) {
                        Thread t = new Thread() {
                                public void run() {
                                        try {
                                                Thread.sleep(100);
                                        }
                                        catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                        }
                                        int keyCode = e.getKeyCode();
                                        switch (keyCode) {
                                                case KeyEvent.VK_ENTER:
                                                        subjectField.requestFocus();
                                                        break;
                                        }
                                }
                        };
                        t.start();
                }
        });
    }
	
	private void initComponents() {
		componentsList.add(fromField = new JTextField());
		fromField.addKeyListener(this);
		fromField.setName("from");
		componentsList.add(toField = new JTextField());
		toField.addKeyListener(this);
		toField.setName("to");
		componentsList.add(dateField = new JDateChooser());
		componentsList.add(subjectField = new JTextField());
		subjectField.addKeyListener(this);
		subjectField.setName("subject");
		
		dateField.setDateFormatString("yyyy-MM-dd");
		setDateChooser();
		
		fromField.setDocument(new FixedSizePlainDocument(50));
		toField.setDocument(new FixedSizePlainDocument(50));
		subjectField.setDocument(new FixedSizePlainDocument(100));
		
		searchButton = new JButton("Buscar");
		searchButton.setMnemonic('B');
		cancelButton = new JButton("Cancelar");
		cancelButton.setMnemonic('C');
		
		searchButton.addActionListener(this);
		searchButton.addKeyListener(this);
		cancelButton.addActionListener(this);
		
		centerPanel = new JPanel(new BorderLayout());
		southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(labels.length,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			labelsPanel.add(new JLabel(labels[i]));
			fieldsPanel.add(componentsList.get(i));
		}

		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);
		southPanel.add(searchButton);
		southPanel.add(cancelButton);
		
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("search")) {
			openWaitingDialog();
			new Worker().start();
		}
	}
	
	private void openWaitingDialog() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		JLabel label1 = new JLabel("Por favor, espere un momento.",JLabel.CENTER);
		JPanel main = new JPanel(new FlowLayout(FlowLayout.CENTER));
		main.add(label1);
		dialog = new JDialog(frame);
		dialog.setTitle("Realizando búsqueda...");
		dialog.setResizable(false);
		dialog.setSize(250,60);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationByPlatform(true);
		dialog.setLocationRelativeTo(frame);
		dialog.setAlwaysOnTop(true);
		dialog.add(main);
		dialog.setVisible(true);		
	}
	
	private void setNormalCursor() {
		int typeCursor = Cursor.DEFAULT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);		
	}
	
	class Worker extends Thread {		
		public void run() {
			Date date = dateField.getDate();
			frame.dispose();
			String [] argsArray = null;
			String sqlCode = "";
			if (date==null) {
				argsArray = new String[]{
						fromField.getText(),
						toField.getText(),
						subjectField.getText()
						};
				sqlCode ="SEL0021";
			}
			else {
				argsArray = new String[]{
						fromField.getText(),
						toField.getText(),
						dateField.getDate().toString(),
						subjectField.getText()		
						};
				sqlCode = "SEL0016";
			}

			try {
				Document doc = QuerySender.getResultSetFromST(sqlCode,argsArray);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
				while (rows.hasNext()) {
					Vector<Object> vector = new Vector<Object>();
					Element columns = (Element) rows.next();
					Iterator columnsIterator = columns.getChildren().iterator();
					while (columnsIterator.hasNext()) {
						Element element = (Element)columnsIterator.next();
						vector.add(element.getText());
					}
					data.add(vector);
				}
				dialog.setVisible(false);
				setNormalCursor();
				new MessageViewer(data);

			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String object = e.getSource().getClass().toString();
		if ("class javax.swing.JButton".equals(object)) {
			if (keyCode==KeyEvent.VK_ENTER && searchButton.hasFocus()){
				searchButton.doClick();
			}
		}
		
		if ("class javax.swing.JTextField".equals(object)) {
			JTextField JText = ((JTextField) e.getSource());
			String textField = JText.getName();
			if (keyCode==KeyEvent.VK_ENTER){
				if (textField.equals("from")) {
					toField.requestFocus();
				}
				if (textField.equals("to")) {
					dateField.requestFocus();
				}
				if (textField.equals("subject")) {
					String value = JText.getText();
					if (value.length() > 0) {
						searchButton.doClick();
					} else {
						searchButton.requestFocus();
					}
				}
			}		
		}
	}

	public void keyTyped(KeyEvent e) {
	}
}