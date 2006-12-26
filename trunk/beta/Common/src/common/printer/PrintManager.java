package common.printer;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

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
import javax.swing.JOptionPane;

import common.misc.CommonConst;

public class PrintManager {
	
	public static enum ImpresionType {PLAIN,POSTSCRIPT,PDF};
	private ImpresionType type;
	private DocFlavor docFlavor;
	private static boolean lastError = false;
	private PrintService[] jps;

	public PrintManager (
			ImpresionType type,
			ByteArrayInputStream is,
			boolean silent,
			int copies,
			String printer) throws FileNotFoundException, PrintException {
		
		this.type = type;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(copies));
		jps = CommonConst.printServices;
		if ((jps==null ) || (jps.length == 0)) {
			if (!lastError) {
				JOptionPane.showMessageDialog(
					null,
					"No existen medios de impresion\n"+
					"disponibles, debe configurar una\n"+
					"impresora en su sistema, y reiniciar\n"+
					"el programa para que se apliquen los cambios"
					);
				lastError = true;
			}
			return;
		}
		PrintService defaultService =null;
		System.out.println("Printer name " + printer);
		if (this.type.equals(ImpresionType.PLAIN)) {
			docFlavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
		}
		else if (type.equals(ImpresionType.PDF)) {
			docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		}
		
		if (!silent) {
			defaultService = ServiceUI.printDialog(null, 200, 200,jps, selectPrinservice(printer), docFlavor, pras);
			if (defaultService!=null) {
				CommonConst.printSelect = defaultService;
				print(defaultService,is,pras);
			}

		}
		else {
			PrintService ps = selectPrinservice(printer);
			if (ps!=null) {
				print(ps,is,pras);
			}
			else {
				System.out.println("Impresora "+printer+"no econtrada");
			}
		}
	}
	
	public PrintManager (
			PostScriptManager postScriptManager,
			boolean silent,
			int copies,
			String printer) throws PrintException {
		this.docFlavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(copies));
		jps = CommonConst.printServices;
		if ((jps==null ) || jps.length == 0) {
			if (!lastError) {
				JOptionPane.showMessageDialog(
						null,
						"No existen medios de impresion\n"+
						"disponibles, debe configurar una\n"+
						"impresora en su sistema, y reiniciar\n"+
						"el programa para que se apliquen los cambios"
						);
				lastError = true;
			}
			return;
		}
		PrintService defaultService = null;
		System.out.println("Printer name " + printer);
		
		if (!silent) {
			defaultService = ServiceUI.printDialog(null, 200, 200,jps, selectPrinservice(printer),docFlavor,pras);
			if (defaultService!=null) {
				CommonConst.printSelect = defaultService;
				print(defaultService,postScriptManager,pras);
			}
		}
		else {
			PrintService ps = selectPrinservice(printer);
			if (ps!=null) {
				print(ps,postScriptManager,pras);
			}
			else {
				System.out.println("Impresora "+printer+"no econtrada");
			}
		}
	}
	
	private void print(PrintService ps, Object printData, PrintRequestAttributeSet pras) throws PrintException {
		DocPrintJob job = ps.createPrintJob();
		Doc doc = new SimpleDoc(printData, docFlavor, null);
		job.print(doc, pras);
}
	
	private PrintService selectPrinservice(String printer) {
		for (PrintService ps : jps) {
			if (ps.getName().equals(printer)) {
				return ps;
			}
		}
		return PrintServiceLookup.lookupDefaultPrintService();
	}
}