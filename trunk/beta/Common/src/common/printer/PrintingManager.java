package common.printer;

import java.awt.print.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;

import org.jpedal.*;
import org.jpedal.exception.*;

import sun.security.mscapi.*;

import common.misc.*;

public class PrintingManager {
	
	public static enum ImpresionType {PLAIN,POSTSCRIPT,PDF};	
	private ImpresionType type;
	private DocFlavor docFlavor;
	private PrintService[] jps;
	private static int count=0;
	private String jobName;
	private ByteArrayInputStream is;
	private boolean silent;
	private HashPrintRequestAttributeSet pras;
	private String printer;
	private int width;
	private int height;
	
	public PrintingManager (
			ImpresionType type,
			ByteArrayInputStream is,
			boolean silent,
			int copies,
			String printer, int width, int height) throws FileNotFoundException, PrintException {
		
		this.is = is;
		this.silent = silent;
		this.type = type;
		this.printer = printer;
		this.width = width;
		this.height = height;
		
		pras = new HashPrintRequestAttributeSet();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		jobName = "empj"+sdf.format(date)+(++count);
		pras.add(new JobName(jobName,Locale.getDefault()));
		pras.add(new Copies(copies));
		jps = CommonConstants.printServices;
		if ((jps==null ) || (jps.length == 0)) {
			showErroDialog();
			return;
		}
		
		String os = System.getProperty("os.name");
		if (this.type.equals(ImpresionType.PLAIN)) {
			if (os.equals("Linux")){
				docFlavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST;
			}
			else {
				docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			}
			printTextPlain();
		}
		else if (type.equals(ImpresionType.PDF)) {
			try {
				printPDF();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void printTextPlain() throws PrintException {
		PrintService defaultService =null;
		PrintService ps = selectPrinservice(printer);
		if (silent && ps!=null) {
			print(ps,is,pras);
		}
		else {
			try {
				defaultService = ServiceUI.printDialog(null, 200, 200,jps, ps, null, pras);
				if (defaultService!=null) {
					CommonConstants.printSelect = defaultService;
					print(defaultService,is,pras);
				}
				is.close();
			} catch (NullPointerException NPEe) {
				NPEe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		is = null;
	}
	
	private void printPDF() throws PrinterException {
		
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintService(selectPrinservice(printer));
		PageFormat pf = printJob.defaultPage();
		boolean printFile=true;
		
		Paper paper = new Paper();
		paper.setSize(width, height);
		paper.setImageableArea(0,0,width,height);
		
		pf.setPaper(paper);
		PdfDecoder decode_pdf = new PdfDecoder();
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte [] buffer = new byte[255];
			int len = 0;
			while ((len=is.read(buffer))>0) {
				os.write(buffer,0,len);
			}
			decode_pdf.openPdfArray(os.toByteArray());
			os.close();
			is.close();
		} catch (PdfException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printJob.setPageable(decode_pdf);
		decode_pdf.setPageFormat(pf);
		pras.add(new PageRanges(1,decode_pdf.getPageCount()));
		if (!silent) {
			printFile=printJob.printDialog(pras);
		}
		System.out.println("printer name=>"+printJob.getPrintService().getName());
		if (printFile) {
			printJob.print(pras);
		}
		
		decode_pdf.closePdfFile();
	}
	
	private void print(PrintService ps, Object printData, PrintRequestAttributeSet pras) throws PrintException {
		DocPrintJob job = ps.createPrintJob();
		System.out.println("printer name=>"+ps.getName());
		Doc doc = new SimpleDoc(printData, docFlavor,null);
		job.print(doc, pras);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private PrintService selectPrinservice(String printer) {
		for (PrintService ps : jps) {
			if (ps!=null && ps.getName().equals(printer)) {
				return ps;
			}
		}
		return PrintServiceLookup.lookupDefaultPrintService();
	}
	
	private void showErroDialog() {
		JOptionPane.showMessageDialog(
				null,
				"No existen medios de impresion\n"+
				"disponibles, debe configurar una\n"+
				"impresora en su sistema, y reiniciar\n"+
				"el programa para que se apliquen los cambios"
				);
	}
}