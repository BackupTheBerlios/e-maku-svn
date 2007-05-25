package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.admin.gui.table.OfflineUsersTable;

public class ControlMessageReport extends JDialog implements ActionListener, MouseListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	JButton xlsButton;
	JButton closeButton;

	public ControlMessageReport(JFrame frame,
			Document goodUsers,Document lateUsers,
			Document noAnswerUsers,Document offlineUsers, String title){
		
		super(frame, true);
		setTitle("Reporte de Mensajes de Control [" + title + "]");
		
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(goodUsers,System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        				
		ApprovedUsersTable table1 = new ApprovedUsersTable(goodUsers);
		table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table1.addMouseListener(this);
		table1.addKeyListener(this);		
		JScrollPane jscroll = new JScrollPane(table1);
		jscroll.setAutoscrolls(true);
		
		LateUsersTable table2 = new LateUsersTable(lateUsers);
		table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table2.addMouseListener(this);
		table2.addKeyListener(this);		
		JScrollPane jscroll2 = new JScrollPane(table2);
		jscroll.setAutoscrolls(true);
				
		NoAnswerUsersTable table3 = new NoAnswerUsersTable(noAnswerUsers);
		table3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table3.addMouseListener(this);
		table3.addKeyListener(this);		
		JScrollPane jscroll3 = new JScrollPane(table3);
		jscroll.setAutoscrolls(true);
		
		OfflineUsersTable table4 = new OfflineUsersTable(offlineUsers);
		table4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table4.addMouseListener(this);
		table4.addKeyListener(this);		
		JScrollPane jscroll4 = new JScrollPane(table4);
		jscroll.setAutoscrolls(true);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Aprobados [Respondieron a Tiempo:"+getResultSize(goodUsers)+"]", jscroll);		
		tabbedPane.add("Reprobados [Respondieron Tarde:"+getResultSize(lateUsers)+"]", jscroll2);
		tabbedPane.add("Reprobados [No Respondieron:"+getResultSize(noAnswerUsers)+"]", jscroll3);
		tabbedPane.add("Reprobados [No Conectados:"+getResultSize(offlineUsers)+"]", jscroll4);
		
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

		}		
		if (command.equals("close")) {
			this.dispose();
		}			
	}
	
	private int getResultSize(Document doc) {
        List messagesList = doc.getRootElement().getChildren("row");
        return messagesList.size();
	}

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
		
	}

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
