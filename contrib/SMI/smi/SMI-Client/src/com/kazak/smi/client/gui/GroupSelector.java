package com.kazak.smi.client.gui;

import javax.swing.JComboBox;

import com.kazak.smi.client.control.Cache;

public class GroupSelector extends JComboBox {

	private static final long serialVersionUID = -2117876634397368886L;
		
	public GroupSelector () {
		super(Cache.getGroups());
	}
}