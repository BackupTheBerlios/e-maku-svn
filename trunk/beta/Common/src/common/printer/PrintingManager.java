package common.printer;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JOptionPane;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.objects.PrinterOptions;

import common.misc.CommonConstants;

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
			PostScriptManager postScriptManager,
			boolean silent,
			int copies,
			String printer) throws PrintException {
		this.docFlavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(copies));
		jps = CommonConstants.printServices;
		if ((jps==null ) || jps.length == 0) {
			showErroDialog();
			return;
		}
		PrintService defaultService = null;
		System.out.println("Printer name " + printer);
		PrintService ps = selectPrinservice(printer);
		if (silent && ps!=null) {
			System.out.println("fue nulo...");
			print(ps,postScriptManager,pras);
		}
		else {
			System.out.println("jps: "+jps);
			System.out.println("ps: "+ps);
			System.out.println("docFlavor: "+docFlavor);
			System.out.println("pras: "+pras);
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

			defaultService = ServiceUI.printDialog(null, 200, 200,jps, ps,docFlavor,pras);
			if (defaultService!=null) {
				System.out.println(" no es silenciosa "+postScriptManager);
				CommonConstants.printSelect = defaultService;
				print(defaultService,postScriptManager,pras);
			}
		}
	}

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
				// Con cups no funciona en TEXT_PLAIN_HOST
				// Cambiado a autosense ....
				docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
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
		else if (type.equals(ImpresionType.POSTSCRIPT)) {
				docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			if (!silent) {
				PrintService defaultService = ServiceUI.printDialog(null, 200, 200,jps, selectPrinservice(printer), docFlavor, pras);
				if (defaultService!=null) {
					CommonConstants.printSelect = defaultService;
					print(defaultService,is,pras);
				}
			}
			else {
				PrintService ps = selectPrinservice(printer);
				if (ps!=null) {
					System.out.println("Sera que imprime plano?");
					print(ps,is,pras);
				}
				else {
					System.out.println("Impresora "+printer+"no econtrada");
				}
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
		System.out.println("printer: "+printer);
		System.out.println("selectPrintService: "+selectPrinservice(printer));
		printJob.setPrintService(selectPrinservice(printer));
		PageFormat pf = printJob.defaultPage();
		boolean printFile=true;
		
		Paper paper = new Paper();
		paper.setSize(height,width);
		paper.setImageableArea(0,0,width,height);
		
		pf.setPaper(paper);
		PdfDecoder decode_pdf = new PdfDecoder();
		//decode_pdf.setSize(width, height);
		System.out.println("tamaño del papel: "+width+" ,"+height);
		decode_pdf.setPageFormat(pf);
		decode_pdf.setUsePDFPaperSize(false); 
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
		try {
			decode_pdf.setPagePrintRange(1, decode_pdf.getPageCount());
		} catch (PdfException e) {
			// TODO Auto-generated catch block
			System.out.println("Se daño paginando");
			e.printStackTrace();
		}
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
		System.out.println("intentando imprimir postscript");
		DocPrintJob job = ps.createPrintJob();
		System.out.println("printer name=>"+ps.getName());
		System.out.println("printData=>"+printData);
		System.out.println("docFlavor=>"+docFlavor);
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