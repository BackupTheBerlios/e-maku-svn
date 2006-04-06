/**
 * Esta clase esta basada en el archivo PDFViewer.java del proyecto JPedal.
 * Mayor informacion: 
 * 
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 * (C) Copyright 2006, by IDRsolutions and Contributors.
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * 
 * PdfViewer.java moficiado a partir de 23-mar-2006
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
 */

package common.pdf.pdfviewer;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ResourceBundle;

import common.control.ReportEvent;
import common.control.ReportListener;
import common.control.ValidHeadersClient;
import common.gui.formas.GenericForm;
import common.miscelanea.ZipHandler;
import common.miscelanea.idiom.Language;
import common.pdf.pdfviewer.gui.GUIFactory;
import common.pdf.pdfviewer.gui.SwingGUI;
import common.pdf.pdfviewer.utils.Printer;

import org.jdom.Element;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfFontException;

public class PDFViewer implements ReportListener {

	/** repository for general settings */
	protected Values commonValues = new Values();

	/** All printing functions and access methods to see if printing active */
	protected Printer currentPrinter = new Printer();

	/** PDF library and panel */
	final protected PdfDecoder decode_pdf = new PdfDecoder();
	
	/** general GUI functions */
	protected SwingGUI currentGUI = new SwingGUI(decode_pdf, commonValues);

	/** command functions */
	protected Commands currentCommands = new Commands(commonValues, currentGUI,decode_pdf, currentPrinter);
	/** scaling values which appear onscreen */
	protected String[] scalingValues;

	private Element data;
	private ZipHandler zip;
	private String idReport;

	
	/**
	 * Constructor del Visor
	 * @param GFforma
	 * @param idReport
	 */
	public PDFViewer(GenericForm GFforma, String idReport) {
		this.zip = new ZipHandler();
		this.idReport = idReport;
		init(null);
		currentGUI.init(scalingValues, currentCommands, currentPrinter);
		currentGUI.addCombo(
				Language.getWord("ESCALA"),
				Language.getWord("AUVISTA"), Commands.SCALING);
		createButtons();
		currentGUI.initStatus();
		ValidHeadersClient.addReportListener(this);
	}
	
	
	/**
	 * 
	 * @param reportTitle
	 * @param bytes
	 */
	private void openReport(String reportTitle, byte[] bytes) {

		// get any user set dpi
		String hiresFlag = System.getProperty("hires");
		if (hiresFlag != null)
			commonValues.setUseHiresImage(true);

		// get any user set dpi
		String memFlag = System.getProperty("memory");
		if (memFlag != null)
			commonValues.setUseHiresImage(false);

		commonValues.maxViewY = 0;// ensure reset for any viewport

		/**
		 * open any default file and selected page
		 */
		if (bytes != null) {

			commonValues.setPdfArray(bytes);
			commonValues.setFileSize(bytes.length);

			currentGUI.setViewerTitle(reportTitle);

			/** see if user set Page */
			String page = System.getProperty("Page");

			if (page != null) {

				int pageNum = -1;
				try {
					pageNum = Integer.parseInt(page);
					if (pageNum < 1) {
						pageNum = -1;
						System.err.println(page+ " must be 1 or larger. Opening on page 1");
					}
				} catch (Exception e) {
					System.err.println(page+ "is not a valid number for a page number. Opening on page 1");
				}
			}
			else {
				currentCommands.openFile();
			}

			bytes = null; // deselect
		}
	}

	
	/**
	 * 
	 * @param bundle
	 */
	protected void init(ResourceBundle bundle) {

		try {
			this.scalingValues = new String[]{
										Language.getWord("ESCANCHO"),
										Language.getWord("LARGO"),
										Language.getWord("ANCHO"),
										"25","50", "75", "100", "125", "150", "200", "250", "500", "750","1000" }; 
			decode_pdf.setDefaultDisplayFont("SansSerif");
		}
		catch (PdfFontException e) {
			
			System.out.println(e.getMessage());
			String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			System.out.println(Language.getWord("POSTSCRIPT"));
			System.out.println("=====================\n");
			int count = fontList.length;
			for (int i = 0; i < count; i++) {
				Font f = new Font(fontList[i], 1, 10);
				System.out.println(
						fontList[i]
						+ " ("
						+ Language.getWord("FUENTESDISP") + "=" + f.getPSName()
						+ ")");

			}
		}
	}

	/**
	 * Crea los botones de navegacion
	 */
	private void createButtons() {
		currentGUI.addButton(
				GUIFactory.BUTTONBAR,
				Language.getWord("IMPRIREP"),
				"/icons/ico_impresora.png",Commands.PRINT);
		currentGUI.addButton(
				GUIFactory.BUTTONBAR,
				Language.getWord("GUARDARCO"),
				"/icons/ico_guardar.png", Commands.SAVE);
		currentGUI.addButton(
				GUIFactory.BUTTONBAR,
				Language.getWord("CERRAR"),
				"/icons/ico_cancelar.png", Commands.EXIT);
	}
	
	/**
	 * Retorna una referencia de la Clase SwingGUI
	 * @return currentGUI
	 */
	public SwingGUI getGUI() {
		return currentGUI;
	}
	
	/**
	 * Metodo encargado de la recepcion de un reporte
	 * por medio del evento ReportEvent
	 */
	public void arriveReport(ReportEvent e) {
		if (e.getIdReport().equals(idReport)) {
			try {
				data = e.getData();
				byte [] bytesReport = zip.getDataDecode(data.getValue());
				openReport(e.getTitleReport(), bytesReport);
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				currentGUI.getFrame().setCursor(cursor);
			}
			catch (IOException IOEe) {
				IOEe.printStackTrace();
			}
		}
	}
}
