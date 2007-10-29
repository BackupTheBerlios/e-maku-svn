package com.kazak.comeet.admin.gui.misc;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;

public class XLSExporter {
	
	HSSFWorkbook wb = new HSSFWorkbook();
	Document[] docs;
	String[] titles;
	TableColumnModel[] columnsModelArray;
	String pageTitle[] = new String[4];
	String reportTitle = "";
	
	public XLSExporter(Document[] docs,String[] titles,TableColumnModel[] columnsModelArray, String date, String hour){
		wb = new HSSFWorkbook();
		setPageTitles();
		this.docs = docs;
		this.titles = titles;
		this.columnsModelArray = columnsModelArray;
		reportTitle = "Reporte de Mensajes de Control (" + date + "/" + hour + ")";
		for(int i=0;i<docs.length;i++) {
			createXLSPage(docs[i],titles[i],columnsModelArray[i],pageTitle[i]);
		}
	}
	
	private void setPageTitles() {
		pageTitle[0] = "Lista de Usuarios que respondieron el mensaje a tiempo";
		pageTitle[1] = "Lista de Usuarios que respondieron el mensaje tarde";
		pageTitle[2] = "Lista de Usuarios que no respondieron el mensaje";		
		pageTitle[3] = "Lista de Usuarios que no estaban en linea al momento del control";
	}
	
	private void setColumnsWidth(HSSFSheet sheet, int columnsSize) {
		for(int j=0;j<columnsSize;j++) {
			switch(j) {
			case 0:
				sheet.setColumnWidth((short)j,(short)3000);
				break;
			case 1:
				sheet.setColumnWidth((short)j,(short)12000);
				break;
			case 2:
				sheet.setColumnWidth((short)j,(short)9000);
				break;
			default:
				sheet.setColumnWidth((short)j,(short)5000);
			}
		}		
	}
	
	@SuppressWarnings("deprecation")
	private void createXLSPage(Document doc,String title,TableColumnModel columnsInfo,String pageTitle) {
		HSSFSheet sheet = wb.createSheet(title);
		int columnsSize = columnsInfo.getColumnCount();
		
		setColumnsWidth(sheet,columnsSize);
		
		HSSFRow titleRow = sheet.createRow(0);
		titleRow.createCell((short)1).setCellValue(reportTitle);
		HSSFRow pageTitleRow = sheet.createRow(1);
		pageTitleRow.createCell((short)1).setCellValue(pageTitle);

		HSSFRow blankRow = sheet.createRow(2);
		blankRow.createCell((short)0).setCellValue(" ");
		
		HSSFRow row0 = sheet.createRow(3);
		
		for(int j=0;j<columnsSize;j++) { 
			TableColumn tmp = columnsInfo.getColumn(j);
			String columnName = (String) tmp.getHeaderValue();
			row0.createCell((short)j).setCellValue(columnName);
		}
		
		blankRow = sheet.createRow(4);
			blankRow.createCell((short)0).setCellValue(" ");
		
		List messagesList = doc.getRootElement().getChildren("row");
		Iterator messageIterator = messagesList.iterator();
		// Loading new info 
		int i=5;
		for (;messageIterator.hasNext();) {  
			Element oneMessage = (Element) messageIterator.next();
			List messagesDetails = oneMessage.getChildren();
		    HSSFRow row = sheet.createRow((short)i);
			for (int k=0;k<columnsSize;k++) {
				String data = ((Element)messagesDetails.get(k)).getText();
			    row.createCell((short)k).setCellValue(data);
			}
			i++;
		}
		if (i==0) {
			 HSSFRow row = sheet.createRow((short)i);
			 row.createCell((short)0).setCellValue("No hay registros en esta página.");
		}		
	}
	
	public HSSFWorkbook getXLSFile() {
		return wb;
	}
}
