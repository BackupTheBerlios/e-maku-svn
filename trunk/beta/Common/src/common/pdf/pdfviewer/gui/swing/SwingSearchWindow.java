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
 * SwingSearchWindow.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.pdf.pdfviewer.Values;
import common.pdf.pdfviewer.gui.SwingGUI;
import common.pdf.pdfviewer.gui.generic.GUISearchWindow;
import common.pdf.pdfviewer.utils.SwingWorker;

import org.jpedal.PdfDecoder;

/**provides interactive search Window and search capabilities*/
public class SwingSearchWindow extends JFrame implements GUISearchWindow{
	
	private static final long serialVersionUID = 7423353291901251301L;
	
	/**flag to stop multiple listeners*/
//	private boolean isSetup=false;
	
	final private String defaultMessage="Enter your text here";
	final JTextField searchText=new JTextField(defaultMessage);
	JCheckBox searchAll;
	JTextField searchCount;
	DefaultListModel listModel;
	SearchList results;
	
	/**swing thread to search in background*/
	SwingWorker searcher=null;
	
	/**flag to show searching taking place*/
	public boolean isSearch=false;
	
	JButton searchButton=new JButton("search");
	
	/**number fo search items*/
//	private int itemFoundCount=0;
	
	/**used when fiding text to highlight on page*/
	Map textPages=new HashMap();
	Map textRectangles=new HashMap();
	
	final JPanel nav=new JPanel();
	
	Values commonValues;
	SwingGUI currentGUI;
	PdfDecoder decode_pdf;

	/**deletes message when user starts typing*/
//	private boolean deleteOnClick;
	
	public SwingSearchWindow(Values commonValues,SwingGUI currentGUI,PdfDecoder decode_pdf) {
		
		this.commonValues=commonValues;
		this.currentGUI=currentGUI;
		this.decode_pdf=decode_pdf;
	}
	
}
