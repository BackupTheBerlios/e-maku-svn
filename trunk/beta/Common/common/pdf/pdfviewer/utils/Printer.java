package common.pdf.pdfviewer.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import javax.print.PrintException;

import org.jpedal.PdfDecoder;

import common.printer.PrintManager;

/**
 * handle printing for GUI viewer
 **/
public class Printer {
	private boolean printintg = false;
	public void printPDF(final PdfDecoder decode_pdf) {
		printintg = true;
		try {
			new PrintManager(
					PrintManager.ImpresionType.PDF,
					new ByteArrayInputStream(decode_pdf.getPdfBuffer()),
					false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PrintException e) {
			e.printStackTrace();
		}
		printintg = false;
	}
	
	public boolean isPrinting() {
		return printintg;
	}
}
