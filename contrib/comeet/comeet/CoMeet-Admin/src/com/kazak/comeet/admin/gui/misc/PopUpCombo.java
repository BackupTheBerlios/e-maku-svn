package com.kazak.comeet.admin.gui.misc;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PopUpCombo {
	private JPopupMenu refresh = new JPopupMenu();
	private JPopupMenu specialGroupsMenu = new JPopupMenu();
	private JPopupMenu wsMenu = new JPopupMenu();
	private JPopupMenu usersMenu = new JPopupMenu();
	private JPopupMenu groupsMenu = new JPopupMenu(); 
	private ActionListener actions;
	
    public PopUpCombo(ActionListener actions) {
    	this.actions = actions;
    	setRefreshPopUp();
    	setGroupPopUp();
    	setSpecialGroupPopUp();
    	setPosPopUp();
    	setUserPopUp();
    }
    
	private void setRefreshPopUp() {
		refresh = new JPopupMenu();
		JMenuItem item = new JMenuItem("Recargar");
        item.setActionCommand("reload");
        item.addActionListener(actions);
        refresh.add(item);
	}
	
	private JMenu getGroupMenu() {
        JMenu submenu = new JMenu("Grupos");
		
		JMenuItem item = new JMenuItem("Editar");
        item.setActionCommand("edit_group");
        item.addActionListener(actions);
        submenu.add(item);
        
        item = new JMenuItem("Eliminar");
      	item.setActionCommand("delete_group");
      	item.addActionListener(actions);
        submenu.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_group");
        item.addActionListener(actions);
        submenu.add(item);
        
		item = new JMenuItem("Nuevo");
        item.setActionCommand("new_group");
        item.addActionListener(actions);
        submenu.add(item);

        return submenu;
	}
	
	private JMenu getExtendedGroupMenu() {
        JMenu submenu = new JMenu("Grupos");
		
		JMenuItem item = new JMenuItem("Editar");
        item.setActionCommand("edit_unknown_group");
        item.addActionListener(actions);
        submenu.add(item);
        
        item = new JMenuItem("Eliminar");
      	item.setActionCommand("delete_unknown_group");
      	item.addActionListener(actions);
        submenu.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_unknown_group");
        item.addActionListener(actions);
        submenu.add(item);
        
		item = new JMenuItem("Nuevo");
        item.setActionCommand("new_group");
        item.addActionListener(actions);
        submenu.add(item);

        return submenu;
	}	
	
	private JMenu getSpecialGroupMenu() {
		JMenu submenu = new JMenu("Grupos");

		JMenuItem item = new JMenuItem("Nuevo");
		item.setActionCommand("new_group");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_group");
		item.addActionListener(actions);
		submenu.add(item);

		return submenu;
	}
	
	private JMenu getPosMenu() {
        JMenu submenu = new JMenu("Puntos");

		JMenuItem item = new JMenuItem("Editar");
		item.setActionCommand("edit_point");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Eliminar");
		item.setActionCommand("delete_point");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_point");
		item.addActionListener(actions);
		submenu.add(item);
		
		item = new JMenuItem("Nuevo");
		item.setActionCommand("new_point");
		item.addActionListener(actions);
		submenu.add(item);

		return submenu;
	}
	
	private JMenu getSpecialPosMenu() {
        JMenu submenu = new JMenu("Puntos");

		JMenuItem item = new JMenuItem("Editar");
		item.setActionCommand("edit_unknown_point");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Eliminar");
		item.setActionCommand("delete_unknown_point");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_unknown_point");
		item.addActionListener(actions);
		submenu.add(item);
		
		item = new JMenuItem("Nuevo");
		item.setActionCommand("new_point");
		item.addActionListener(actions);
		submenu.add(item);

		return submenu;
	}
	
	private JMenu getUserMenu() {
		JMenu submenu = new JMenu("Usuarios");

		JMenuItem item = new JMenuItem("Editar");
		item.setActionCommand("edit_user");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Eliminar");
		item.setActionCommand("delete_user");
		item.addActionListener(actions);
		submenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_user");
		item.addActionListener(actions);
		submenu.add(item);

		JMenu users = new JMenu("Nuevo");
		
		item = new JMenuItem("Administrativo");
		item.setActionCommand("new_admin_user");
		item.addActionListener(actions);
		users.add(item);

		item = new JMenuItem("Operativo");
		item.setActionCommand("new_op_user");
		item.addActionListener(actions);
		users.add(item);
		submenu.add(users);
		
		return submenu; 
	}
	
	private JMenu getSpecialUserMenu() {
		JMenu submenu = new JMenu("Usuarios");
		JMenu adminmenu = new JMenu("Administrativos");
		JMenu opmenu = new JMenu("Operativos");

		JMenuItem item = new JMenuItem("Editar");
		item.setActionCommand("edit_admin_user");
		item.addActionListener(actions);
		adminmenu.add(item);

		item = new JMenuItem("Eliminar");
		item.setActionCommand("delete_admin_user");
		item.addActionListener(actions);
		adminmenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_admin_user");
		item.addActionListener(actions);
		adminmenu.add(item);

		item = new JMenuItem("Nuevo");
		item.setActionCommand("new_admin_user");
		item.addActionListener(actions);
		adminmenu.add(item);
		
		item = new JMenuItem("Editar");
		item.setActionCommand("edit_op_user");
		item.addActionListener(actions);
		opmenu.add(item);

		item = new JMenuItem("Eliminar");
		item.setActionCommand("delete_op_user");
		item.addActionListener(actions);
		opmenu.add(item);

		item = new JMenuItem("Buscar");
		item.setActionCommand("search_op_user");
		item.addActionListener(actions);
		opmenu.add(item);

		item = new JMenuItem("Nuevo");
		item.setActionCommand("new_op_user");
		item.addActionListener(actions);
		opmenu.add(item);
				
		submenu.add(adminmenu);
		submenu.add(opmenu);

		return submenu; 
	}
	
	private void setGroupPopUp() {
		//--- Popup Grupos                		
        groupsMenu.add(getGroupMenu());
        groupsMenu.add(new JSeparator());
        groupsMenu.add(getSpecialPosMenu());
        groupsMenu.add(getSpecialUserMenu());
	}
	
	private void setSpecialGroupPopUp() {
		//--- Popup Grupos Especiales
        specialGroupsMenu.add(getSpecialGroupMenu());        
        specialGroupsMenu.add(new JSeparator());
        specialGroupsMenu.add(getSpecialPosMenu());
        specialGroupsMenu.add(getSpecialUserMenu());        
	}

	private void setPosPopUp() {
		//--- Popup Puntos Venta		
        wsMenu.add(getPosMenu());
		wsMenu.add(new JSeparator());
		wsMenu.add(getExtendedGroupMenu());
        wsMenu.add(getSpecialUserMenu());        
	}
	
	private void setUserPopUp() {
		//--- Popup Usuarios		
        usersMenu.add(getUserMenu());
		usersMenu.add(new JSeparator());
		usersMenu.add(getExtendedGroupMenu());
		usersMenu.add(getSpecialPosMenu());
	}
	
	public void showMenu(int menu, Component c, int x, int y) {
		switch(menu) {
		case 0:
			groupsMenu.show(c,x,y);
			break;
		case 1:
			specialGroupsMenu.show(c,x,y);
			break;
		case 2:
			usersMenu.show(c,x,y);
			break;
		case 3:
			wsMenu.show(c,x,y);
			break;
		case 4:
			refresh.show(c,x,y);
			break;
		}
	}
}
