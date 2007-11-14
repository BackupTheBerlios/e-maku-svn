package common.printer;

import java.io.*;
import java.util.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;

import common.misc.*;

public class PrintingManager {
	
	public static enum ImpresionType {PLAIN,POSTSCRIPT,PDF};	
	private ImpresionType type;
	private DocFlavor docFlavor;
	private PrintService[] jps;

	public PrintingManager (
			ImpresionType type,
			ByteArrayInputStream is,
			boolean silent,
			int copies,
			String printer) throws FileNotFoundException, PrintException {
		
		this.type = type;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(copies));
		Date date = new Date();
		pras.add(new JobName("emaku-job-"+date.toString(),Locale.getDefault()));
		jps = CommonConstants.printServices;
		if ((jps==null ) || (jps.length == 0)) {
			showErroDialog();
			return;
		}
		PrintService defaultService =null;
		System.out.println("Printer name " + printer);
		if (this.type.equals(ImpresionType.PLAIN)) {
			String os = System.getProperty("os.name");
			if (os.equals("Linux")){
				docFlavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST;
			}
			else {
				docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			}
		}
		else if (type.equals(ImpresionType.PDF)) {
			docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		}
		PrintService ps = selectPrinservice(printer);
		if (silent && ps!=null) {
			print(ps,is,pras);
		}
		else {
			defaultService = ServiceUI.printDialog(null, 200, 200,jps, ps, docFlavor, pras);
			if (defaultService!=null) {
				CommonConstants.printSelect = defaultService;
				print(defaultService,is,pras);
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		is = null;
	}
	
	public PrintingManager (
			PostScriptManager postScriptManager,
			boolean silent,
			int copies,
			String printer) throws PrintException {
		this.docFlavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(copies));
		Date date = new Date();
		pras.add(new JobName("emaku-job-"+date.toString(),Locale.getDefault()));
		jps = CommonConstants.printServices;
		if ((jps==null ) || jps.length == 0) {
			showErroDialog();
			return;
		}
		PrintService defaultService = null;
		System.out.println("Printer name " + printer);
		PrintService ps = selectPrinservice(printer);
		if (silent && ps!=null) {
			print(ps,postScriptManager,pras);
		}
		else {
			defaultService = ServiceUI.printDialog(null, 200, 200,jps, ps,docFlavor,pras);
			if (defaultService!=null) {
				CommonConstants.printSelect = defaultService;
				print(defaultService,postScriptManager,pras);
			}
		}
	}
	
	private void print(PrintService ps, Object printData, PrintRequestAttributeSet pras) throws PrintException {
		DocPrintJob job = ps.createPrintJob();
		Doc doc = new SimpleDoc(printData, docFlavor, null);
		System.out.println(docFlavor);
		job.print(doc, pras);
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