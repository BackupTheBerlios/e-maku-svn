package common.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.misc.language.Language;


/**
 * EmakuImportExcelFile.java Creado el 09-feb-2007
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * <br>
 * @author <A href='mailto:felipe@gmail.com'>Luis Felipe Hernandez</A>
 */

public class EmakuImportExcelFile extends JPanel implements Couplable,ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9137825185914807588L;
	private JButton JBopen; 
	private JFileChooser filechooser = new JFileChooser();
	private GenericForm GFforma;
	private Document doc;
	private Vector<RecordListener> recordListener = new Vector<RecordListener>();
	
	private File file;
	private Workbook workbook;
	private Element element;
	private Element rows = null;

	public EmakuImportExcelFile(GenericForm GFforma, Document doc) {
		this.GFforma=GFforma;
		this.doc=doc;
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JBopen = new JButton();
		JBopen.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_excel.png")));
		JBopen.setToolTipText(Language.getWord("IMPORT_EXCEL_FILE"));
		this.add(JBopen);
		JBopen.addActionListener(this);
	}
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	public boolean containData() {
		// TODO Auto-generated method stub
		return false;
	}

	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getPackage() throws VoidPackageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Component getPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub
		
	}
	public void actionPerformed(ActionEvent e) {
		int returnVal = filechooser.showSaveDialog(GFforma.getParent());

	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	readExcelFile(filechooser.getSelectedFile());
	    } 
		
	}
	
	private void readExcelFile(File file) {
		class ImportarExcel extends Thread {
			private File file;
			public ImportarExcel(File file) {
				this.file = file;
			}
			public void run() {
				try {
					workbook = Workbook.getWorkbook(file);
					element = new Element("table");
					//for (Sheet sheet:workbook.getSheets()) {
					Sheet sheet = workbook.getSheet(0);
						 for(int i=3;i<sheet.getRows();i++) {
							 Cell[] celdas = sheet.getRow(i);
							 rows = new Element("row");
							 for(Cell celda:celdas) {
							 //for (int j=2;j<5;j++) {
								 //Cell celda = celdas[j];
								 rows.addContent(new Element("col").setText(celda.getContents()));
							 }
							 element.addContent(rows);
						 }
					//}
					 notificando();
				} 
				catch (BiffException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					file = null;
					workbook = null;
					element = null;
					rows = null;
					System.gc();
				}
			}
		}
	
		new ImportarExcel(file).start();
	}
	
	public void notificando() {
		RecordEvent event = new RecordEvent(this,element);
		
		synchronized(recordListener) {
			for(RecordListener l:recordListener) {
				l.arriveRecordEvent(event);
			}
		}
		event = null;
	}
	
	public void addRecordListener(RecordListener listener) {
		recordListener.addElement(listener);
	}

	public  void removeRecordListener(RecordListener listener) {
		recordListener.removeElement(listener);
	}
	public boolean containSqlCode(String sqlCode) {
		return false;
	}

}
