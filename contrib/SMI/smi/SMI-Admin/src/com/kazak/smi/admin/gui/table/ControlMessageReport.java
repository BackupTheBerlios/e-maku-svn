package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.util.Iterator;
import java.util.List;
//import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.misc.XLSExporter;
import com.kazak.smi.admin.gui.table.OfflineUsersTable;

public class ControlMessageReport extends JDialog implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private JButton xlsButton;
	private JButton closeButton;
	private Document[] docs;
	private String[] pageTitles = new String[4];
	private int columns[] = new int[4];

	public ControlMessageReport(JFrame frame,
			Document[] docs, String title){
		super(frame, true);
		this.docs = docs;
		setTitle("Reporte de Mensajes de Control [" + title + "]");
		        				
		ApprovedUsersTable table0 = new ApprovedUsersTable(docs[0]);
		table0.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table0.addKeyListener(this);	
		columns[0] = table0.getColumnCount();
		JScrollPane jscroll0 = new JScrollPane(table0);
		jscroll0.setAutoscrolls(true);
		
		LateUsersTable table1 = new LateUsersTable(docs[1]);
		table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table1.addKeyListener(this);
		columns[1] = table1.getColumnCount();
		JScrollPane jscroll1 = new JScrollPane(table1);
		jscroll1.setAutoscrolls(true);
				
		NoAnswerUsersTable table2 = new NoAnswerUsersTable(docs[2]);
		table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table2.addKeyListener(this);
		columns[2] = table2.getColumnCount();
		JScrollPane jscroll2 = new JScrollPane(table2);
		jscroll2.setAutoscrolls(true);
		
		OfflineUsersTable table3 = new OfflineUsersTable(docs[3]);
		table3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table3.addKeyListener(this);
		columns[3] = table3.getColumnCount();
		JScrollPane jscroll3 = new JScrollPane(table3);
		jscroll3.setAutoscrolls(true);
		
		pageTitles[0] = "Aprobados [Respondieron a Tiempo:"+getResultSize(docs[0])+"]";
		pageTitles[1] = "Reprobados [Respondieron Tarde:"+getResultSize(docs[1])+"]";
		pageTitles[2] = "Reprobados [No Respondieron:"+getResultSize(docs[2])+"]";
		pageTitles[3] = "Reprobados [No Conectados:"+getResultSize(docs[3])+"]";
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(pageTitles[0], jscroll0);		
		tabbedPane.add(pageTitles[1], jscroll1);
		tabbedPane.add(pageTitles[2], jscroll2);
		tabbedPane.add(pageTitles[3], jscroll3);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(tabbedPane);
		
		xlsButton = new JButton("Exportar Reporte");
		xlsButton.setActionCommand("export");
		xlsButton.addActionListener(this);
		closeButton = new JButton("Cerrar");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(xlsButton);
		southPanel.add(closeButton);
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(centerPanel, BorderLayout.CENTER);
		main.add(southPanel, BorderLayout.SOUTH);		
		
		add(main,BorderLayout.CENTER);
        pack();
        setSize(570,380);
        setLocationRelativeTo(frame);
        setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("export")) {
			JFileChooser chooser = new JFileChooser("reporte-FECHA.xls");
		    int returnVal = chooser.showSaveDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	File file = chooser.getSelectedFile();
			    XLSExporter xls = new XLSExporter(docs,pageTitles,columns);
			    saveXLSFile(xls.getXLSFile(),file);
		    }
		}		
		if (command.equals("close")) {
			this.dispose();
		}			
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
	
	/*
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}*/

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
