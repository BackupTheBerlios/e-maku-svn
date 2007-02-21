package common.printer.plainViewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PrinterViewsArray {

	ByteArrayOutputStream[] printerViews;
	int pagesTotal;
	int currentPage = 0;

	// Constructor

	public PrinterViewsArray(int pagesTotal) {
		this.pagesTotal = pagesTotal;
		initPrinterViews();
	}

	// Opens the output file for plain text report

	public void initPrinterViews() {
			printerViews  = new ByteArrayOutputStream[pagesTotal];
			printerViews[currentPage] = new ByteArrayOutputStream();
			initPrinterViewArray();
	}

	// Initialize a printer view
	
	public void initPrinterPage(int pageNum) {
		currentPage = pageNum;
		printerViews[currentPage] = new ByteArrayOutputStream();
	}
	
	// Update page index
	
	public void setCurrentPage(int pageNum) {
		currentPage = pageNum;
		System.out.println("*** Paginando: " + currentPage);
	}
	
	
	// Writes a string to the output file

	public void addStringToPrinterView(String textString) {
			final StringBuffer buffer = new StringBuffer("");
			buffer.append(textString);
			final byte[] text = buffer.toString().getBytes();
			printerViews[currentPage].write(text, 0, text.length);
	}

	// Init the file header for plain text printing
	
	public void initPrinterViewArray() {
			//Reset printer
			printerViews[currentPage].write(0x1b); // ESC
			printerViews[currentPage].write(0x40); // Reset printer
			//Deselect condensed printing
			printerViews[currentPage].write(0x1b); // ESC
			printerViews[currentPage].write(0x12); // 
	}

	// Enables the printer font bold mode

	public void activeBold() {	
			//enable bold
			printerViews[currentPage].write(0x1b); // ESC
			printerViews[currentPage].write(0x45); // E
	}

	// Disables the printer font bold mode 

	public void disableBold() {
			// disable bold
			printerViews[currentPage].write(0x1b); // ESC
			printerViews[currentPage].write(0x46); // F
	}

	// Inserts a new page escape code into the text file

	public void startNewPage() {
			printerViews[currentPage].write(0x1b); // ESC
			printerViews[currentPage].write(0x0c); // Form Feed		    
	}

	// Closes the output file

	public void closePrinterPage() {
		try {
			printerViews[currentPage].close();		 
		}
		catch (IOException e) {
			System.out.println("ERROR: No se pudo cerrar el archivo.");
		} 			
	}

	// Returns the text plain file

	public ByteArrayOutputStream[] getReportFile() {
		return printerViews;
	}

//	Class end		
}
