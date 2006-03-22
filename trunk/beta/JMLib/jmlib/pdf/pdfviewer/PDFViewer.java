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
 * PDFViewer.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */

package jmlib.pdf.pdfviewer;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ResourceBundle;

import jmlib.control.ReportEvent;
import jmlib.control.ReportListener;
import jmlib.control.ValidHeadersClient;
import jmlib.gui.formas.GenericForm;
import jmlib.miscelanea.ZipHandler;
import jmlib.miscelanea.idiom.Language;
import jmlib.pdf.pdfviewer.gui.GUIFactory;
import jmlib.pdf.pdfviewer.gui.SwingGUI;
import jmlib.pdf.pdfviewer.gui.generic.GUIMouseHandler;
import jmlib.pdf.pdfviewer.gui.generic.GUISearchWindow;
import jmlib.pdf.pdfviewer.gui.generic.GUIThumbnailPanel;
import jmlib.pdf.pdfviewer.gui.swing.SwingMouseHandler;
import jmlib.pdf.pdfviewer.gui.swing.SwingSearchWindow;
import jmlib.pdf.pdfviewer.gui.swing.SwingThumbnailPanel;
import jmlib.pdf.pdfviewer.utils.Printer;

import org.jdom.Element;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfFontException;
//import org.jpedal.utils.LogWriter;

/**
 * Scope:<b>(All)</b>
 * <br>Description: Demo to show JPedal being used as a GUI viewer,
 * and to demonstrate some of JPedal's capabilities
 * 
 *
 * <br>This class provides the framework for the Viewer and calls other classes which provide the following
 * functions:-
 * 
 * <br>Values commonValues - repository for general settings
 * Printer currentPrinter - All printing functions and access methods to see if printing active
 * PdfDecoder decode_pdf - PDF library and panel
 * ThumbnailPanel thumbnails - provides a thumbnail pane down the left side of page - thumbnails can be clicked on to goto page
 * PropertiesFile properties - saved values stored between sessions
 * SwingGUI currentGUI - all Swing GUI functions
 * SearchWindow searchFrame (not GPL) - search Window to search pages and goto references on any page
 * Commands currentCommands - parses and executes all options
 * SwingMouseHandler mouseHandler - handles all mouse and related activity
 */
public class PDFViewer implements ReportListener {
	
	/**repository for general settings*/
	protected Values commonValues=new Values(); 
	
	/**All printing functions and access methods to see if printing active*/
	protected Printer currentPrinter=new Printer();
	
	/**PDF library and panel*/
	final protected PdfDecoder decode_pdf = new PdfDecoder(); //USE THIS FOR THE VIEWER ONLY
	/**/
	
	/**encapsulates all thumbnail functionality - just ignore if not required*/
	protected GUIThumbnailPanel thumbnails=new SwingThumbnailPanel(commonValues,decode_pdf);
		
	/**general GUI functions*/
	protected SwingGUI currentGUI=new SwingGUI(decode_pdf,commonValues,thumbnails);

	/**search window and functionality*/
	private GUISearchWindow searchFrame=new SwingSearchWindow(commonValues,currentGUI,decode_pdf);
	
	/**command functions*/
	protected Commands currentCommands=new Commands(commonValues,currentGUI,decode_pdf,
			thumbnails,searchFrame,currentPrinter);
	
	/**all mouse actions*/
	protected GUIMouseHandler mouseHandler=new SwingMouseHandler(decode_pdf,currentGUI,commonValues,currentCommands);
	
	/**scaling values which appear onscreen*/
	protected String[] scalingValues;

	private Element data;

	private ZipHandler zip;
	private String idReport;

	
	/**
	 * setup and run client, loading defaultFile on startup
	 */
	public void setupViewer(String reportTitle,byte [] bytes) {
		openDefaultFile(reportTitle, bytes);
	}
	
	/**
	 * open the file passed in by user on startup (do not call directly)
	 */
	private void openDefaultFile(String reportTitle,byte [] bytes) {
		
		//get any user set dpi
		String hiresFlag=System.getProperty("hires");
		if(hiresFlag!=null)
			commonValues.setUseHiresImage(true);
		
		//get any user set dpi
		String memFlag=System.getProperty("memory");
		if(memFlag!=null)
			commonValues.setUseHiresImage(false);
		
		//reset flag
		thumbnails.resetToDefault();
		
		commonValues.maxViewY=0;// ensure reset for any viewport
		
		/** 
		 * open any default file and selected page
		 */ 
		if(bytes!=null){

			commonValues.setPdfArray(bytes);				
			commonValues.setFileSize(bytes.length);
			
			currentGUI.setViewerTitle(reportTitle);
				
			/**see if user set Page*/
			String page=System.getProperty("Page");

			if(page!=null){
					
   			   int pageNum=-1;
			   try{
			        pageNum=Integer.parseInt(page);
					if(pageNum<1){
						pageNum=-1;
						System.err.println(page+ " must be 1 or larger. Opening on page 1");
					 }
				  }catch(Exception e){
						System.err.println(page+ "is not a valid number for a page number. Opening on page 1");
				   }
			 }
			else
				currentCommands.openFile(bytes);
		
			bytes=null; //deselect
		}
	}
	
	/**
	 * setup and run client
	 */
	public PDFViewer(GenericForm GFforma, String idReport) {
		this.zip = new ZipHandler();
		this.idReport = idReport;
		setupViewer();
		ValidHeadersClient.addReportListener(this);
	}
	
	public void setPDFViewer(String titleReport) {
		try {
			setupViewer(titleReport,zip.getDataDecode(data.getValue()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * initialise and run client (default as Application in own Frame)
	 */
	public void setupViewer() {
		
		/**switch on thumbnails if flag set*/
		String setThumbnail=System.getProperty("thumbnail");
		if((setThumbnail!=null)&&(setThumbnail.equals("true"))) {
			thumbnails.setThumbnailsEnabled();
			System.out.println("Thumbails");
		}
		
		/**non-GUI initialisation*/
		init(null);
		
		/**
		 * gui setup 
		 */
		currentGUI.init(scalingValues,currentCommands,currentPrinter);
		
		setupButtonsAndMenus();
		
		//mouseHandler.setupMouse();
	}
	
	/**
	 * setup the viewer
	 */
	protected void init(ResourceBundle bundle) {
		
		/**setup scaling values which ar displayed for user to choose*/
		String[] scalingValues={Language.getWord("ESCANCHO"),Language.getWord("LARGO"),Language.getWord("ANCHO"),
				"25","50","75","100","125","150","200","250","500","750","1000"};
		this.scalingValues=scalingValues;
		
		/**
		 * FONT EXAMPLE - Replace global default for non-embedded fonts.
		 * 
		 * You can replace Lucida as the standard font used for all non-embedded and substituted fonts 
		 * by using is code.
		 * Java fonts are case sensitive, but JPedal resolves currentGUI.frame, so you could 
		 * use Webdings, webdings or webDings for Java font Webdings
		 */
		try{
			//choice of example font to stand-out (useful in checking results to ensure no font missed. 
			//In general use Helvetica or similar is recommended
			decode_pdf.setDefaultDisplayFont("SansSerif");
		}catch(PdfFontException e){ //if its not available catch error and show valid list
			
			System.out.println(e.getMessage());
			
			//get list of fonts you can use
			String[] fontList =GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			System.out.println(Language.getWord("POSTSCRIPT"));
			System.out.println("=====================\n"); 
			int count = fontList.length;
			for (int i = 0; i < count; i++) {
				Font f=new Font(fontList[i],1,10);
				System.out.println(fontList[i]+" ("+Language.getWord("FUENTESDISP")+"="+f.getPSName()+")");
				
			}	
		}	
	}
	
	/**
	 * sets up all the toolbar items
	 */
	private void setupButtonsAndMenus() {		
		/**
		 * combo boxes on toolbar
		 * */
		currentGUI.addCombo(Language.getWord("ESCALA"), Language.getWord("AUVISTA"), Commands.SCALING);	

		createButtons();
		/**status object on toolbar showing 0 -100 % completion */
		currentGUI.initStatus();
	}
	
	/**
	 * setup up the buttons 
	 * (add your own here if required)
	 */
	private void createButtons() {
		currentGUI.addButton(GUIFactory.BUTTONBAR,Language.getWord("IMPRIREP"),"/icons/ico_impresora.png",Commands.PRINT);
		currentGUI.addButton(GUIFactory.BUTTONBAR,Language.getWord("GUARDARCO"),"/icons/ico_guardar.png",Commands.SAVE);
		currentGUI.addButton(GUIFactory.BUTTONBAR,Language.getWord("CERRAR"),"/icons/ico_cancelar.png",Commands.EXIT);
	}
		
	public SwingGUI getGUI() {
		return currentGUI;
	}

	public void arriveReport(ReportEvent e) {
		if (e.getIdReport().equals(idReport)) {
			data = e.getData();
			setPDFViewer(e.getTitleReport());
			currentGUI.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
}
