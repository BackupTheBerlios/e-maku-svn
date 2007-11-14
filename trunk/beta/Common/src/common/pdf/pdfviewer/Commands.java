/**
 * Esta clase esta basada en el archivo Commands.java del proyecto JPedal.
 * Mayor informacion: 
 * 
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 * (C) Copyright 2006, by IDRsolutions and Contributors.
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * 
 * Commands.java moficiado a partir de 23-mar-2006
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

package common.pdf.pdfviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.*;

import org.jpedal.PdfDecoder;
import org.jpedal.objects.PdfFileInformation;

import common.misc.language.Language;
import common.pdf.pdfviewer.gui.SwingGUI;
import common.pdf.pdfviewer.gui.popups.ErrorDialog;
import common.pdf.pdfviewer.utils.FileFilterer;
import common.pdf.pdfviewer.utils.Printer;
import common.pdf.pdfviewer.utils.SwingWorker;

/**code to execute the actual commands*/
public class Commands {
	
	public static final int SAVE = 5;
	public static final int PRINT = 6;
	public static final int EXIT = 7;
	public static final int AUTOSCROLL = 8;
	public static final int OPENFILE = 10;
	public static final int FIRSTPAGE = 50;
	public static final int FBACKPAGE = 51;
	public static final int BACKPAGE = 52;
	public static final int FORWARDPAGE = 53;
	public static final int FFORWARDPAGE = 54;
	public static final int LASTPAGE = 55;
	public static final int GOTO = 56;
	
	/**combo boxes start at 250*/
	public static final int SCALING = 252;
	
	private Values commonValues;
	private SwingGUI currentGUI;
	private PdfDecoder decode_pdf;
	
	
	/**image if file tiff or png or jpg*/
	private BufferedImage img=null;
	private Printer currentPrinter;
	
	
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
					}
					else{
						currentPrinter.printPDF(decode_pdf);
					}
				}
				else {
					currentGUI.showMessageDialog(Language.getWord("IMPRIESP"));
				}
			}
			else {
				currentGUI.showMessageDialog(Language.getWord("NOARCHIMP"));
			}
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
					//decode_pdf.verifyAccess();
					
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
				commonValues.setPageCount(1);
				commonValues.setCurrentPage(1);
				currentGUI.zoom();
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
		//try{
			decode_pdf.setExtractionMode(0, 72, currentGUI.getScaling());
			/***/
		//}
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
		synchronized (commonValues) {
		//if (!commonValues.isProcessing()) { //lock to stop multiple accesses
			
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
		}
		//}else
			//currentGUI.showMessageDialog(Language.getWord("ESPCARGANDOPAG"));
	}
	
	
	
	/** move back one page */
	private void back(int count) {
		
		//if (!commonValues.isProcessing()) { //lock to stop multiple accesses
		synchronized (commonValues) {
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
		}
		//}else
			//currentGUI.showMessageDialog(Language.getWord("ESPCARGANDOPAG")); 
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
			chooser.setSelectedFile(new File(commonValues.getInputDir()+"/emaku_report.pdf"));
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
		
		int confirm=JOptionPane.showInternalConfirmDialog(currentGUI.getFrame(),Language.getWord("CLOSE_CURRENT_WINDOW"),"",JOptionPane.YES_NO_OPTION);
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
