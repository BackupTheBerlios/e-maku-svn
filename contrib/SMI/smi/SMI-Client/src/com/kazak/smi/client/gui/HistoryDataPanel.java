package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import com.kazak.smi.client.control.ConfirmMessage;
import com.kazak.smi.client.control.HeadersValidator;
import com.kazak.smi.client.control.MessageEvent;
import com.kazak.smi.client.control.MessageListener;


public class HistoryDataPanel extends JPanel implements MessageListener {

	private static final long serialVersionUID = 1L;
	private JTable JTData;
	private HistoryDataModel historyDataModel;
	private HistoryMessagePanel messagePanel;
	
	public HistoryDataPanel(HistoryMessagePanel messagePanel) {
		HeadersValidator.addMessageListener(this);
		setLayout(new BorderLayout());
		this.messagePanel = messagePanel;
		historyDataModel = new HistoryDataModel();
		
		JTData = createTable();
		JTData.setModel(historyDataModel);
		JTData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTData.setEnabled(true);
		int columns = historyDataModel.getColumnCount();
		for (int i =0 ; i <  columns ; i++) {
			int width = historyDataModel.getWidth(i);
			JTData.getColumnModel().getColumn(i).setMinWidth(0);
			JTData.getColumnModel().getColumn(i).setPreferredWidth(width);	
		}
		
		JScrollPane jscroll = new JScrollPane(JTData);
		jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
		this.add(new JPanel(),BorderLayout.SOUTH);
		this.add(jscroll,BorderLayout.CENTER);
		addEvents();
	}
	
	private JTable createTable() {
		 JTable table = new JTable() {
			private static final long serialVersionUID = 358108449178930008L;
			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c =  super.prepareRenderer(r, row, col);
				Boolean marked = (Boolean)getValueAt(row,6);
				if (marked && isCellSelected(row, col)) {
					c.setBackground(Color.LIGHT_GRAY);
				}
				else if (marked){
					c.setBackground(Color.WHITE);
				}
				else if (!marked && !isCellSelected(row, col)){
					c.setBackground(new Color(255,255,155));
				}
				return c;
			}
		
		};
		 table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 return table;
	}
	
	private void addEvents() {
		JTData.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int rowIndex = JTData.getSelectedRow();
						if (rowIndex < JTData.getRowCount()) {
							messagePanel.setData(getSelectedData());
						}
						JTData.setValueAt(true,rowIndex,6);
						new ConfirmMessage(
								true,
								(String )JTData.getValueAt(rowIndex,1),
								(String )JTData.getValueAt(rowIndex,2),
								(String )JTData.getValueAt(rowIndex,4),
								(String )JTData.getValueAt(rowIndex,3));
					}
				});
	}
	
	public String[] getSelectedData() {
		int rowIndex = JTData.getSelectedRow();
		String[] data = new String[5];
		for (int i =0 ; i < data.length ; i++) {
			data[i] = String.valueOf(JTData.getValueAt(rowIndex,(i+1)));
		}
		return data;
	}

	public JTable getJTData() {
		return JTData;
	}

	public void arriveMessage(MessageEvent event) {
		JTData.updateUI();
		
	}
}