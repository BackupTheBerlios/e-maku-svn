package common.printer.plainViewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

public class PrinterViewsArray {

    ByteArrayOutputStream byteArray;
	private Vector <ByteArrayOutputStream>printerViews = new Vector<ByteArrayOutputStream>();
	//int pagesTotal;
	int currentPage = 0;

	// Constructor

	public PrinterViewsArray() {
		//this.pagesTotal = pagesTotal;
		byteArray = new ByteArrayOutputStream();
		initPrinterViewArray();
	}

	// Opens the output file for plain text report
/*
	public void initPrinterViews() {
			//printerViews  = new ByteArrayOutputStream[pagesTotal];
			//printerViews[currentPage] = new ByteArrayOutputStream();
			
			initPrinterViewArray();
	}
*/
	// Initialize a printer view
	
	public void initPrinterPage(int pageNum) {
		currentPage = pageNum;
		byteArray = new ByteArrayOutputStream();	
		//printerViews[currentPage] = new ByteArrayOutputStream();
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
			//printerViews[currentPage].write(text, 0, text.length);
			byteArray.write(text, 0, text.length);
	}

	// Init the file header for plain text printing
	
	public void initPrinterViewArray() {
			//Reset printer
			//printerViews[currentPage].write(0x1b); // ESC
			//printerViews[currentPage].write(0x40); // Reset printer
			byteArray.write(0x1b);
			byteArray.write(0x40);
			//Deselect condensed printing
			//printerViews[currentPage].write(0x1b); // ESC
			//printerViews[currentPage].write(0x12); //
			byteArray.write(0x1b);
			byteArray.write(0x12);
	}

	// Enables the printer font bold mode

	public void activeBold() {	
			//enable bold
		    byteArray.write(0x1b);
		    byteArray.write(0x45);
			// printerViews[currentPage].write(0x1b); // ESC
			// printerViews[currentPage].write(0x45); // E
	}

	// Disables the printer font bold mode 

	public void disableBold() {
			// disable bold
			// printerViews[currentPage].write(0x1b); // ESC
			// printerViews[currentPage].write(0x46); // F
			byteArray.write(0x1b);
			byteArray.write(0x46);
			
	}

	// Inserts a new page escape code into the text file

	public void startNewPage() {
			//printerViews[currentPage].write(0x1b); // ESC
			//printerViews[currentPage].write(0x0c); // Form Feed
		    byteArray.write(0x1b);
		    byteArray.write(0x0c);
	}

	// Closes the output file

	public void closePrinterPage() {
		try {
			//printerViews[currentPage].close();		 
			byteArray.close();
			printerViews.add(currentPage, byteArray);
		}
		catch (IOException e) {
			System.out.println("ERROR: No se pudo cerrar el archivo.");
		} 			
	}

	// Returns the text plain file

	public Vector<ByteArrayOutputStream> getReportFile() {
		return printerViews;
	}

//	Class end		
}
