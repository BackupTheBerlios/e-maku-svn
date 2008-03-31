package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.emaku.tools.Run;

public class SettingsDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton accept,cancel;
	private JTextField theme, output, driver, url, user;
	private Properties properties;

	public SettingsDialog(JFrame frame) {
		super(frame);
		setTitle("eMaku RPM Settings");
		setLayout(new BorderLayout());
		setInterface();
		loadData();

		setSize(new Dimension(400,200));
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
	public void setInterface() {
		
		JPanel labels = new JPanel(new GridLayout(5, 1));
		JPanel fields = new JPanel(new GridLayout(5, 1));
		
		JLabel themeLabel = new JLabel("Theme: ");
		labels.add(themeLabel);
		
		theme = new JTextField(10);
		fields.add(theme);
		
		JLabel outputLabel = new JLabel("Jar Output Directory: ");
		labels.add(outputLabel);
		
		output = new JTextField(10);
		fields.add(output);
		
		JLabel driverLabel = new JLabel("Driver: ");
		labels.add(driverLabel);
		
		driver = new JTextField(10);
		fields.add(driver);
		
		JLabel urlLabel = new JLabel("DB Url: ");
		labels.add(urlLabel);
		
		url = new JTextField(10);
		fields.add(url);
		
		JLabel userLabel = new JLabel("DB User: ");
		labels.add(userLabel);
		
		user = new JTextField(10);
		fields.add(user);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(labels,BorderLayout.WEST);
		northPanel.add(fields,BorderLayout.CENTER);

		accept = new JButton("Save");
		accept.setActionCommand("SAVE");
		accept.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.setActionCommand("CLOSE");
		cancel.addActionListener(this);
		
		JPanel south = new JPanel(new FlowLayout());
		south.add(accept);
		south.add(cancel);
		
		add(northPanel,BorderLayout.CENTER);
		add(south,BorderLayout.SOUTH);
	}
	
	private void loadData() {
		properties = FrontEnd.loadConfigFile(Run.root);
		theme.setText(properties.getProperty("theme"));
		output.setText(properties.getProperty("output"));
		driver.setText(properties.getProperty("driver"));
		url.setText(properties.getProperty("url"));
		user.setText(properties.getProperty("user"));
	}
	
	private void saveData() {
		properties.setProperty("theme",theme.getText());
		String directory = output.getText();
		File file = new File(directory);
		if(!file.exists()) {
			JOptionPane.showMessageDialog(
                    this,
                    "ERROR: Directory " + directory + " does not exist!",
                    "ERROR",JOptionPane.ERROR_MESSAGE);

			return;
		}
		properties.setProperty("output",output.getText());
		properties.setProperty("driver",driver.getText());
		properties.setProperty("url",url.getText());
		properties.setProperty("user",user.getText());

		try {
			OutputStream outFile = new FileOutputStream(new File(Run.root + FrontEnd.confPath));
			properties.store(outFile,"eMaku RPM Config File");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();		
		if ("CLOSE".equals(action)) {
			setVisible(false);
		}
		if ("SAVE".equals(action)) {
			saveData();
		}
	}
	
}
