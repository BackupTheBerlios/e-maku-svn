package common.pdf.pdfviewer.utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.standard.PageRanges;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.jpedal.PdfDecoder;

import common.misc.language.Language;
import common.pdf.pdfviewer.gui.GUIFactory;

/**
 * handle printing for GUI viewer
 **/
public class Printer {
	
	/**flag to stop mutliple prints*/
	private static int printingThreads=0;
	
	/**page range to print*/
	int rangeStart=1,rangeEnd=1;
	
	
	/**provide user with visual clue to print progress*/
	Timer updatePrinterProgress=null;

	private ProgressMonitor status = null;
	
	public void printPDF(final PdfDecoder decode_pdf,final GUIFactory currentGUI) {
		
		//provides atomic flag on printing so we don't exit until all done
		printingThreads++;
		
		/**default settings*/
		rangeStart=1;
		rangeEnd=decode_pdf.getPageCount();
		
		/**
		 * printing in thread to improve background printing -
		 * comment out if not required
		 */
		Thread worker = new Thread() {
			public void run() {
				
				boolean canceled=true;
				
				try {
					  /* Nota: Opciones utilizables en el futuro
					   * 
				        case 0: //No scaling
					    decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_NONE);
					
				        case 1: //Fit to scaling
					    decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_FIT_TO_PRINTER_MARGINS);
					
				        case 2: //Reduce to scaling
					    decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_REDUCE_TO_PRINTER_MARGINS);
					    */

					
					//setup print job and objects
					PrinterJob printJob = PrinterJob.getPrinterJob();
					
					PageFormat pf = printJob.defaultPage();

					Paper paper = new Paper();
					paper.setSize(595, 842);
					paper.setImageableArea(43, 43, 509, 756);
					
						
					decode_pdf.setPrintPageScalingMode(PdfDecoder.PAGE_SCALING_NONE);
					pf.setPaper(paper);
						
					//VERY useful for debugging! (shows the imageable
					// area as a green box bordered by a rectangle)
					decode_pdf.showImageableArea();
						
					/**
					 * Example 1 - uses Pagable interface and can get page Range
					 * 
					 * RECOMMENDED for Java 1.4 and Above
					 */
					//setup JPS to use JPedal
					printJob.setPageable(decode_pdf);
					decode_pdf.setPageFormat(pf); 
						
					//setup default values to padd into JPS
					PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
					aset.add(new PageRanges(1,decode_pdf.getPageCount()));
			
					boolean printFile=printJob.printDialog(aset);
  				    //set page range
					PageRanges r=(PageRanges) aset.get(PageRanges.class);
					if((r!=null)&&(printFile)){
						decode_pdf.setPagePrintRange((SetOfIntegerSyntax) r);
						int[][] values=r.getMembers();
						int[] pages=values[0];
						int p1=pages[0];
						int p2=pages[1];
							
						//all returns huge number not page end range
						if(p2==2147483647)
							p2=decode_pdf.getPageCount();
						
						if(p1<p2) {
							rangeStart=p1;
							rangeEnd=p2;
						}
						else {
							rangeStart=p2;
							rangeEnd=p1;
						}			
						
						/**
						 * popup to show user progress
						 */
						status = new ProgressMonitor(currentGUI.getFrame(),"","",1,100);
						
						/** used to track user stopping movement and call refresh every 2 seconds*/
						updatePrinterProgress = new Timer(1000,new ActionListener() {

							public void actionPerformed(ActionEvent event) {
								
								int currentPage=decode_pdf.getCurrentPrintPage();
								
								if(currentPage>0)
								updatePrinterProgess(decode_pdf,currentPage,rangeEnd);
								
								//make sure turned off
								if(currentPage==-1){
									updatePrinterProgress.stop();
									status.close();
								}				
							}
						});
						updatePrinterProgress.setRepeats(true);
						updatePrinterProgress.start();
							
						/**
						 * generic call to both Pageable and printable
						 */
						if (printFile) {
							printJob.print();
							canceled = false;
						}
						else {
							canceled=true;
						}
					}
				} catch (PrinterException ee) {
					ee.printStackTrace();
					currentGUI.showMessageDialog(ee.getMessage()+" "+ee+" "+" "+ee.getCause());
				} catch (Exception e) {
					e.printStackTrace();
					currentGUI.showMessageDialog("Excepcion "+e);
				} catch (Error err) {
					err.printStackTrace();
					currentGUI.showMessageDialog("Error "+err);
				}
				
				/**
				 * visual print update progress box
				 */
				if(updatePrinterProgress!=null){
					updatePrinterProgress.stop();
					status.close();
				}
				/**report any or our errors 
				 * (we do it this way rather than via PrinterException as MAC OS X has a nasty bug in PrinterException)
				 */
				if(!canceled && !decode_pdf.isPageSuccessful()){
					String errorMessage="Problema encontrado!\n"+decode_pdf.getPageFailureMessage()+"\n";
					
					if(decode_pdf.getPageFailureMessage().toLowerCase().indexOf("memory") != -1)
						errorMessage += "Memoria insuficiente!";
					
					currentGUI.showMessageDialog(errorMessage);
				}
				
				printingThreads--;
				
				//redraw to clean up
				decode_pdf.invalidate();
				decode_pdf.updateUI();
				decode_pdf.repaint();
				
				if((!canceled)){
					currentGUI.showMessageDialog(Language.getWord("IMPFIN"));
					decode_pdf.resetCurrentPrintPage();
				}
			}
		};
		worker.start();
		
	}
	
	/**visual print indicator*/
	private String dots=".";
	
	private void updatePrinterProgess(PdfDecoder decode_pdf,int currentPage, int pageCount) {
		
		if(status.isCanceled()) {
	
			decode_pdf.stopPrinting();
			updatePrinterProgress.stop();
			status.close();
			printingThreads--;
			return;
		}
		
		//update visual clue
		dots=dots+".";
		if(dots.length()>8)
			dots=".";
		
		//Calculate no of pages printing
		int noOfPagesPrinting=(rangeEnd-rangeStart+1);
		
		// calculate which page we are currently printing
		int currentPrintingPage=(currentPage-rangeStart);
		
		//allow for backwards
		boolean isBackwards=((currentPrintingPage<=0));
		
		if(rangeStart==rangeEnd)
			isBackwards=false;
		
		if((isBackwards))
			noOfPagesPrinting=(rangeStart-rangeEnd+1);
		
		int percentage = (int) (((float)currentPrintingPage / (float)noOfPagesPrinting) * 100);
		
		if((!isBackwards)&&(percentage<1))
			percentage=1;
		
		//invert percentage so percentage works correctly
		if(isBackwards) {
			percentage=-percentage;
			currentPrintingPage=-currentPrintingPage;
		}
		
		if(noOfPagesPrinting==1)
			percentage=50;
		
		status.setProgress(percentage);
		String message=currentPrintingPage + " of " + 
		noOfPagesPrinting + ": " + percentage + "%"+" "+dots;
		
		if(isBackwards) {
			status.setNote("Reversed Printing " + message);
		}
		else {
			status.setNote("Printing " + message);
		}
			
	}
	
	public boolean isPrinting() {
		
		return printingThreads>0;
	}
	
}
