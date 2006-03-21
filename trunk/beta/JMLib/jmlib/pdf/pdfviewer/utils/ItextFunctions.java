/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2006, IDRsolutions and Contributors.
 *
 * 	This file is part of JPedal
 *
     This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * ItextFunctions.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package jmlib.pdf.pdfviewer.utils;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ProgressMonitor;
import javax.swing.text.JTextComponent;

import jmlib.pdf.pdfviewer.gui.GUIFactory;
import jmlib.pdf.pdfviewer.gui.SwingGUI;
import jmlib.pdf.pdfviewer.gui.popups.SavePDF;

import org.jpedal.PdfDecoder;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;

/**central location to place external code using itext library*/
public class ItextFunctions {
	
	public final static int ROTATECLOCKWISE = 0;
	public final static int ROTATECOUNTERCLOCKWISE = 1;
	public final static int ROTATE180 = 2;
	
	private final String separator=System.getProperty( "file.separator" );
	
	private String fileName = "";
	private GUIFactory currentGUI;
	private byte [] bytes;
	private PdfDecoder dPDF;
	
	public ItextFunctions(SwingGUI currentGUI,byte [] bytes,PdfDecoder decode_pdf){
		fileName=decode_pdf.getObjectStore().getCurrentFilename();
		this.currentGUI=currentGUI;
		this.bytes = bytes;
		this.dPDF=decode_pdf;
	}
	
	//<start-forms>
	/**uses itext to save out form data with any changes user has made*/
	public void saveFormsData(String file) {
		try {
			org.jpedal.objects.acroforms.AcroRenderer formRenderer=dPDF.getCurrentFormRenderer();
			
			if(formRenderer==null)
				return;
			
			PdfReader reader = new PdfReader(bytes);
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(file));
			AcroFields form = stamp.getAcroFields();
			
			List names = formRenderer.getComponentNameList();
			
			/**
			 * work through all components writing out values
			 */
			for (int i = 0; i < names.size(); i++) {
				
				String name=(String) names.get(i);
				Component[] comps = formRenderer.getComponentsByName(name);
				
				int type=form.getFieldType(name);
				String value="";
				switch(type){
				case AcroFields.FIELD_TYPE_CHECKBOX :
					if(comps.length==1){
						JCheckBox cb=(JCheckBox) comps[0];
						value=cb.getName();
						if(value!=null){
							int ptr=value.indexOf("-(");
							if(ptr!=-1){
								value=value.substring(ptr+2,value.length()-1);
							}
						}
						
						if(value.equals(""))
							value="On";
						
						if(cb.isSelected())
							form.setField(name, value);
						else
							form.setField(name, "Off");
						
					}else{
						for(int j=0;j<comps.length;j++){
							JCheckBox cb=(JCheckBox) comps[j];
							if(cb.isSelected()){
								
								value=cb.getName();
								if(value!=null){
									int ptr=value.indexOf("-(");
									if(ptr!=-1){
										value=value.substring(ptr+2,value.length()-1);
										form.setField(name, value);
									}
								}
								
								break;
							}
						}
					}
					
					break;
				case AcroFields.FIELD_TYPE_COMBO :
					JComboBox combobox=(JComboBox) comps[0];
					value = (String) combobox.getSelectedItem();
					
					/**allow for user adding new value to Combo
					 * to emulate Acrobat
					 * *
					String currentText = (String) combobox.getEditor().getItem();
					
					if(!currentText.equals(""))
						value = currentText;
						*/
					
					if(value == null)
						value="";
					form.setField(name, value);
					
					break;
				case AcroFields.FIELD_TYPE_LIST :
					JList list=(JList) comps[0];
					value = (String) list.getSelectedValue();
					if(value == null)
						value="";
					form.setField(name, value);
					
					break;
				case AcroFields.FIELD_TYPE_NONE :
					
					break;
				case AcroFields.FIELD_TYPE_PUSHBUTTON :
					
					break;
				case AcroFields.FIELD_TYPE_RADIOBUTTON :
					
					for(int j=0;j<comps.length;j++){
						JRadioButton radioButton=(JRadioButton) comps[j];
						if(radioButton.isSelected()){
							
							value=radioButton.getName();
							if(value!=null){
								int ptr=value.indexOf("-(");
								if(ptr!=-1){
									value=value.substring(ptr+2,value.length()-1);
									form.setField(name, value);
								}
							}
							
							break;
						}
					}
					
					break;
				case AcroFields.FIELD_TYPE_SIGNATURE :
					
					break;
					
				case AcroFields.FIELD_TYPE_TEXT :
					JTextComponent tc=(JTextComponent) comps[0];
					value = tc.getText();
					form.setField(name, value);
					
					ArrayList objArrayList = form.getFieldItem(name).widgets;
					PdfDictionary dic = (PdfDictionary)objArrayList.get(0);
					PdfDictionary action =(PdfDictionary)PdfReader.getPdfObject(dic.get(PdfName.MK));

					if (action == null) {
						PdfDictionary d = new PdfDictionary(PdfName.MK);
						dic.put(PdfName.MK, d);

						Color color = tc.getBackground();
						PdfArray f = new PdfArray(new int[] { color.getRed(),
								color.getGreen(), color.getBlue() });
						d.put(PdfName.BG, f);
					}
					
					// moderatly useful debug code
//					Item dd = form.getFieldItem(name);
//					
//					ArrayList objArrayList = dd.widgets;
//					Iterator iter1 = objArrayList.iterator(),iter2;
//					String strName;
//					PdfDictionary objPdfDict = null;
//					PdfName objName = null;
//					PdfObject objObject = null;
//					while(iter1.hasNext())
//					{
//						objPdfDict = (PdfDictionary)iter1.next();
//						System.out.println("PdfDictionary Object: " + objPdfDict.toString());
//						Set objSet = objPdfDict.getKeys();
//						for(iter2 = objSet.iterator(); iter2.hasNext();)
//						{
//							objName = (PdfName)iter2.next();
//							objObject = objPdfDict.get(objName);
//							if(objName.toString().indexOf("MK")!=-1)
//								System.out.println("here");
//							System.out.println("objName: " + objName.toString() + " - objObject:" + objObject.toString() + " - Type: " + objObject.type());
//							if(objObject.isDictionary())
//							{
//								Set objSet2 = ((PdfDictionary)objObject).getKeys();
//								PdfObject objObject2;
//								PdfName objName2;
//								for(Iterator iter3 = objSet2.iterator(); iter3.hasNext();)
//								{
//									objName2 = (PdfName)iter3.next();
//									objObject2 = ((PdfDictionary)objObject).get(objName2);
//									System.out.println("objName2: " + objName2.toString() + " -objObject2: " + objObject2.toString() + " - Type: " + objObject2.type());
//								}
//							}
//						}
//					}

					
					break;
				default:
				break;
				}
			}
			stamp.close();
			
		} catch (ClassCastException e1){
			System.out.println("Expected component does not match actual component");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	//<end-forms>
	
	public void extractPagesToNewPDF(SavePDF current_selection) {
		
		final boolean exportIntoMultiplePages=current_selection.getExportType();
		
		final int[] pgsToExport =current_selection.getExportPages();
		
		if(pgsToExport==null)
			return ;
		
		final int noOfPages= pgsToExport.length;

		//get user choice
		final String output_dir = current_selection.getRootDir()+separator+fileName+separator+"PDFs"+separator;
		
		File testDirExists=new File(output_dir);
		if(!testDirExists.exists())
			testDirExists.mkdirs();
		
		final ProgressMonitor status = new ProgressMonitor(currentGUI.getFrame(),"Generating pdfs of pages","",0,noOfPages);
		
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				if(exportIntoMultiplePages){
							
					boolean yesToAll = false;
					
					for(int i=0;i<noOfPages;i++){
						int page =pgsToExport[i];
						
						if(status.isCanceled()){
							currentGUI.showMessageDialog("Operation Canceled.\n" +
									i+" pages have been exported");
							
							return null;
						}
						try {
							
							PdfReader reader = new PdfReader(bytes);
							
							File fileToSave = new File(output_dir + fileName+"_pg_"+page + ".pdf");
							
							if(fileToSave.exists() && !yesToAll){
								if(pgsToExport.length > 1){
									int n = currentGUI.showOverwriteDialog(fileToSave.getAbsolutePath(),true);
									
									if(n==0){
										// clicked yes so just carry on for this once
									}else if(n==1){
										// clicked yes to all, so set flag
										yesToAll = true;
									}else if(n==2){
										// clicked no, so loop round again
										status.setProgress(page);
										continue;
									}else{
										
										currentGUI.showMessageDialog("Operation Canceled.\n" +
												i+" pages have been exported");
										
										status.close();
										return null;
									}
								}else{
									int n = currentGUI.showOverwriteDialog(fileToSave.getAbsolutePath(),false);
									
									if(n==0){
										// clicked yes so just carry on
									}else{
										// clicked no, so exit
										return null;
									}
								}
							}
							
							Document document = new Document();
							PdfCopy writer = new PdfCopy(document, new FileOutputStream(fileToSave));
							
							document.open();
							
							PdfImportedPage pip = writer.getImportedPage(reader, page);
							writer.addPage(pip);
							
							PRAcroForm form = reader.getAcroForm();
							if (form != null) {
								writer.copyAcroForm(reader);
							}
							
							document.close();
						}
						catch (Exception de) {
							de.printStackTrace();
						}
						
						status.setProgress(i+1);
					}
				}else{
					try {
						
						PdfReader reader = new PdfReader(bytes);
						
						File fileToSave = new File(output_dir + "export_"+fileName + ".pdf");
						
						if(fileToSave.exists()){
							int n = currentGUI.showOverwriteDialog(fileToSave.getAbsolutePath(),false);
							
							if(n==0){
								// clicked yes so just carry on
							}else{
								// clicked no, so exit
								return null;
							}
						}
						
						Document document = new Document();
						PdfCopy copy = new PdfCopy(document,new FileOutputStream(fileToSave.getAbsolutePath()));
						document.open();
						PdfImportedPage pip;
						for(int i=0;i<noOfPages;i++){
							int page=pgsToExport[i];
							
							pip = copy.getImportedPage(reader,page);
							copy.addPage(pip);
						}
						
						PRAcroForm form = reader.getAcroForm();
						
						if (form != null) {
							copy.copyAcroForm(reader);
						}
						
						List bookmarks = SimpleBookmark.getBookmark(reader); 
						copy.setOutlines(bookmarks);
						
						document.close();
						
					}catch (Exception de) {
						de.printStackTrace();
					}
				}
				status.close();
				
				currentGUI.showMessageDialog("Pages saved as PDF to "+output_dir);
				
				return null;
			}
		};
		
		worker.start();
		
	}
	
	public void nup(String file){
		//<start-13>
		try {
			
			int pow2=2;
			
			// we create a reader for a certain document
			PdfReader reader = new PdfReader(bytes);
			// we retrieve the total number of pages and the page size
			int total = reader.getNumberOfPages();
			
			Rectangle pageSize = reader.getPageSize(1);
			
			Rectangle newSize;
			if((pow2 % 2) == 0)
				newSize = new Rectangle(pageSize.width(), pageSize.height());
			else
				newSize = new Rectangle(pageSize.height(), pageSize.width());
			
			Rectangle unitSize = new Rectangle(pageSize.width(), pageSize.height());
			Rectangle currentSize;
			for (int i = 0; i < pow2; i++) {
				unitSize = new Rectangle(unitSize.height() / 2, unitSize.width());
			}
			int n = (int)Math.pow(2, pow2);
			int r = (int)Math.pow(2, (int)pow2 / 2);
			int c = n / r;
			// step 1: creation of a document-object
			Document document = new Document(newSize, 0, 0, 0, 0);
			// step 2: we create a writer that listens to the document
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			// step 3: we open the document
			document.open();
			// step 4: adding the content
			PdfContentByte cb = writer.getDirectContent();
			PdfImportedPage page;
			float offsetX, offsetY, factor;
			int p;
			for (int i = 0; i < total; i++) {
				if (i % n == 0) {
					document.newPage();
				}
				p = i + 1;
				offsetX = unitSize.width() * ((i % n) % c);
				offsetY = newSize.height() - (unitSize.height() * (((i % n) / c) + 1));
				currentSize = reader.getPageSize(p);
				factor = Math.min(unitSize.width() / currentSize.width(), unitSize.height() / currentSize.height());
				offsetX += (unitSize.width() - (currentSize.width() * factor)) / 2f;
				offsetY += (unitSize.height() - (currentSize.height() * factor)) / 2f;
				page = writer.getImportedPage(reader, p);
				cb.addTemplate(page, factor, 0, 0, factor, offsetX, offsetY);
			}
			// step 5: we close the document
			document.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		//<end-13>
	}
	
	public void handouts(String file){
		//<start-13>
		try {

			File dest = new File(file);
			
			int pages=4;

			float x1 = 30f;
			float x2 = 280f;
			float x3 = 320f;
			float x4 = 565f;

			float[] y1 = new float[pages];
			float[] y2 = new float[pages];

			float height = (778f - (20f * (pages - 1))) / pages;
			y1[0] = 812f;
			y2[0] = 812f - height;

			for (int i = 1; i < pages; i++) {
				y1[i] = y2[i - 1] - 20f;
				y2[i] = y1[i] - height;
			}

			// we create a reader for a certain document
			PdfReader reader = new PdfReader(bytes);
			// we retrieve the total number of pages
			int n = reader.getNumberOfPages();

			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4);
			// step 2: we create a writer that listens to the document
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
			// step 3: we open the document
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfImportedPage page;
			int rotation;
			int i = 0;
			int p = 0;
			// step 4: we add content
			while (i < n) {
				i++;
				Rectangle rect = reader.getPageSizeWithRotation(i);
				float factorx = (x2 - x1) / rect.width();
				float factory = (y1[p] - y2[p]) / rect.height();
				float factor = (factorx < factory ? factorx : factory);
				float dx = (factorx == factor ? 0f : ((x2 - x1) - rect.width() * factor) / 2f);
				float dy = (factory == factor ? 0f : ((y1[p] - y2[p]) - rect.height() * factor) / 2f);
				page = writer.getImportedPage(reader, i);
				rotation = reader.getPageRotation(i);
				if (rotation == 90 || rotation == 270) {
					cb.addTemplate(page, 0, -factor, factor, 0, x1 + dx, y2[p] + dy + rect.height() * factor);
				}
				else {
					cb.addTemplate(page, factor, 0, 0, factor, x1 + dx, y2[p] + dy);
				}
				cb.setRGBColorStroke(0xC0, 0xC0, 0xC0);
				cb.rectangle(x3 - 5f, y2[p] - 5f, x4 - x3 + 10f, y1[p] - y2[p] + 10f);
				for (float l = y1[p] - 19; l > y2[p]; l -= 16) {
					cb.moveTo(x3, l);
					cb.lineTo(x4, l);
				}
				cb.rectangle(x1 + dx, y2[p] + dy, rect.width() * factor, rect.height() * factor);
				cb.stroke();

				p++;
				if (p == pages) {
					p = 0;
					document.newPage();
				}
			}
			// step 5: we close the document
			document.close();
		}
		catch(Exception e) {
        	
            System.err.println(e.getMessage());
		}
		//<end-13>
	}
	
	/*public void add(int pageCount,PdfPageData currentPageData, InsertBlankPDFPage addPage){
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		int pageToInsertBefore = addPage.getInsertBefore();
		
		boolean insertAsLastPage = false;
		if(pageToInsertBefore == -1)
			return;
		else if(pageToInsertBefore == -2)
			insertAsLastPage = true;
		
		try{
			PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			
			if(insertAsLastPage)
				stamp.insertPage(pageCount + 1, reader.getPageSizeWithRotation(pageCount));
			else
				stamp.insertPage(pageToInsertBefore, reader.getPageSizeWithRotation(pageToInsertBefore));
			
			
			stamp.close();
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void rotate(int pageCount,PdfPageData currentPageData, RotatePDFPages current_selection){
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			int[] pgsToRotate = current_selection.getRotatedPages();
			
			int check = -1;
			
			if(pgsToRotate.length == 1){
				check=currentGUI.showConfirmDialog("Are you sure wish to rotate the selected page from the document",
						"Confirm",JOptionPane.YES_NO_OPTION);
			}else{
				check=currentGUI.showConfirmDialog("Are you sure wish to rotate the selected pages from the document",
						"Confirm",JOptionPane.YES_NO_OPTION);
			}
			
			if(check != 0)
				return;
			
			if(pgsToRotate == null)
				return;
			
			List pagesToRotate = new ArrayList();
			for(int i=0;i<pgsToRotate.length;i++)
				pagesToRotate.add(new Integer(pgsToRotate[i]));
			
			int direction = current_selection.getDirection();
			
			PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			for(int page=1;page <= pageCount; page++){
				if(pagesToRotate.contains(new Integer(page))){
					int currentRotation = Integer.parseInt(reader.getPageN(page).get(PdfName.ROTATE).toString());
					
					if(direction == ROTATECLOCKWISE)
						reader.getPageN(page).put(PdfName.ROTATE, new PdfNumber((currentRotation + 90) % 360));
					else if(direction == ROTATECOUNTERCLOCKWISE)
						reader.getPageN(page).put(PdfName.ROTATE, new PdfNumber((currentRotation - 90) % 360));
					else if(direction == ROTATE180)
						reader.getPageN(page).put(PdfName.ROTATE, new PdfNumber((currentRotation + 180) % 360));
					else
						throw new Exception("invalid desired rotation");
				}
				
			}
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			stamp.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
		
	}*/
	
	/*public void setCrop(int pageCount, PdfPageData currentPageData, CropPDFPages cropPage){
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			
			int[] pgsToEdit = cropPage.getPages();
			
			if(pgsToEdit == null)
				return;
			
			List pagesToEdit = new ArrayList();
			for(int i=0;i<pgsToEdit.length;i++)
				pagesToEdit.add(new Integer(pgsToEdit[i]));
			
			PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			boolean applyToCurrent = cropPage.applyToCurrentCrop();
			
			for(int page=1;page <= pageCount; page++){
				if(pagesToEdit.contains(new Integer(page))){
					ArrayList currentCrop = ((PdfArray) reader.getPageN(page).get(PdfName.CROPBOX)).getArrayList();
					
					float currentLeftCrop = ((PdfNumber)currentCrop.get(0)).floatValue();
					float currentBottomCrop = ((PdfNumber)currentCrop.get(1)).floatValue();
					float currentRightCrop = ((PdfNumber)currentCrop.get(2)).floatValue();
					float currentTopCrop = ((PdfNumber)currentCrop.get(3)).floatValue();
					
					float[] newCrop = cropPage.getCrop();
					
					if(applyToCurrent){
						newCrop[0] = currentLeftCrop + newCrop[0];
						newCrop[1] = currentBottomCrop + newCrop[1];
						newCrop[2] = currentRightCrop - newCrop[2];
						newCrop[3] = currentTopCrop - newCrop[3];
					}else{
						newCrop[2] = reader.getPageSize(page).width() - newCrop[2];
						newCrop[3] = reader.getPageSize(page).height() - newCrop[3];
					}
					
					reader.getPageN(page).put(PdfName.CROPBOX, new PdfArray(newCrop));
				}
			}
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			stamp.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void delete(int pageCount, PdfPageData currentPageData,DeletePDFPages deletedPages){
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			int[] pgsToDelete = deletedPages.getDeletedPages();
			
			int check = -1;
			
			if(pgsToDelete.length == 1){
				check=currentGUI.showConfirmDialog("Are you sure wish to delete the selected page from the document",
						"Confirm",JOptionPane.YES_NO_OPTION);
			}else{
				check=currentGUI.showConfirmDialog("Are you sure wish to delete the selected pages from the document",
						"Confirm",JOptionPane.YES_NO_OPTION);
			}
			
			if(check != 0)
				return;
			
			if(pgsToDelete == null)
				return;
			
			List pagesToDelete = new ArrayList();
			for(int i=0;i<pgsToDelete.length;i++)
				pagesToDelete.add(new Integer(pgsToDelete[i]));
			
			PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			
			Document document = new Document();
			PdfCopy writer = new PdfCopy(document, new FileOutputStream(bytes));
			
			document.open();
			
			for(int page=1;page <= pageCount; page++){
				if(!pagesToDelete.contains(new Integer(page))){
					PdfImportedPage pip = writer.getImportedPage(reader, page);
					
					writer.addPage(pip);
				}
			}
			
			document.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void stampImage(int pageCount, PdfPageData currentPageData, final StampImageToPDFPages stampImage) {
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			
			int[] pgsToEdit = stampImage.getPages();
			
			if(pgsToEdit == null)
				return;
			
			File fileToTest = new File(stampImage.getImageLocation());
			if(!fileToTest.exists()){
				currentGUI.showMessageDialog("The image you have selected does not exist");
				return;
			}
			
			List pagesToEdit = new ArrayList();
			for(int i=0;i<pgsToEdit.length;i++)
				pagesToEdit.add(new Integer(pgsToEdit[i]));
			
			final PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			int n = reader.getNumberOfPages();
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			
			Image img = Image.getInstance(fileToTest.getAbsolutePath());
			
			int chosenWidthScale = stampImage.getWidthScale();
			int chosenHeightScale = stampImage.getHeightScale();
			
			img.scalePercent(chosenWidthScale,chosenHeightScale);
			
			String chosenPlacement = stampImage.getPlacement();
			
			int chosenRotation = stampImage.getRotation();
			img.setRotationDegrees(chosenRotation);
			
			String chosenHorizontalPosition = stampImage.getHorizontalPosition();
			String chosenVerticalPosition = stampImage.getVerticalPosition();
			
			float chosenHorizontalOffset = stampImage.getHorizontalOffset();
			float chosenVerticalOffset = stampImage.getVerticalOffset();
			
			for(int page = 0;page<=n;page++){
				if(pagesToEdit.contains(new Integer(page))){
					
					PdfContentByte cb;
					if(chosenPlacement.equals("Overlay"))
						cb = stamp.getOverContent(page);
					else
						cb = stamp.getUnderContent(page);
					
					int currentRotation = currentPageData.getRotation(page);
					Rectangle pageSize;
					if(currentRotation == 90 || currentRotation == 270)
						pageSize = reader.getPageSize(page).rotate();
					else
						pageSize = reader.getPageSize(page);
					
					float startx,starty;
					if(chosenVerticalPosition.equals("From the top")){
						starty = pageSize.height() - ((img.height() * (chosenHeightScale / 100)) / 2);
					}else if(chosenVerticalPosition.equals("Centered")){
						starty = (pageSize.height() / 2) - ((img.height() * (chosenHeightScale / 100)) / 2);
					}else{
						starty = 0;
					}
					
					if(chosenHorizontalPosition.equals("From the left")){
						startx = 0;
					}else if(chosenHorizontalPosition.equals("Centered")){
						startx = (pageSize.width() / 2) - ((img.width() * (chosenWidthScale / 100)) / 2);
					}else{
						startx = pageSize.width() - ((img.width() * (chosenWidthScale / 100)) / 2);
					}
					
					img.setAbsolutePosition(startx + chosenHorizontalOffset, starty + chosenVerticalOffset);
					
					cb.addImage(img);
				}
			}
			
			stamp.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void stampText(int pageCount, PdfPageData currentPageData, final StampTextToPDFPages stampText) {
		//<start-13>
		
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			
			int[] pgsToEdit = stampText.getPages();
			
			if(pgsToEdit == null)
				return;
			
			List pagesToEdit = new ArrayList();
			for(int i=0;i<pgsToEdit.length;i++)
				pagesToEdit.add(new Integer(pgsToEdit[i]));
			
			
			final PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			
			for(int page=1;page <= pageCount;page++){
				if(pagesToEdit.contains(new Integer(page))){
					
					String chosenText = stampText.getText();
					
					if(!chosenText.equals("")){
						
						String chosenFont = stampText.getFontName();
						Color chosenFontColor = stampText.getFontColor();
						int chosenFontSize = stampText.getFontSize();
						
						int chosenRotation = stampText.getRotation();
						String chosenPlacement = stampText.getPlacement();
						
						String chosenHorizontalPosition = stampText.getHorizontalPosition();
						String chosenVerticalPosition = stampText.getVerticalPosition();
						
						float chosenHorizontalOffset = stampText.getHorizontalOffset();
						float chosenVerticalOffset = stampText.getVerticalOffset();
						
						BaseFont font = BaseFont.createFont(chosenFont , BaseFont.WINANSI, false);
						
						PdfContentByte cb;
						if(chosenPlacement.equals("Overlay"))
							cb = stamp.getOverContent(page);
						else
							cb = stamp.getUnderContent(page);
						
						cb.beginText();
						cb.setColorFill(chosenFontColor);
						cb.setFontAndSize(font, chosenFontSize);
						
						int currentRotation = currentPageData.getRotation(page);
						Rectangle pageSize;
						if(currentRotation == 90 || currentRotation == 270)
							pageSize = reader.getPageSize(page).rotate();
						else
							pageSize = reader.getPageSize(page);
						
						float startx;
						float starty;
						
						if(chosenVerticalPosition.equals("From the top")){
							starty = pageSize.height();
						}else if(chosenVerticalPosition.equals("Centered")){
							starty = pageSize.height() / 2;
						}else{
							starty = 0;
						}
						
						if(chosenHorizontalPosition.equals("From the left")){
							startx = 0;
						}else if(chosenHorizontalPosition.equals("Centered")){
							startx = pageSize.width() / 2;
						}else{
							startx = pageSize.width();
						}
						
						cb.showTextAligned(Element.ALIGN_CENTER, chosenText, startx + chosenHorizontalOffset, starty + chosenVerticalOffset, chosenRotation);
						cb.endText();
					}
				}
			}
			
			stamp.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void addHeaderFooter(int pageCount, PdfPageData currentPageData,final AddHeaderFooterToPDFPages addHeaderFooter){
		//<start-13>
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try{
			
			int[] pgsToEdit = addHeaderFooter.getPages();
			
			if(pgsToEdit == null)
				return;
			
			List pagesToEdit = new ArrayList();
			for(int i=0;i<pgsToEdit.length;i++)
				pagesToEdit.add(new Integer(pgsToEdit[i]));
			
			final PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(bytes));
			
			for(int page=1;page <= pageCount;page++){
				if(pagesToEdit.contains(new Integer(page))){
					
					String chosenFont = addHeaderFooter.getFontName();
					Color chosenFontColor = addHeaderFooter.getFontColor();
					int chosenFontSize = addHeaderFooter.getFontSize();
					
					float chosenLeftRightMargin = addHeaderFooter.getLeftRightMargin();
					float chosenTopBottomMargin = addHeaderFooter.getTopBottomMargin();
					
					String chosenLeftHeader = addHeaderFooter.getLeftHeader();
					String chosenCenterHeader = addHeaderFooter.getCenterHeader();
					String chosenRightHeader = addHeaderFooter.getRightHeader();
					
					String chosenLeftFooter = addHeaderFooter.getLeftFooter();
					String chosenCenterFooter = addHeaderFooter.getCenterFooter();
					String chosenRightFooter = addHeaderFooter.getRightFooter();
					
					BaseFont font = BaseFont.createFont(chosenFont , BaseFont.WINANSI, false);
					
					PdfContentByte cb = stamp.getOverContent(page);
					
					cb.beginText();
					cb.setColorFill(chosenFontColor);
					cb.setFontAndSize(font, chosenFontSize);
					
					Rectangle pageSize = reader.getPageSizeWithRotation(page);
					
					if(!chosenLeftHeader.equals("")){
						cb.showTextAligned(Element.ALIGN_LEFT, chosenLeftHeader, chosenLeftRightMargin,pageSize.height() - chosenTopBottomMargin, 0);
					}
					if(!chosenCenterHeader.equals("")){
						cb.showTextAligned(Element.ALIGN_CENTER, chosenCenterHeader,pageSize.width() / 2,pageSize.height() - chosenTopBottomMargin, 0);
					}
					if(!chosenRightHeader.equals("")){
						cb.showTextAligned(Element.ALIGN_RIGHT, chosenRightHeader, pageSize.width() - chosenLeftRightMargin, pageSize.height() - chosenTopBottomMargin, 0);
					}
					
					if(!chosenLeftFooter.equals("")){
						cb.showTextAligned(Element.ALIGN_LEFT, chosenLeftFooter, chosenLeftRightMargin,chosenTopBottomMargin, 0);
					}
					if(!chosenCenterFooter.equals("")){
						cb.showTextAligned(Element.ALIGN_CENTER, chosenCenterFooter, pageSize.width() / 2,chosenTopBottomMargin, 0);
					}
					if(!chosenRightFooter.equals("")){
						cb.showTextAligned(Element.ALIGN_RIGHT, chosenRightFooter, pageSize.width() - chosenLeftRightMargin, chosenTopBottomMargin, 0);
					}				
					
					cb.endText();
				}
			}
			
			stamp.close();
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
	
	/*public void encrypt(int pageCount, PdfPageData currentPageData, EncryptPDFPage encryptPage) {
		//<start-13>
		String p = encryptPage.getPermissions();
		int encryptionLevel = encryptPage.getEncryptionLevel();
		String userPassword = encryptPage.getUserPassword();
		String masterPassword = encryptPage.getMasterPassword();
		
		int permit[] = {
				PdfWriter.AllowPrinting,
				PdfWriter.AllowModifyContents,
				PdfWriter.AllowCopy,
				PdfWriter.AllowModifyAnnotations,
				PdfWriter.AllowFillIn
		};
		
		
		int permissions = 0;
		for (int i = 0; i < p.length(); ++i) {
			permissions |= (p.charAt(i) == '0' ? 0 : permit[i]);
		}
		
		File tempFile = null;
		
		try {
			tempFile=File.createTempFile("temp",null);
			
			ObjectStore.copy(bytes, tempFile.getAbsolutePath());
		}catch(Exception e){
			return;
		}
		
		try {
			PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
			
			PdfEncryptor.encrypt(reader, new FileOutputStream(bytes),
					userPassword.getBytes(), masterPassword.getBytes(), permissions, encryptionLevel==0);
			
		} catch (Exception e) {
			
			ObjectStore.copy(tempFile.getAbsolutePath(),bytes);
			
			e.printStackTrace();
			
		} finally{
			tempFile.delete();
		}
		//<end-13>
	}*/
}