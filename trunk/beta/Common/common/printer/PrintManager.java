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

import common.misc.CommonConst;

public class PrintManager {
	
	public static enum ImpresionType {PLAIN,POSTSCRIPT,PDF};
	private ImpresionType type;
	private DocFlavor docFlavor;
	
	public PrintManager (ImpresionType type, ByteArrayInputStream is,boolean silent) throws FileNotFoundException, PrintException {
		this.type = type;
		
		if (this.type.equals(ImpresionType.PLAIN)) {
			this.docFlavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
		}
		else if (this.type.equals(ImpresionType.PDF)) {
			this.docFlavor = DocFlavor.INPUT_STREAM.PDF;
		}
		PrintService defaultService = CommonConst.defaultPrintService;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		if (!silent) {
			PrintService printServices[] = PrintServiceLookup.lookupPrintServices(this.docFlavor, pras);
			defaultService = ServiceUI.printDialog(null, 200, 200,printServices, defaultService, this.docFlavor, pras);	
		}
		if (defaultService != null) {
			DocPrintJob job = defaultService.createPrintJob();
			Doc doc = new SimpleDoc(is, docFlavor, null);
			job.print(doc, pras);
		}
	}
	
	public PrintManager (PostScriptManager postScriptManager, boolean silent) throws PrintException {
		PrintService defaultService = CommonConst.defaultPrintService;
		this.docFlavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		if (!silent) {
			PrintService printServices[] = PrintServiceLookup.lookupPrintServices(this.docFlavor, pras);
			defaultService = ServiceUI.printDialog(null, 200, 200,printServices, defaultService, this.docFlavor,pras);	
		}
		if (defaultService != null) {
			DocPrintJob job = defaultService.createPrintJob();
			Doc doc = new SimpleDoc(postScriptManager, docFlavor, null);
			job.print(doc, null);
		}
	}
}