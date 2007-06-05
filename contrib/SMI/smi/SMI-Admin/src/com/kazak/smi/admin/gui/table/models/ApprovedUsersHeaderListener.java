package com.kazak.smi.admin.gui.table.models;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

public class ApprovedUsersHeaderListener extends MouseAdapter {
	
    JTableHeader   header;
    ApprovedUsersModel    model;
    SortButtonRenderer renderer;
    
    public ApprovedUsersHeaderListener(JTableHeader header, ApprovedUsersModel model, SortButtonRenderer renderer) {
      this.header   = header;
      this.model    = model;
      this.renderer = renderer;
    }
      
    public void mousePressed(MouseEvent e) {
      int index = header.columnAtPoint(e.getPoint());
      boolean isAscent;
      renderer.setPressedColumn(index);
      renderer.setSelectedColumn(index);
      header.repaint();
      if (SortButtonRenderer.DOWN == renderer.getState(index)) {
        isAscent = false;
      } else {
        isAscent = true;
      }
      model.updateTable(index, isAscent);	      	
    }

    public void mouseReleased(MouseEvent e) {
        renderer.setPressedColumn(-1); // clear
        header.repaint();    	
    }
        
    public void initHeader() {
        renderer.setPressedColumn(0);
        renderer.setColumnForUpdate();
        header.repaint();
        model.updateTable(0, true);	      	
    }
            
  }