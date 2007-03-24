package common.printer.plainViewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * PrinterViewsArray.java Creado el 1 de Febrero 2007
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
 * @author <A href='mailto:xtingray@qhatu.net'>Gustavo González</A>
 */

public class PrinterViewsArray {

    ByteArrayOutputStream byteArray;
	private Vector <ByteArrayOutputStream>printerViews = new Vector<ByteArrayOutputStream>();
	int currentPage = 0;

	// Constructor

	public PrinterViewsArray() {
		byteArray = new ByteArrayOutputStream();
		initPrinterViewArray();
	}

	// Init the file header for plain text printing
	
	public void initPrinterViewArray() {
			//Reset printer
			byteArray.write(0x1b); // ESC
			byteArray.write(0x40); // Reset printer
			//Deselect condensed printing
			byteArray.write(0x1b); // ESC
			byteArray.write(0x12); // Deselect condensed printing
	}
	
	// Initialize a printer view
	
	public void initPrinterPage(int pageNum) {
		currentPage = pageNum;
		byteArray = new ByteArrayOutputStream();	
	    byteArray.write(0x1b); // ESC
	    byteArray.write(0x0c); // Form Feed
	}	
	
	// Writes a string to the output file

	public void addStringToPrinterView(String textString) {
			final StringBuffer buffer = new StringBuffer("");
			buffer.append(textString);
			final byte[] text = buffer.toString().getBytes();
			byteArray.write(text, 0, text.length);
	}

	// Enables the printer font bold mode

	public void activeBold() {	
			//enable bold
		    byteArray.write(0x1b); // ESC
		    byteArray.write(0x45); // E
	}

	// Disables the printer font bold mode 

	public void disableBold() {
			byteArray.write(0x1b); // ESC
			byteArray.write(0x46); // F			
	}

	// Closes the output file

	public void closePrinterPage() {
		try {		 
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
