/*
* ===========================================
* Java Pdf Extraction Decoding Access Library
* ===========================================
*
* Project Info:  http://www.jpedal.org
* Project Lead:  Mark Stephens (mark@idrsolutions.com)
*
* (C) Copyright 2004, IDRsolutions and Contributors.
* ---------------
* PrintPages.java
* ---------------
* (C) Copyright 2004, by IDRsolutions and Contributors.
*
* Original Author:  Mark Stephens (mark@idrsolutions.com)
* Contributor(s):
* 
* 2004-06-09		Add as proof of concept example
*
*/
package jmlib.pdf.printing;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;

import org.jpedal.PdfDecoder;
import org.jpedal.utils.LogWriter;

/**
<P>This example prints a pdf file or directory of files using JPS
in the XMP Scope:<b>(All)</b>
 */
public class SilentPrint {

	/**flag to output information for debugging the code*/
	private static boolean debugCode=true;
	
	/**correct separator for OS */
	private final String separator = System.getProperty("file.separator");

	/**the decoder object which decodes the pdf and returns a data object*/
	private PdfDecoder decode_pdf = null;
	
	/**max copies (default) - is checked when data read and resized to suit*/
//	private int maxCopies=5;
	
	/**number of copies to print*/
	private int copiesToPrint=1;

	/**number of pages in the document*/
//	private int pageCount;

	/**choosen printer - if not set, program will exit with list*/
	private String printer="FinePrint";
	
	/**used to set tray (null has no effect*/
	private String tray=null;
	
	/**
	 * example method to open a file and print the pages 
	 */
	public SilentPrint(String file_name) {

		/**
		 * if file name ends pdf, do the file otherwise 
		 * do every pdf file in the directory. We already know file or
		 * directory exists so no need to check that, but we do need to
		 * check its a directory
		 */
		if (file_name.toLowerCase().endsWith(".pdf")) {
			decodeAndPrintFile(file_name);
		} else {

			/**
			 * get list of files and check directory
			 */
			String[] files = null;
			File inputFiles = null;

			/**make sure name ends with a deliminator for correct path later*/
			if (!file_name.endsWith(separator))
				file_name = file_name + separator;

			try {
				inputFiles = new File(file_name);

				if (!inputFiles.isDirectory()) {
					System.err.println(
						file_name + " is not a directory. Exiting program");
				}
				files = inputFiles.list();
			} catch (Exception ee) {
				LogWriter.writeLog(
					"Exception trying to access file " + ee.getMessage());
			}

			/**now work through all pdf files*/
			long fileCount = files.length;

			for (int i = 0; i < fileCount; i++) {

				if (files[i].toLowerCase().endsWith(".pdf")) {
					logMessage(file_name + files[i]);

					decodeAndPrintFile(file_name + files[i]);
				}
			}
		}
	}

	/**
	 * routine to decode a file and print it
	 */
	private void decodeAndPrintFile(String file_name) {
		
		/**
		 * open the file and get page count
		*/
		try {
			
			logMessage("Opening file :" + file_name+" to print.");
			
			decode_pdf = new PdfDecoder(true);
			//decode_pdf.setExtractionMode(0, 72,1);
			decode_pdf.openPdfFile(file_name);
			
			/**get number of pages*/
			//pageCount=decode_pdf.getPageCount();
			
		} catch (Exception e) {
			reportError("Exception " + e + " in pdf code");
		}
		

		/**
		 * print if allowed and values found
		 */
		if ((decode_pdf.isEncrypted())&& (!decode_pdf.isExtractionAllowed()))
			logMessage("Encrypted settings");
		else
			printAllCopies();
			
		
		/**close the pdf file*/
		decode_pdf.closePdfFile();

	}
	
	/**
	 * top level routine used to print the copies in a loop
	 */
	final private void printAllCopies() {
		
		/**loop to print each copy of document in turn
		 * 
		 * JPS also has a function to set number of copies but not at a document level
		 * */
		for(int currentCopy=0;currentCopy<copiesToPrint;currentCopy++){
		
			/**tell user and add 1 so user sees list 1,2,3 not 0,1*/
			logMessage("========================");
			logMessage("\nPrinting copy "+(currentCopy+1));
			logMessage("========================");
			
			printPages();
			
		}
	}

	/**PRINTING CODE
	 * if you put this into a thread you will need to synchronize 
	 * and ensure terminated if program exits
	 * Checks printer is present and sets tray if supported.
	 * 
	 * Uses pageable interface so does not work under 1.3
	 */
	private void printPages() {
		
		try{
			/**
			 * setup print job
			 */
			PrinterJob printJob = PrinterJob.getPrinterJob();
			PageFormat pf = printJob.defaultPage();
			
			//<start-13>
			//used if you want to alter tray or orientation,etc
			PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
			
			/**
			 * choose the printer, testing if printer in list
			 */
			PrintService[] service=PrinterJob.lookupPrintServices(); //list of printers
			//enable if on list otherwise error
			boolean matchFound=false;
			int count=service.length;
			for(int i=0;i<count;i++){
				if(service[i].getName().indexOf(printer)!=-1){
					printJob.setPrintService(service[i]);
					i=count;
					matchFound=true;
				}
			}
			
			/**
			 * if printer is not on list, report 
			 * otherwise continue.
			 */
			if(!matchFound){
				String list="";
				for(int i=0;i<count;i++)
					list=list+service[i].getName()+",";
				
				this.reportError("Printer "+printer+" not supported. Options="+list);
			}else{	
				
				/**
				 * sample code to see if tray vaues supported and select (not used by default)
				 */
				if(tray!=null){
					boolean hasTrayValues=printJob.getPrintService().isAttributeCategorySupported(javax.print.attribute.standard.Media.class);
					
					/**
					 * get tray values supported (has to be done each time as printer can change)
					 */
					if(hasTrayValues){
						
						/** get possible values*/
						//Hashtable possibleValues=new Hashtable();
						Map<String, Object> possibleValues = new HashMap<String, Object>();
						//Class category = Media.class;
						Object o = printJob.getPrintService().getSupportedAttributeValues(Media.class, null, null);
						
						if (o == null) {
							logMessage("Attribute Media.class not supported");
						} else if (o.getClass().isArray()) {
							// Attribute values are a set of values
							for (int i=0; i<Array.getLength(o); i++) {
								Object v = Array.get(o, i);
								// v is one of the possible values
								possibleValues.put(v.toString(),v);
								logMessage("Atribute values "+v.toString());
							}
						}
						
						/**add tray value if support to attributes*/
						Object trayValue=possibleValues.get(tray);
						if(trayValue!=null)
							attributeSet.add((Attribute) trayValue);
						
					}else{
						logMessage("Tray "+tray+" not implemented");	
						logMessage("Atributes implemented are ");
						Class[] c=printJob.getPrintService().getSupportedAttributeCategories();
						for(int j=0;j<c.length;j++)
						logMessage(">>"+c[j].getName());
					}
				}
				
				/**
				 * show values for debugging
				 */
				if(debugCode){
					Attribute[] attribs=attributeSet.toArray();
					int count1=attribs.length;
					for(int i=0;i<count1;i++)
						logMessage(i+" "+attribs[i].getName()+" "+attribs[i].toString());
				}
				//<end-13>
				
				/**
				 * at last we can do the printing
				 */
				
				//Create default Page format A4
				Paper paper = new Paper();
				
				//A4 borderless (setting may need adjustment
				paper.setSize(595, 842);
                 paper.setImageableArea(0, 0, 595, 842);
				
				pf.setPaper(paper);
				
				printJob.setCopies(1);
				
				/**
				 * workaround to improve performance on PCL printing 
				 * by printing using drawString or Java's glyph if font
				 * available in Java
				 */
				//decode_pdf.setTextPrint(PdfDecoder.NOTEXTPRINT); //normal mode - only needed to reset
				//decode_pdf.setTextPrint(PdfDecoder.TEXTGLYPHPRINT); //intermediate mode - let Java create Glyphs if font matches
				//decode_pdf.setTextPrint(PdfDecoder.TEXTSTRINGPRINT); //try and get Java to do all the work
				
				/**scaling options for printing - same functionality as Acrobat*/
				//No scaling (default)
				//decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_NONE);
				//Fit to scaling
				//decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_FIT_TO_PRINTER_MARGINS);
				//Reduce to scaling
				//decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_REDUCE_TO_PRINTER_MARGINS);
				
				decode_pdf.setPageFormat(pf); 
				
				//pages to print
				decode_pdf.setPagePrintRange(1,decode_pdf.getPageCount());
				
                printJob.setPageable(decode_pdf);
                
                //Print PDF document
                //printJob.print(attributeSet); /use if you alter attributeSet
                printJob.print();
            //<start-13>   
			}
			//<end-13>
		}catch(Exception ee){
			LogWriter.writeLog("Exception "+ee+" printing");
		}catch(Error err){
			LogWriter.writeLog("Error "+err+" printing");
		}

	}

	/**single routine to log activity for easy debugging*/
    private static void logMessage(String message){
    		
    		//change to suit your needs
    		if(debugCode){
    			System.out.println(message);
    			LogWriter.writeLog(message);
    		}
    }

	/**single routine so error handling can be easily setup*/
    private void reportError(String message){
    		
    		//change to suit your needs
    		System.err.println(message);
    		LogWriter.writeLog(message);
    }

	
	/**
	 * main routine which checks for any files passed and runs the demo
	 */
	public static void main(String[] args) {
		logMessage("Simple demo to print pages");

		//check user has passed us a filename and use default if none
		if (args.length != 1){
			logMessage("File name or directory must be supplied");
		}
			
		String file_name = args[0];

		logMessage("File :" + file_name);
		
		//check file exists
		File pdf_file = new File(file_name);

		//if file exists, open and get number of pages
		if (pdf_file.exists() == false) {
			logMessage("File " + file_name + " not found");
		}
		//SilentPrint images1 =
		//	new SilentPrint(file_name);
	}
	
	

}
