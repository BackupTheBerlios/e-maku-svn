package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.kazak.smi.admin.models.TableSorter;


public class HistoryDataPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable JTData;
	private HistoryDataModel historyDataModel;
	private HistoryMessagePanel messagePanel;
	
	public HistoryDataPanel(HistoryMessagePanel messagePanel, Vector<Vector<Object>> data) {
		setLayout(new BorderLayout());
		this.messagePanel = messagePanel;
		historyDataModel = new HistoryDataModel(data);
		TableSorter ts =new TableSorter(historyDataModel);
		JTData = createTable();
		JTData.setModel(ts);
		ts.setTableHeader(JTData.getTableHeader());
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
		 JTable table = new JTable();
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
					}
				});
	}
	
	public String[] getSelectedData() {
		int rowIndex = JTData.getSelectedRow();
		String[] data = new String[7];
		for (int i =0 ; i < data.length ; i++) {
			data[i] = String.valueOf(JTData.getValueAt(rowIndex,(i+1)));
		}
		return data;
	}

	public JTable getJTData() {
		return JTData;
	}
}