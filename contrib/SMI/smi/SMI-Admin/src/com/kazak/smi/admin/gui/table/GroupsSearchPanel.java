package com.kazak.smi.admin.gui.table;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdom.Document;

import java.util.HashMap;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.UsersList;
import com.kazak.smi.admin.gui.table.OnLineUsersTable;

public class GroupsSearchPanel extends JPanel implements PopupMenuListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JComboBox groups;
	private JLabel groupsLabel;
	private JLabel groupSize;
	private HashMap<String,String> groupsHash;
	private OnLineUsersTable table;
	private JButton update,viewMsg,close;
	private UsersList frame;
	
	public GroupsSearchPanel(UsersList frame) {

		this.frame = frame;
		this.setLayout(new BorderLayout());
		groupsLabel = new JLabel("Grupo:");
		groups = new JComboBox(Cache.getGroupsList());
		groupSize = new JLabel("");
		groupsHash = Cache.getGroupsHash();		
		groups.addPopupMenuListener(this);
		
		table = new OnLineUsersTable(frame);
		table.setModelTab(1);
		table.getModel().setLabel(groupSize);
		JScrollPane jscroll = new JScrollPane(table);
		jscroll.setPreferredSize(new Dimension(500,300));
		jscroll.setAutoscrolls(true);

		update = new JButton("Actualizar Listado");
		update.setActionCommand("update");
		update.addActionListener(this);

		viewMsg = new JButton("Ver Mensajes");
		viewMsg.setActionCommand("view");
		viewMsg.addActionListener(this);
		viewMsg.setEnabled(false);
		table.setListButton(viewMsg);
		
		close = new JButton("Cerrar");
		close.setActionCommand("close");
		close.addActionListener(this);

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(groupsLabel);
		top.add(groups);
		top.add(groupSize);

		JPanel down = new JPanel();
		down.setLayout(new FlowLayout(FlowLayout.CENTER));
		down.add(update);
		down.add(viewMsg);
		down.add(close);
		
		add(top,BorderLayout.NORTH);
		add(jscroll,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {		
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		updateList();
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("update")) {
			updateList();
		}
		if (command.equals("view")) {
			int row = table.getSelectedRow();
			if(row != -1) {
				String login = (String) table.getModel().getValueAt(row, 0);
				new MessagesDialog(frame,login);
			}
		}
		if (command.equals("close")) {
			frame.dispose();
		}
	}

	private void updateList() {
		frame.updateGroupTable();
	}
	
	public void setEnableListButton() {
        if (table.getGroupSize() == 0 && viewMsg.isEnabled()) {
    		viewMsg.setEnabled(false);
        }
	}
	
	public OnLineUsersTable getTable(){
		return table;
	}
	
	public String getGroupsSelection() {
		return (String) groups.getSelectedItem();
	}
	
	public String getGroupID() {
		return groupsHash.get(getGroupsSelection());
	}
	
	public void updateUserList(Document doc) {
        table.getModel().setQuery(doc);
 	}
	
}
