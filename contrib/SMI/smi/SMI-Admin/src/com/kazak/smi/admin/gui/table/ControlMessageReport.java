package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.gui.misc.XLSExporter;
import com.kazak.smi.admin.gui.misc.ExtensionFilter;
import com.kazak.smi.admin.gui.table.OfflineUsersTable;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.network.MailSender;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

// This class shows a complete report of control messages at specific date/hour

public class ControlMessageReport extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JButton xlsButton;
	private JButton mailButton;
	private JButton closeButton;
	private String[] pageTitles = new String[4];
	private int columns[] = new int[4];
	private TableColumnModel[] columnsModelArray = new TableColumnModel[4];
	private String date, hour;
	private static JDialog dialog;
	private String fileName;
	private XLSExporter xls;

	public ControlMessageReport(JFrame frame,
			Document[] docs, String date, String hour){
		super(frame, true);
		setTitle("Reporte de Mensajes de Control [" + date + "/" + hour + "]");
		this.date = date;
		this.hour = hour;
		dialog = this;
		fileName = "reporteControl_" + date + "_" + hour;
		        				
		ApprovedUsersTable table0 = new ApprovedUsersTable(docs[0]);
		columnsModelArray[0] = table0.getColumnModel();
		table0.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		columns[0] = table0.getColumnCount();
		JScrollPane jscroll0 = new JScrollPane(table0);
		jscroll0.setAutoscrolls(true);
		
		LateUsersTable table1 = new LateUsersTable(docs[1]);
		columnsModelArray[1] = table1.getColumnModel();
		table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columns[1] = table1.getColumnCount();
		JScrollPane jscroll1 = new JScrollPane(table1);
		jscroll1.setAutoscrolls(true);
				
		NoAnswerUsersTable table2 = new NoAnswerUsersTable(docs[2]);
		columnsModelArray[2] = table2.getColumnModel();
		table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columns[2] = table2.getColumnCount();
		JScrollPane jscroll2 = new JScrollPane(table2);
		jscroll2.setAutoscrolls(true);
		
		OfflineUsersTable table3 = new OfflineUsersTable(docs[3]);
		columnsModelArray[3] = table3.getColumnModel();
		table3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columns[3] = table3.getColumnCount();
		JScrollPane jscroll3 = new JScrollPane(table3);
		jscroll3.setAutoscrolls(true);
		
		int total0 = getResultSize(docs[0]);
		int total1 = getResultSize(docs[1]);
		int total2 = getResultSize(docs[2]);
		int total3 = getResultSize(docs[3]);
		
		pageTitles[0] = "Aprobados R "+total0;
		pageTitles[1] = "Reprobados 1 RT "+total1;
		pageTitles[2] = "Reprobados 2 NR "+total2;
		pageTitles[3] = "Reprobados 3 NC "+total3;
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Aprobados [Respondieron a Tiempo:"+total0+"]", jscroll0);
		tabbedPane.add("Reprobados [Respondieron Tarde:"+total1+"]", jscroll1);
		tabbedPane.add("Reprobados [No Respondieron:"+total2+"]", jscroll2);
		tabbedPane.add("Reprobados [No Conectados:"+total3+"]", jscroll3);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(tabbedPane);
		
		xlsButton = new JButton("Exportar a Hoja de Cálculo");
		xlsButton.setActionCommand("export");
		xlsButton.addActionListener(this);
		mailButton = new JButton("Enviar a mi Correo");
		mailButton.setActionCommand("mail");
		mailButton.addActionListener(this);		
		closeButton = new JButton("Cerrar");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(xlsButton);
		southPanel.add(mailButton);
		southPanel.add(closeButton);
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(centerPanel, BorderLayout.CENTER);
		main.add(southPanel, BorderLayout.SOUTH);		
		
		xls = new XLSExporter(docs,pageTitles,columnsModelArray,date,hour);
		
		add(main,BorderLayout.CENTER);
        pack();
        setSize(570,380);
        setLocationRelativeTo(frame);
        setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("export")) {
			JFileChooser chooser = new JFileChooser();
			ExtensionFilter filter = new ExtensionFilter("xls","Hoja de Cálculo");
			chooser.addChoosableFileFilter(filter);
			chooser.setSelectedFile(new File(fileName + ".xls"));

			while(true) {
				boolean write = true;
				int returnVal = chooser.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					int result = -1;
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						// Overwrite file?
						result = JOptionPane.showConfirmDialog(this,"Desea sobreescribir el archivo \"" 
								+ fileName + "\"? ");
						if(result == 2 || result == 1) {
							write = false;
						}			    		
					}
					if (write) {
						saveXLSFile(xls.getXLSFile(),file);
						break;
					}
				} else {
					break;
				}
			}
		}		
		if (command.equals("mail")) {
			loadMailParams();
			requestMailInfo();
		}			
		if (command.equals("close")) {
			this.dispose();
		}			
	}
	
	// This method requests the total of users online 
	public void requestMailInfo() {
		Element onlist = new Element("GETMAILINFO");
		Element id = new Element("id").setText("MAILPARAMS");
		onlist.addContent(id);
		Document document = new Document(onlist);
		if (document!=null) {
			try {
				SocketWriter.write(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("ERROR: Falla de entrada/salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	
	// Gets the total of users online
	private void loadMailParams() {
		class Monitor extends Thread {
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetFromST("MAILPARAMS");
		    		MailSender mail = new MailSender(dialog,doc,date,hour,getXLSFile(xls.getXLSFile()));
		    		if(mail.sentOk()) {
		    			mailButton.setEnabled(false);
		    		}
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}			
		}
		new Monitor().start();
	}
	
	private int getResultSize(Document doc) {
        List messagesList = doc.getRootElement().getChildren("row");
        return messagesList.size();
	}
	
	private void saveXLSFile(HSSFWorkbook wb,File file) {
		try {
			FileOutputStream outputFile = new FileOutputStream(file);
			wb.write(outputFile);
			outputFile.close();
			JOptionPane.showMessageDialog(
					this,
			"Archivo guardado con éxito. ");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("No se pudo procesar la operación:\n" +
					"Causa:\n"+e.getMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No se pudo procesar la operación:\n" +
					"Causa:\n"+e.getMessage()); 
		}
	}
	
	// This method returns the attachment for a control report mail  
	private File getXLSFile(HSSFWorkbook wb) {
		File file = null;
		try {
			file = File.createTempFile(fileName,".xls");
			FileOutputStream outputFile = new FileOutputStream(file);
			wb.write(outputFile);
			outputFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("No se pudo procesar la operación:\n" +
					"Causa:\n"+e.getMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No se pudo procesar la operación:\n" +
					"Causa:\n"+e.getMessage()); 
		}
			
		return file;
	}
}
