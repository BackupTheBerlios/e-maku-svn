package common.printer;

import java.io.*;
import java.text.*;
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
	private static int count=0;
	private String jobName;
	private DocAttributeSet daset = new HashDocAttributeSet();
	public PrintingManager (
			ImpresionType type,
			ByteArrayInputStream is,
			boolean silent,
			int copies,
			String printer, String orientation) throws FileNotFoundException, PrintException {
		
		this.type = type;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		jobName = "empj"+sdf.format(date)+(++count);
		pras.add(new JobName(jobName,Locale.getDefault()));
		if (orientation!=null) {
			if ("LANDSCAPE".equals(orientation)) {
				pras.add(OrientationRequested.LANDSCAPE);
				daset.add(OrientationRequested.LANDSCAPE);
			}
		}
		
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
			docFlavor = DocFlavor.INPUT_STREAM.PDF;
		}
		PrintService ps = selectPrinservice(printer);
		if (silent && ps!=null) {
			pras.add(new Copies(copies));
			print(ps,is,pras);
		}
		else {
			try {
				defaultService = ServiceUI.printDialog(null, 200, 200,jps, ps, docFlavor, pras);
				if (defaultService!=null) {
					CommonConstants.printSelect = defaultService;
					print(defaultService,is,pras);
				}
			} catch (NullPointerException NPEe) {
				//NPEe.printStackTrace();
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		is = null;
	}
	
	private void print(PrintService ps, Object printData, PrintRequestAttributeSet pras) throws PrintException {
		DocPrintJob job = ps.createPrintJob();
		Doc doc = new SimpleDoc(printData, docFlavor, daset);
		job.print(doc, pras);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("::::::");
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