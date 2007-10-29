package com.kazak.comeet.admin.gui.table;

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

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.gui.misc.UsersOnlineFrame;
import com.kazak.comeet.admin.gui.table.UsersOnlineTable;

public class GroupsSearchPanel extends JPanel implements PopupMenuListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JComboBox groups;
	private JLabel groupsLabel;
	private JLabel groupSize;
	private HashMap<String,String> groupsHash;
	private UsersOnlineTable groupPanelTable;
	private JButton update,viewMsg,close;
	private UsersOnlineFrame frame;
	
	public GroupsSearchPanel(UsersOnlineFrame frame) {

		this.frame = frame;
		this.setLayout(new BorderLayout());
		groupsLabel = new JLabel("Grupo:");
		groups = new JComboBox(Cache.getGroupsList());
		groupSize = new JLabel("");
		groupsHash = Cache.getGroupsHash();		
		groups.addPopupMenuListener(this);
		
		groupPanelTable = new UsersOnlineTable(frame);
		groupPanelTable.setModelTab(1);
		groupPanelTable.getModel().setLabel(groupSize);
		JScrollPane jscroll = new JScrollPane(groupPanelTable);
		jscroll.setPreferredSize(new Dimension(500,300));
		jscroll.setAutoscrolls(true);

		update = new JButton("Actualizar Listado");
		update.setMnemonic('A');
		update.setActionCommand("update");
		update.addActionListener(this);

		viewMsg = new JButton("Ver Mensajes");
		viewMsg.setMnemonic('V');
		viewMsg.setActionCommand("view");
		viewMsg.addActionListener(this);
		viewMsg.setEnabled(false);
		groupPanelTable.setListButton(viewMsg);
		
		close = new JButton("Cerrar");
		close.setMnemonic('C');
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
			int row = groupPanelTable.getSelectedRow();
			if(row != -1) {
				String login = (String) groupPanelTable.getModel().getValueAt(row, 0);
				groupPanelTable.getMessages(login);
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
        if (groupPanelTable.getResultSize() == 0 && viewMsg.isEnabled()) {
    		viewMsg.setEnabled(false);
        }
	}
	
	public UsersOnlineTable getTable(){
		return groupPanelTable;
	}
	
	public String getGroupsSelection() {
		return (String) groups.getSelectedItem();
	}
	
	public String getGroupID() {
		return groupsHash.get(getGroupsSelection());
	}
	
	public void updateUserList(Document doc) {
        groupPanelTable.getModel().setQuery(doc);
 	}
	
}
