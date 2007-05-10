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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.util.HashMap;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.UsersList;
import com.kazak.smi.admin.gui.table.UsersTable;

public class GroupsSearchPanel extends JPanel implements PopupMenuListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private JComboBox groups;
	private JLabel groupsLabel;
	private HashMap<String,String> groupsHash;
	private UsersTable table;
	private JButton update,close;
	private UsersList frame;
	
	public GroupsSearchPanel(UsersList frame) {

		this.frame = frame;
		this.setLayout(new BorderLayout());
		groupsLabel = new JLabel("Grupos:");
		groups = new JComboBox(Cache.getGroupsList());
		groupsHash = Cache.getGroupsHash();		
		groups.addPopupMenuListener(this);
		frame.updateList(groups.getSelectedItem().toString());		

		table = new UsersTable();
		JScrollPane js = new JScrollPane(table);
		js.setPreferredSize(new Dimension(500,300));
		js.setAutoscrolls(true);

		update = new JButton("Actualizar");
		update.setActionCommand("update");
		update.addActionListener(this);

		close = new JButton("Cerrar");
		close.setActionCommand("close");
		close.addActionListener(this);

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(groupsLabel);
		top.add(groups);

		JPanel down = new JPanel();
		down.setLayout(new FlowLayout(FlowLayout.CENTER));
		down.add(update);
		down.add(close);
		
		add(top,BorderLayout.NORTH);
		add(js,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
