package common.pdf.pdfviewer.utils;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Timer;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JComboBox;

import org.jpedal.PdfDecoder;
import org.jpedal.examples.simpleviewer.utils.Messages;
import org.jpedal.objects.PrinterOptions;
import org.jpedal.utils.LogWriter;


public class Printer {
	
	/**flag to stop mutliple prints*/
	private static int printingThreads=0;
	
	/**page range to print*/
	int rangeStart=1,rangeEnd=1;
	
	/**type of printing - all, odd, even*/
	int subset=PrinterOptions.ALL_PAGES;
	
	JComboBox scaling=null;
	
	/**Check to see if Printing cancelled*/
	boolean wasCancelled=false;
	
	/**Allow Printing Cancelled to appear once*/
	boolean messageShown=false;
	
	boolean pagesReversed=false;
	
	/**provide user with visual clue to print progress*/
	Timer updatePrinterProgress=null;

	public void printPDF(final PdfDecoder decode_pdf) {
		
		//provides atomic flag on printing so we don't exit until all done
		printingThreads++;
		
		/**
		 * printing in thread to improve background printing -
		 * comment out if not required
		 */
		Thread worker = new Thread() {
			public void run() {
				
				boolean printFile=false;
				
				try {

					//setup print job and objects
					PrinterJob printJob = PrinterJob.getPrinterJob();
					PageFormat pf = printJob.defaultPage();
					
					/**
					 * default page size
					 */
					Paper paper = new Paper();
					paper.setSize(595, 842);
					paper.setImageableArea(43, 43, 509, 756);
					
					pf.setPaper(paper);
					
					//VERY useful for debugging! (shows the imageable
					// area as a green box bordered by a rectangle)
					//decode_pdf.showImageableArea();
					
					/**
					 * SERVERSIDE printing IF you wish to print using a
					 * server, do the following
					 * 
					 * See SilentPrint.java example
					 *  
					 */
					
					/**
					 * workaround to improve performance on PCL printing 
					 * by printing using drawString or Java's glyph if font
					 * available in Java
					 */
					//decode_pdf.setTextPrint(PdfDecoder.NOTEXTPRINT); //normal mode - only needed to reset
					//decode_pdf.setTextPrint(PdfDecoder.TEXTGLYPHPRINT); //intermediate mode - let Java create Glyphs if font matches
					//decode_pdf.setTextPrint(PdfDecoder.TEXTSTRINGPRINT); //try and get Java to do all the work
					
					//<start-13>
					/**/
					
					/**
					 * Example 1 - uses Pagable interface and can get page Range 
					 * 
					 * ADDITIONAL  FEATURES NOT AVAILABLE (see Example 3)
					 * 
					 * RECOMMENDED for Java 1.4 and Above
					 */
					//setup JPS to use JPedal
					printJob.setPageable(decode_pdf);
					
					//setup default values to padd into JPS
					PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
					aset.add(new PageRanges(1,decode_pdf.getPageCount()));
					
					// useful debugging code to show supported values and values returned by printer
					//Attribute[] settings = aset.toArray();
					
					//Class[] attribs=printJob.getPrintService().getSupportedAttributeCategories();
					//for(int i=0;i<attribs.length;i++)
					//System.out.println(i+" "+attribs[i]);
					
					//for(int i=0;i<settings.length;i++) //show values set by printer
					//	System.out.println(i+" "+settings[i].toString()+" "+settings[i].getName());

                     decode_pdf.setPageFormat(pf); 
                     printFile=printJob.printDialog(aset);

					//set page range
					PageRanges r=(PageRanges) aset.get(PageRanges.class);
					if((r!=null)&&(printFile)){
						
						//Option A - use range
						decode_pdf.setPagePrintRange((SetOfIntegerSyntax) r);
						
						//Option B - use start and end value checking in right order
						int[][] values=r.getMembers();
						int[] pages=values[0];
						int p1=pages[0];
						int p2=pages[1];
						
						//all returns huge number not page end range
						if(p2==2147483647)
							p2=decode_pdf.getPageCount();
						
						if(p1<p2){
							rangeStart=p1;
							rangeEnd=p2;
						}else{
							rangeStart=p2;
							rangeEnd=p1;
						}
						
						//page range can also be set with
						//decode_pdf.setPagePrintRange(p1,p2);
						
					}
					
					
					/**
	                //<end-13>
					
					// Example 2 - uses printable interface and can get page Range
					//RECOMMENDED for Java 1.3
					//allow user to edit settings and select printing with 1.3 support
					printJob.setPrintable(decode_pdf, pf);
					
					printFile=printJob.printDialog();
					
					/**
					 * example 3 - custom dialog so we can copy Acrobat PDF settings
                     * (removed from OS versions)
					 */

					/** used to track user stopping movement and call refresh every 2 seconds*/

					/**
					 * generic call to both Pageable and printable
					 */
					if (printFile)
						printJob.print();
					
				} catch (PrinterException ee) {
					ee.printStackTrace();
					LogWriter.writeLog("Exception " + ee + " printing");
					//<start-13>
					//<end-13>
				} catch (Exception e) {
					LogWriter.writeLog("Exception " + e + " printing");
					e.printStackTrace();
				} catch (Error err) {
					err.printStackTrace();
					LogWriter.writeLog("Error " + err + " printing");
				}
				
				/**report any or our errors 
				 * (we do it this way rather than via PrinterException as MAC OS X has a nasty bug in PrinterException)
				 */
				if(!printFile && !decode_pdf.isPageSuccessful()){
					String errorMessage=Messages.getMessage("PdfViewerError.ProblemsEncountered")+decode_pdf.getPageFailureMessage()+"\n";
					
					if(decode_pdf.getPageFailureMessage().toLowerCase().indexOf("memory") != -1)
						errorMessage += Messages.getMessage("PdfViewerError.RerunJava")+
										Messages.getMessage("PdfViewerError.RerunJava1")+
										Messages.getMessage("PdfViewerError.RerunJava2");
					
				}
				
				printingThreads--;
				
				//redraw to clean up
				decode_pdf.invalidate();
				decode_pdf.updateUI();
				decode_pdf.repaint();
				
				
				if((printFile && !wasCancelled)){
					decode_pdf.resetCurrentPrintPage();
				}
			}

			//<end-13>
		};
		
		//start printing in background (comment out if not required)
		worker.start();
		
	}
	
	public boolean isPrinting() {
		return printingThreads>0;
	}
}