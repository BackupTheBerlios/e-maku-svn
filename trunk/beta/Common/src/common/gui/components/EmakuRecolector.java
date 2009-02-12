package common.gui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;


/**
 * EmakuImportExcelFile.java Creado el 12-feb-2009
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

public class EmakuRecolector extends JPanel implements Couplable, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9088188780947712582L;
	private JButton JBunitech; 
	private JButton JBmetrologic; 
	private Element element;
	private ArrayList<RecordListener> recordListener = new ArrayList<RecordListener>();
	private File file;
	private RandomAccessFile raf;


	public EmakuRecolector(GenericForm GFforma, Document doc) {
		JBunitech = new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_lector_b_16x16.png")));
		JBmetrologic = new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_lector_n_16x16.png")));
		this.add(JBunitech);
		this.add(JBmetrologic);
		JBunitech.setName("unitech");
		JBmetrologic.setName("metrologic");
		JBunitech.addActionListener(this);
		JBmetrologic.addActionListener(this);
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

	public void notificando() {
		System.out.println("notificando");
		RecordEvent event = new RecordEvent(this,element);
		for(RecordListener l:recordListener) {
			l.arriveRecordEvent(event);
		}
		event = null;
	}
	
	public void addRecordListener(RecordListener listener) {
		recordListener.add(listener);
	}

	public  void removeRecordListener(RecordListener listener) {
		recordListener.remove(listener);
	}

	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub

	}

	public void arriveAnswerEvent(AnswerEvent e) {
	}

	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		final JButton button = (JButton)e.getSource();
        try{
        	Thread h = new Thread() {
        		public void run () {
                	String linea=null;
                    Process p=null;
					try {
						if (button.getName().equals("unitech")) {
							p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/C", "Start","C://recolector_unit.bat"});		
						}
						else {
							p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/C", "Start","C://recolector_sp2.bat"});
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    BufferedReader input = new BufferedReader (new InputStreamReader (p.getInputStream()));
                    try {
						while ((linea = input.readLine()) != null) {
						     System.out.println(linea);
						 }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
        		}
        	};
        	h.start();
        	//aplicacion.exec("cmd.exe /K C:/recolector_sp2.bat");
    		while (file==null || !file.exists()) {
        		file = new File("c:","sp2datos.txt");
    			System.out.print(".");
    			Thread.sleep(500);
    		}
    		raf = new RandomAccessFile(file,"r");
    		String line = new String();
			element = new Element("table");
    		while ((line=raf.readLine())!=null) {
    			Element rows = new Element("row");
    			StringTokenizer STlinea = new StringTokenizer(line,",");
    			rows.addContent(new Element("col").setText(STlinea.nextToken()));
    			rows.addContent(new Element("col").setText(STlinea.nextToken()));
    			element.addContent(rows);
    		}
    		
    		raf.close();    		
    		notificando();
        }
        catch(IOException IOe) {
        	IOe.printStackTrace();
        } catch (InterruptedException IEe) {
			// TODO Auto-generated catch block
			IEe.printStackTrace();
		}
        finally {
			file = null;
			element = null;
			System.gc();
		}


	}

}
