package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.MessagesTable;

class MessagesDialog extends JDialog implements ActionListener, MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private MessagesTable table;
	private JButton closeButton;
	private JTextArea messageArea;
	
	public MessagesDialog(JFrame frame, String login, Document doc) {
		super(frame, true);
		setTitle("Mensajes del usuario " + login);
		table = new MessagesTable(doc);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(this);
		table.addKeyListener(this);
		JScrollPane jscroll = new JScrollPane(table);
		jscroll.setAutoscrolls(true);
		
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		JScrollPane jscroll2 = new JScrollPane(messageArea);
		jscroll2.setAutoscrolls(true);
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(jscroll);
		split.setBottomComponent(jscroll2);
		jscroll.setPreferredSize(new Dimension(660,300));
		split.setDividerLocation(160);
	
		closeButton = new JButton("Cerrar");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(closeButton);
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(split, BorderLayout.CENTER);
		main.add(southPanel, BorderLayout.SOUTH);		
		getContentPane().add(main);
        pack();
        setSize(570,380);
        setLocationRelativeTo(frame);
        setResizable(false);
        setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("close")) {
			this.dispose();
		}		
	}

	public void mouseClicked(MouseEvent e) {
		setMessage((MessagesTable)e.getSource(),0);
	}
	
	public void setMessage(MessagesTable table, int i) {
		int index = table.getSelectedRow();
		int max = table.getRowCount();
		index = index + i;
		if (index != -1 && index < max) {
			messageArea.setText(table.getModel().getMessage(index));
			messageArea.setCaretPosition(0);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == 38) {
			setMessage((MessagesTable)e.getSource(),-1);		    
		}
		if (code == 40) {
			setMessage((MessagesTable)e.getSource(),1);		    
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}