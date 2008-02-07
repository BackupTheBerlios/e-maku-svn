package common.gui.forms;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jdom.*;

import common.control.*;
import common.misc.*;

/**
 * 
 * XLSReceiver.java Created at 05-feb-2008
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * 
 * <br>
 * 
 * @author Cristian Cepeda [cristian@qhatu.net]
 */
public class XLSReceiver extends JInternalFrame implements ReportListener {

	private static final long serialVersionUID = 1L;
	private String idReport;
	private JProgressBar progressBar;
	private ByteArrayInputStream bin;

	// TODO Pendiente traduccion.
	public XLSReceiver(GenericForm fforma, String idReport) {
		super("Reporte");
		ClientHeaderValidator.addReportListener(this);
		this.idReport = idReport;
		this.setLayout(new BorderLayout());
		this.setSize(200, 45);
		this.setClosable(false);
		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		progressBar = new JProgressBar();
		
		progressBar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		progressBar.setForeground(new Color(255, 162, 0));
		this.add(progressBar,BorderLayout.CENTER);
	}

	public void arriveReport(ReportEvent e) {
		if (!e.getIdReport().equals(idReport)) {
			return;
		}
		Element element = e.getData();
		this.setTitle(e.getTitleReport());
		ZipHandler zh = new ZipHandler();
		try {
			progressBar.setIndeterminate(false);
			progressBar.setMinimum(0);
			progressBar.setMaximum(10);
			progressBar.setValue(10);
			this.setVisible(false);
			bin = zh.getDataDecodeInputStream(element.getText());
			String str= "El reporte no a sido guardado";
			String title = "Reporte";
			int typeMessage = JOptionPane.ERROR_MESSAGE;
			if (save()) {
				str="Reporte guardado";
				typeMessage= JOptionPane.INFORMATION_MESSAGE;
			}
			JOptionPane.showMessageDialog(this,str,title,typeMessage);
			bin.close();
			bin = null;
			this.dispose();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	private boolean save() {
		File home = new File(System.getProperty("user.home"));
		JFileChooser fch = new JFileChooser(home);
		fch.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int op = fch.showSaveDialog(this);
		boolean ret = false;
		if (op==JFileChooser.APPROVE_OPTION) {
			File file = fch.getSelectedFile();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[255];
				int len = 0;
				while ((len = bin.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				ret=true;
			} catch (IOException e) {
				e.printStackTrace();
				ret=false;
			}
		}
		return ret;
	}
}