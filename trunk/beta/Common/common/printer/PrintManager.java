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
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;

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
		else {
			this.docFlavor = DocFlavor.INPUT_STREAM.PNG;
		}
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		if (!silent) {
			PrintService printService[] = PrintServiceLookup.lookupPrintServices(this.docFlavor, pras);
			defaultService = ServiceUI.printDialog(null, 200, 200,printService, defaultService, this.docFlavor, pras);	
		}
		if (defaultService != null) {
			DocPrintJob job = defaultService.createPrintJob();
			DocAttributeSet das = new HashDocAttributeSet();
			das.add(OrientationRequested.PORTRAIT);
			Doc doc = new SimpleDoc(is, docFlavor, das);
			job.print(doc, pras);
			System.out.println("Finish print");
		}
	}
}
