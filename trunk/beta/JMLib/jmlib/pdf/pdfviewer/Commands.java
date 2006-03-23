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
 * Commands.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package jmlib.pdf.pdfviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jmlib.miscelanea.idiom.Language;
import jmlib.pdf.pdfviewer.gui.SwingGUI;
import jmlib.pdf.pdfviewer.gui.popups.ErrorDialog;
import jmlib.pdf.pdfviewer.utils.FileFilterer;
import jmlib.pdf.pdfviewer.utils.Printer;
import jmlib.pdf.pdfviewer.utils.SwingWorker;

import org.jpedal.PdfDecoder;
import org.jpedal.examples.simpleviewer.utils.Messages;
import org.jpedal.exception.PdfException;
import org.jpedal.objects.PdfFileInformation;

/**code to execute the actual commands*/
public class Commands {
	
	public static final int INFO = 1;
	public static final int BITMAP = 2;
	public static final int IMAGES = 3;
	public static final int TEXT = 4;
	public static final int SAVE = 5;
	public static final int PRINT = 6;
	public static final int EXIT = 7;
	public static final int AUTOSCROLL = 8;
//	public static final int DOCINFO = 9;
	public static final int OPENFILE = 10;
// public static final int BOOKMARK = 11;
//	public static final int FIND = 12;
//	public static final int SNAPSHOT = 13;
//	public static final int OPENURL = 14;
//	public static final int VISITWEBSITE = 15;
//	public static final int PREVIOUSDOCUMENT = 16;
//	public static final int NEXTDOCUMENT = 17;
	
	public static final int FIRSTPAGE = 50;
	public static final int FBACKPAGE = 51;
	public static final int BACKPAGE = 52;
	public static final int FORWARDPAGE = 53;
	public static final int FFORWARDPAGE = 54;
	public static final int LASTPAGE = 55;
	public static final int GOTO = 56;
	
	/**combo boxes start at 250*/
//	public static final int QUALITY = 250;
//	public static final int ROTATION = 251;
	public static final int SCALING = 252;
	
	/**
	 * external/itext menu options start at 500 - add your own CONSTANT here
	 * and refer to action using name at ALL times
	 */
	public static final int SAVEFORM = 500;
	public static final int PDF = 501;
	public static final int ROTATE=502;
	public static final int DELETE=503;
	public static final int ADD=504;
	public static final int SECURITY=505;
	public static final int ADDHEADERFOOTER=506;
	public static final int STAMPTEXT=507;
	public static final int STAMPIMAGE=508;
	public static final int SETCROP=509;
	public static final int NUP = 510;
	public static final int HANDOUTS = 511;
	//public static final int NEWFUNCTION = 512;
	
	
	private Values commonValues;
	private SwingGUI currentGUI;
	private PdfDecoder decode_pdf;
	
	
	/**image if file tiff or png or jpg*/
	private BufferedImage img=null;
	
	private Printer currentPrinter;
	
//	public Commands(Values commonValues,SwingGUI currentGUI,PdfDecoder decode_pdf,GUIThumbnailPanel thumbnails, 
			//PropertiesFile properties , GUISearchWindow searchFrame,Printer currentPrinter) {
	
public Commands(Values commonValues,SwingGUI currentGUI,PdfDecoder decode_pdf,Printer currentPrinter) {
		this.commonValues=commonValues;
		this.currentGUI=currentGUI;
		this.decode_pdf=decode_pdf;
		this.currentPrinter=currentPrinter;
	}
	
	/**
	 * main routine which executes code for current command
	 */
	public void executeCommand(int ID) {

		switch(ID){
			
		case SAVE:
			saveFile();
			break;
			
		case PRINT:
			if(commonValues.getPdfArray()!=null){
				if(!currentPrinter.isPrinting()){
					if(!commonValues.isPDF()){
						currentGUI.showMessageDialog(Language.getWord("IMPRIERR"));
					}else{
						currentPrinter.printPDF(decode_pdf,currentGUI);
					}
				}else {
					currentGUI.showMessageDialog(Language.getWord("IMPRIESP"));
				}
			}else
				currentGUI.showMessageDialog(Language.getWord("NOARCHIMP")); 
			break;
			
		case EXIT:
			if(currentPrinter.isPrinting())
				currentGUI.showMessageDialog(Language.getWord("AUNIMP"));
			else
				exit();
			break;
			
		case AUTOSCROLL:
			currentGUI.toogleAutoScrolling();
			break;
		
		case FIRSTPAGE:
			if((commonValues.getPdfArray()!=null)&&(commonValues.getPageCount()>1)&&(commonValues.getCurrentPage()!=1))
				back(commonValues.getCurrentPage()-1);
			break;
			
		case FBACKPAGE:
			if(commonValues.getPdfArray()!=null)
				back(10);
			break;
			
		case BACKPAGE:
			if(commonValues.getPdfArray()!=null)
				back(1);
			break;
			
		case FORWARDPAGE:
			if(commonValues.getPdfArray()!=null)
				forward(1);
			break;
			
		case FFORWARDPAGE:
			if(commonValues.getPdfArray()!=null)
				forward(10);
			break;
			
		case LASTPAGE:
			if((commonValues.getPdfArray()!=null)&&(commonValues.getPageCount()>1)&&(commonValues.getPageCount()-commonValues.getCurrentPage()>0))
				forward(commonValues.getPageCount()-commonValues.getCurrentPage());
			break;
			
		case SCALING:
			if(!commonValues.isProcessing()){
				if(commonValues.getPdfArray()!=null)
					currentGUI.zoom();
			}
			break;
			
/*		case ROTATION:
			if(commonValues.getPdfArray()!=null)
				currentGUI.rotate(); 
			break; */

		default:
			System.out.println(Language.getWord("ITEMNOSEL"));
		}
	}

	/**
	 * called by nav functions to decode next page
	 */
	private void decodeImage(final boolean resizePanel) {
		
		//remove any search highlight
		decode_pdf.setFoundTextAreas(null);
		
		currentGUI.setRectangle(null);
		
		//stop user changing scaling while decode in progress
		currentGUI.resetComboBoxes(false);
		
		decode_pdf.clearScreen();
		
		
		commonValues.setProcessing(true);
		
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				
				try {
					
					currentGUI.updateStatusMessage(Language.getWord("DECOPAG"));
					
					if(img!=null)
						decode_pdf.addImage(img);
					/**
					 * make sure screen fits display nicely
					 */
					if (resizePanel)
						currentGUI.zoom();
					
					if (Thread.interrupted())
						throw new InterruptedException();
					currentGUI.setPageNumber();
					
					//<start-13>
					currentGUI.setViewerTitle(null); //restore title
					//<end-13>
					
				} catch (Exception e) {
					//<start-13>
					currentGUI.setViewerTitle(null); //restore title
					//<end-13>
				}
				
				currentGUI.setStatusProgress(100);
				
				//reanable user changing scaling 
				currentGUI.resetComboBoxes(true);
		
				//ensure drawn
				decode_pdf.repaint();
				
				return null;
			}
		};
		
		worker.start();
		
	}
	
	/**
	 *  initial method called to open a new PDF
	 */
	protected boolean openUpFile(byte [] bytes) {
		
		commonValues.maxViewY=0;// rensure reset for any viewport
		
		boolean fileCanBeOpened = true;
		
		/** reset default values */
		currentGUI.setScalingToDefault(); 
		
		decode_pdf.closePdfFile();
		
		try {
	  		  decode_pdf.openPdfArray(commonValues.getPdfArray());
			
			  currentGUI.updateStatusMessage(Language.getWord("ABRIARCHIVO"));
			
			/** flag up if encryption present */
			
			/** popup window if needed */
			if ((fileCanBeOpened)&&(decode_pdf.isEncrypted()) && (!decode_pdf.isFileViewable())) {
				fileCanBeOpened = false;
				
				//<start-13>
				/**
				 * //<end-13>JOptionPane.showMessageDialog(currentGUI.frame,"Please
				 * use Java 1.4 to display encrypted files"); //<start-13>
				 */
				
				String password = currentGUI.showInputDialog(Language.getWord("CLAVEPDF")); //$NON-NLS-1$
				
				/** try and reopen with new password */
				if (password != null) {
					decode_pdf.setEncryptionPassword(password);
					decode_pdf.verifyAccess();
					
					if (decode_pdf.isFileViewable())
						fileCanBeOpened = true;
					
				}
				
				currentGUI.showMessageDialog(Language.getWord("CLAVEPDFREQ"));
				//<end-13>
			}
			
			
			if (fileCanBeOpened) {				
				/** reset values */
				commonValues.setCurrentPage(1);
			}
			
		} catch (Exception e) {
			System.err.println("Exception " + e + " opening file");
			
			ErrorDialog.showError(e,Language.getWord("ABRIERROR"),currentGUI.getFrame());
			//System.exit(1);
			
		}
		
		return fileCanBeOpened;
		
	}
	
	/**
	 *  checks file can be opened (permission) 
	 */
	protected void openFile() {
		
		//get any user set dpi
		String hiresFlag=System.getProperty("hires");
		if(hiresFlag!=null)
			commonValues.setUseHiresImage(true);
		
		//get any user set dpi
		String memFlag=System.getProperty("memory");
		if(memFlag!=null){
			commonValues.setUseHiresImage(false);
		}
		
		
		//<start-forms>
		//flush forms list
		currentGUI.setNoPagesDecoded();
		//<end-forms>
		
		commonValues.maxViewY=0;// rensure reset for any viewport
		commonValues.setPDF(true);
		
		//currentGUI.setQualityBoxVisible(commonValues.isPDF());
				
		boolean fileCanBeOpened=openUpFile(commonValues.getPdfArray());
		commonValues.setCurrentPage(1);
		
		try{
			if(fileCanBeOpened)
				processPage();	
			else{
				currentGUI.setViewerTitle("");
				decode_pdf.clearScreen();
				this.currentGUI.zoom();
				commonValues.setPageCount(1);
				commonValues.setCurrentPage(1);
			}
		}catch(Exception e){
			System.err.println("Exception " + e + " decoding file");
	        e.printStackTrace();		
		}
		
		commonValues.setProcessing(false);
	}
	
	
	/**
	 * decode and display selected page
	 */
	protected void processPage() {
		PdfFileInformation currentFileInformation = null;
		if(commonValues.isPDF()){
			/**
			 * get PRODUCER and if OCR disable text printing
			 */
			currentFileInformation=decode_pdf.getFileInformationData();
			
			/**switch all on by default*/
			decode_pdf.setRenderMode(PdfDecoder.RENDERIMAGES+PdfDecoder.RENDERTEXT);
			
			String[] values=currentFileInformation.getFieldValues();
			String[] fields=currentFileInformation.getFieldNames();
			
			/** holding all creators that produce OCR pdf's */
			String[] ocr = {"TeleForm","dgn2pdf"};
			
			for(int i=0;i<fields.length;i++){
				
				if((fields[i].equals("Creator"))|(fields[i].equals("Producer"))){
					
					for(int j=0;j<ocr.length;j++){
						
						if(values[i].equals(ocr[j])){
							
							decode_pdf.setRenderMode(PdfDecoder.RENDERIMAGES);
							
							/**
							 * if we want to use java 13 JPEG conversion
							 */
							decode_pdf.setEnableLegacyJPEGConversion(true);
							
						}
					}
				}
				
				boolean currentProcessingStatus=commonValues.isProcessing();
				commonValues.setProcessing(true);	//stops listeners processing spurious event			
				if(commonValues.isUseHiresImage()){
					decode_pdf.useHiResScreenDisplay(true);
					//currentGUI.setSelectedComboIndex(Commands.QUALITY,1);
				}else{
					decode_pdf.useHiResScreenDisplay(false);
					//currentGUI.setSelectedComboIndex(Commands.QUALITY,0);
				}
				commonValues.setProcessing(currentProcessingStatus);
				
			}
		}
		commonValues.setPageCount(decode_pdf.getPageCount());
		
		/**special customisations for images*/
		if(!commonValues.isPDF()){
			commonValues.setPageCount(1);
			decode_pdf.useHiResScreenDisplay(true);
			
		}
				
		if(commonValues.getPageCount()<commonValues.getCurrentPage()){
			commonValues.setCurrentPage(commonValues.getPageCount());
			System.err.println(commonValues.getCurrentPage()+ Language.getWord("PAGFUERANGO"));
		}
		
		//values extraction mode,dpi of images, dpi of page as a factor of 72
		try{
			decode_pdf.setExtractionMode(0, 72, currentGUI.getScaling());
			/***/
		}catch(PdfException e){
		}
		//resize (ensure at least certain size)
		currentGUI.zoom();
		
		//add a border
		decode_pdf.setPDFBorder(BorderFactory.createLineBorder(Color.black, 1));
		
		/** turn off border in printing */
		decode_pdf.disableBorderForPrinting();
	
		/**
		 * update the display, including any rotation
		 */
		currentGUI.setPageNumber();
		
		//currentGUI.resetRotationBox();
		
		if(commonValues.isPDF())
			currentGUI.decodePage(true);
		else
			decodeImage(true);

	}
	
	/**move forward one page*/
	private void forward(int count) {
		
		if (!commonValues.isProcessing()) { //lock to stop multiple accesses
			
			/**if in range update count and decode next page. Decoded pages are cached so will redisplay
			 * almost instantly*/
			int updatedTotal=commonValues.getCurrentPage()+count;
			if (updatedTotal <= commonValues.getPageCount()) {
				commonValues.setCurrentPage(updatedTotal);
				
				currentGUI.resetStatusMessage(Language.getWord("CARGANDOPAG")+commonValues.getCurrentPage());
				/**reset as rotation may change!*/
				decode_pdf.setPageParameters(currentGUI.getScaling(), commonValues.getCurrentPage());
			
				//decode the page
				currentGUI.decodePage(false);
				
				//if scaling to window reset screen to fit rotated page
				if(currentGUI.getSelectedComboIndex(Commands.SCALING)<3)
					currentGUI.zoom();
				
			}
		}else
			currentGUI.showMessageDialog(Language.getWord("ESPCARGANDOPAG"));
	}
	
	
	
	/** move back one page */
	private void back(int count) {
		
		if (!commonValues.isProcessing()) { //lock to stop multiple accesses
			
			/**
			 * if in range update count and decode next page. Decoded pages are
			 * cached so will redisplay almost instantly
			 */
			int updatedTotal=commonValues.getCurrentPage()-count;
			if (updatedTotal >= 1) {
				commonValues.setCurrentPage(updatedTotal);
				
				currentGUI.resetStatusMessage(Language.getWord("CARGANDOPAG")+commonValues.getCurrentPage());
				
				/** reset as rotation may change! */
				decode_pdf.setPageParameters(currentGUI.getScaling(), commonValues.getCurrentPage());
	
				currentGUI.decodePage(false);
				
				//if scaling to window reset screen to fit rotated page
				if(currentGUI.getSelectedComboIndex(Commands.SCALING)<3)
					currentGUI.zoom();				
			}
		}else
			currentGUI.showMessageDialog(Language.getWord("ESPCARGANDOPAG")); 
	}
	
	public void gotoPage(String page) {
		int newPage;
		
		//allow for bum values
		try{
			newPage=Integer.parseInt(page);
			
			if((newPage>decode_pdf.getPageCount())|(newPage<1)){
				currentGUI.showMessageDialog(Language.getWord("PAGE")+ " "+
						page+" "+Language.getWord("RANGOFU")+" "+decode_pdf.getPageCount());
				newPage=commonValues.getCurrentPage();
				currentGUI.setPageNumber();
			}
			
		}catch(Exception e){
			currentGUI.showMessageDialog(">"+page+ "< "+Language.getWord("RANGOFU")); 
			newPage=commonValues.getCurrentPage();
			currentGUI.pageCounter2.setText(""+commonValues.getCurrentPage()); 
		}
		
		//open new page
		if((!commonValues.isProcessing())&&(commonValues.getCurrentPage()!=newPage)){
			commonValues.setCurrentPage(newPage);
			currentGUI.decodePage(false);
			currentGUI.zoom();
		}
	}

	private void saveFile() {	
		
		/**
		 * create the file chooser to select the file
		 */
		File file=null;
		String fileToSave="";
		boolean finished=false;
		
		while(!finished){
			JFileChooser chooser = new JFileChooser(commonValues.getInputDir());
			chooser.setSelectedFile(new File(commonValues.getInputDir()+"/jmidas_report.pdf"));
			chooser.addChoosableFileFilter(new FileFilterer(new String[]{"pdf"}, "Pdf (*.pdf)"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			//set default name to current file name 
			int approved=chooser.showSaveDialog(null);
			if(approved==JFileChooser.APPROVE_OPTION){
				
				ByteArrayInputStream fis=null;
				FileOutputStream fos=null;
				
				file = chooser.getSelectedFile();
				fileToSave=file.getAbsolutePath();
				
				if(!fileToSave.endsWith(".pdf")){
					fileToSave += ".pdf";
					file=new File(fileToSave);
				}
				
				if(file.exists()){
					int n=currentGUI.showConfirmDialog(fileToSave+"\n" +
							Language.getWord("ARCHIVOEX") + "\n" +
							Language.getWord("ARCHIVORESC") ,"Resave Forms Data",JOptionPane.YES_NO_OPTION);
					if(n==1)
						continue;
				}
				
				try {
					fis=new ByteArrayInputStream(commonValues.getPdfArray());
					fos=new FileOutputStream(fileToSave);
					
					byte[] buffer=new byte[4096];
					int bytes_read;
					
					while((bytes_read=fis.read(buffer))!=-1)
						fos.write(buffer,0,bytes_read);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				
				try{
					fis.close();
					fos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				finished=true;
			}else{
				return;
			}
		}
	}
	
	/**Clean up and exit program*/
	private void exit() {
		
		int confirm=JOptionPane.showInternalConfirmDialog(currentGUI.getFrame(),Messages.getMessage("PdfViewerCloseing.message"),"",JOptionPane.YES_NO_OPTION);
		
		if(confirm==JOptionPane.NO_OPTION){
			return;
		}
		
		/**cleanup*/
		decode_pdf.closePdfFile();
		
		flush();
		currentGUI.close();
	}
	
	/**
	 * routine to remove all objects from temp store
	 */
	public final void flush() {
		
		String target=commonValues.getTarget();

        if(target!=null){
            //get contents

            File temp_files = new File(target);
            String[] file_list = temp_files.list();
            if (file_list != null) {
                for (int ii = 0; ii < file_list.length; ii++) {
                    File delete_file = new File(target + commonValues.getSeparator()+file_list[ii]);
                    delete_file.delete();
                }
            }

        }
	}
}
