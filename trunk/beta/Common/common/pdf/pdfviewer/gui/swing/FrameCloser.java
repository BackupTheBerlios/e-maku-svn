/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2006, IDRsolutions and Contributors.
 *
 * 	This file is part of JPedal
 *
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * FrameCloser.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.swing;

import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import common.miscelanea.idiom.Language;
import common.pdf.pdfviewer.Commands;
import common.pdf.pdfviewer.Values;
import common.pdf.pdfviewer.gui.GUIFactory;
import common.pdf.pdfviewer.utils.Printer;

import org.jpedal.PdfDecoder;

/**cleanly shutdown if user closes window*/
public class FrameCloser implements InternalFrameListener {
	
	private Commands currentCommands;
	GUIFactory currentGUI;
	PdfDecoder decode_pdf;
	private Printer currentPrinter;
	Values commonValues;
	
	public FrameCloser(Commands currentCommands,GUIFactory currentGUI,PdfDecoder decode_pdf, Printer currentPrinter,Values commonValues) {
		this.currentCommands=currentCommands;
		this.currentGUI=currentGUI;
		this.decode_pdf=decode_pdf;
		this.currentPrinter=currentPrinter;
		this.commonValues=commonValues;
	}
	
	public void windowClosing(WindowEvent e) {}

	public void internalFrameOpened(InternalFrameEvent e) {}

	public void internalFrameClosing(InternalFrameEvent e) {
		
		currentCommands.flush();
		if(currentPrinter.isPrinting()) {
			currentGUI.showMessageDialog(Language.getWord("CURRENT_WINDOW_BUSY"));
		}
		else if(commonValues.isProcessing()){
			int confirm=JOptionPane.showInternalConfirmDialog(currentGUI.getFrame(),Language.getWord("CLOSE_CURRENT_WINDOW"),"",JOptionPane.YES_NO_OPTION);
			if(confirm==JOptionPane.YES_OPTION){
				decode_pdf.closePdfFile();
				currentGUI.getFrame().dispose();
			}
		} else {
			currentGUI.getFrame().dispose();
		}
		
	}

	public void internalFrameClosed(InternalFrameEvent e) {}

	public void internalFrameIconified(InternalFrameEvent e) {}

	public void internalFrameDeiconified(InternalFrameEvent e) {}

	public void internalFrameActivated(InternalFrameEvent e) {}

	public void internalFrameDeactivated(InternalFrameEvent e) {}
}
