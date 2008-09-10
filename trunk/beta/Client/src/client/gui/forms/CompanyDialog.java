package client.gui.forms;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.jdom.Element;

import common.misc.language.Language;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CompanyDialog.java Creado el 04-ago-2004
 * 
 * Este archivo es parte de eMaku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * eMaku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase tiene como fin realizar la creación del archivo de configuración,
 * en el caso de que no exista. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class CompanyDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	final JTextField JTFName;
	final JTextField JTFJar;
	final JTextField JTFDir;
	final JTextField JTFHost;
	final JTextField JTFPort;
	//private Element root;
	private String name;
	private String oldName;

	public CompanyDialog(JDialog parent,String title,final String label,String name,String jar,String dir,String host, String port) {
		
		super(parent, true);
		oldName = name;	
		this.setTitle(title);
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setAlwaysOnTop(true);

		JPanel JPlabels = new JPanel();
		JPanel JPfields = new JPanel();
		JPlabels.setLayout(new GridLayout(5, 1));
		JPfields.setLayout(new GridLayout(5, 1));

		JLabel JLName = new JLabel(Language.getWord("NAME"));
		JPanel JPLName = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPLName.add(JLName);

		JLabel JLJar = new JLabel(Language.getWord("JAR_FILE"));
		JPanel JPLJar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPLJar.add(JLJar);

		JLabel JLDir = new JLabel(Language.getWord("DIR"));
		JPanel JPLDir = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPLDir.add(JLDir);
		
		JLabel JLHost = new JLabel(Language.getWord("HOST"));
		JPanel JPLHost = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPLHost.add(JLHost);
		
		JLabel JLPort = new JLabel(Language.getWord("PORT"));
		JPanel JPLPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPLPort.add(JLPort);

		JPlabels.add(JPLName);
		JPlabels.add(JPLJar);
		JPlabels.add(JPLDir);
		JPlabels.add(JPLHost);
		JPlabels.add(JPLPort);

		JTFName = new JTextField(10);
		JTFName.setText(name);
		JTFName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTFName.transferFocus();
			}
		});

		JPanel JPTName = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPTName.add(JTFName);

		JTFJar = new JTextField(10);
		JTFJar.setText(jar);
		JTFJar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTFJar.transferFocus();
			}
		});

		JPanel JPTJar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPTJar.add(JTFJar);

		JTFDir = new JTextField(10);
		JTFDir.setText(dir);
		JTFDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTFDir.transferFocus();
			}
		});
		
		JPanel JPTDir = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPTDir.add(JTFDir);
		
		JTFHost = new JTextField(10);
		JTFHost.setText(host);
		JTFHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTFHost.transferFocus();
			}
		});
		
		JPanel JPTHost = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPTHost.add(JTFHost);
		
		JTFPort = new JTextField(10);
		JTFPort.setText(port);
		JTFPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTFPort.transferFocus();
			}
		});
		JPanel JPTPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPTPort.add(JTFPort);

		JPfields.add(JPTName);
		JPfields.add(JPTJar);
		JPfields.add(JPTDir);
		JPfields.add(JPTHost);
		JPfields.add(JPTPort);

		JPanel JPForm = new JPanel(new BorderLayout());
		JPForm.add(JPlabels,BorderLayout.WEST);
		JPForm.add(JPfields,BorderLayout.CENTER);

		JPanel JPsouth = new JPanel();
		JPsouth.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton JBAccept = new JButton(label);
		JBAccept.setMnemonic('A');

		JBAccept.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				packingData(label);
			}
		});

		JBAccept.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					packingData(label);
				}
			}
		});

		JButton JBCancel = new JButton(Language.getWord("CANCEL"));
		JBCancel.setMnemonic(Language.getNemo("CANCEL"));
		JBCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPsouth.add(JBAccept);
		JPsouth.add(JBCancel);

		this.getContentPane().add(JPForm, BorderLayout.CENTER);
		this.getContentPane().add(JPsouth, BorderLayout.SOUTH);
		this.getContentPane().add(new JPanel(), BorderLayout.WEST);
		this.getContentPane().add(new JPanel(), BorderLayout.EAST);
	}

	public String getName() {
		return JTFName.getText().trim();
	}

	public String getJarFile() {
		return JTFJar.getText().trim();
	}

	public String getDirectory() {
		return JTFDir.getText().trim();
	}
	
	public String getHost() {
		return JTFHost.getText().trim();
	}
	
	public String getPort() {
		return JTFPort.getText().trim();
	}

	private void packingData(String label) {

		name              = getName();
		String jarFile    = getJarFile();
		String directory  = getDirectory();
		String host       = getHost();
		String serverport = getPort();

		if (name.length() < 1) {
			JOptionPane.showMessageDialog(this,Language.getWord("EMPTY_NAME"));
			JTFName.requestFocus();
			return;
		}

		if(SettingsDialog.alreadyExists(name)) {
			JOptionPane.showMessageDialog(this,Language.getWord("COMPANY_EXISTS"));
			JTFName.requestFocus();
			return;
		}

		if (jarFile.length() < 1) {
			JOptionPane.showMessageDialog(this,Language.getWord("EMPTY_JAR"));
			JTFJar.requestFocus();
			return;
		}

		if (!(jarFile.toLowerCase().endsWith(".jar"))) {
			jarFile = jarFile + ".jar";
			JTFJar.setText(jarFile);
		}

		if (directory.length() < 1) {
			JOptionPane.showMessageDialog(this,Language.getWord("EMPTY_DIR"));
			JTFDir.requestFocus();
			return;
		}

		Element root = new Element("company");
		root.addContent(new Element("name").setText(name));
		root.addContent(new Element("jarFile").setText(jarFile));
		root.addContent(new Element("directory").setText(directory));
		root.addContent(new Element("host").setText(host));
		root.addContent(new Element("serverport").setText(serverport));

		if(label.equals(Language.getWord("ADD"))) {
			SettingsDialog.addCompany(root,name);
		} else {
			SettingsDialog.editCompany(root,oldName);
		}

		setVisible(false);
	}
}