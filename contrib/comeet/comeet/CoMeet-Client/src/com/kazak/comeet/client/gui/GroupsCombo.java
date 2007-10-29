package com.kazak.comeet.client.gui;

import javax.swing.JComboBox;

import com.kazak.comeet.client.control.Cache;

public class GroupsCombo extends JComboBox {

	private static final long serialVersionUID = -2117876634397368886L;
		
	public GroupsCombo () {
		super(Cache.getGroups());
	}
}