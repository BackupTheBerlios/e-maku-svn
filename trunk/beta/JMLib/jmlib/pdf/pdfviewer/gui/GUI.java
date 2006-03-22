
/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2005, IDRsolutions and Contributors.
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
 * GUI.java
 * ---------------
 * (C) Copyright 2006, by IDRsolutions and Contributors.
 *

 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package jmlib.pdf.pdfviewer.gui;


import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import jmlib.pdf.pdfviewer.Values;
import jmlib.pdf.pdfviewer.gui.generic.GUIButton;
import jmlib.pdf.pdfviewer.gui.generic.GUICombo;
import jmlib.pdf.pdfviewer.gui.generic.GUIOutline;
import jmlib.pdf.pdfviewer.gui.generic.GUIThumbnailPanel;
//import jmlib.pdf.pdfviewer.utils.PropertiesFile;

import org.jpedal.PdfDecoder;

/**any shared GUI code - generic and AWT*/
public class GUI {
	
	/**nav buttons - global so accessible to ContentExtractor*/
	public GUIButton first,fback,back,forward,fforward,end;
	
	/**handle for internal use*/
	protected PdfDecoder decode_pdf;
	
	/** location for divider with thumbnails turned on */
	protected final int thumbLocation=200;
	
	/** minimum screen width to ensure menu buttons are visible */
	protected final int minimumScreenWidth=700;
	
	//<start-forms>
	/**track pages decoded once already*/
	//protected HashMap pagesDecoded=new HashMap();
	protected Map<Integer, String> pagesDecoded = Collections.synchronizedMap(new HashMap<Integer, String>());

	//<end-forms>
	
	/**allows user to toggle on/off text/image snapshot*/
	protected  GUIButton snapshotButton;
	
	/**cursorBox to draw onscreen*/
	private Rectangle currentRectangle =null;
	
	
	public int cropX;

	public int cropW;

	public int cropH;

	/**crop offset if present*/
	protected int mediaX,mediaY;

	public int mediaW;

	public int cropY;

	public int mediaH;
	
	/**show if outlines drawn*/
	protected boolean hasOutlinesDrawn=false;
	
	/**XML structure of bookmarks*/
	protected GUIOutline tree=null;
	
	/**stops autoscrolling at screen edge*/
	private boolean allowScrolling=true;
	
	/** location for the divider when bookmarks are displayed */
	protected int divLocation=170;
	
	/**flag to switch bookmarks on or off*/
	protected boolean showOutlines=true;
	
	/**scaling values as floats to save conversion*/
	protected float[] scalingFloatValues={1.0f,1.0f,1.0f,.25f,.5f,.75f,1.0f,1.25f,1.5f,2.0f,2.5f,5.0f,7.5f,10.0f};
	
	/**page scaling to use 1=100%*/
	protected float scaling = 1;
	
	/** padding so that the pdf is not right at the edge */
	protected final int inset=25;
	
	/**store page rotation*/
	protected int rotation=0;
	
	/**scaling values as floats to save conversion*/
	protected final String[] rotationValues={"0","90","180","270"};
	
	/**scaling factors on the page*/
	protected GUICombo rotationBox;
	
	/**list of image quality values so user can choose */
	protected final String[] qualityValues={"Memory","Quality"};
	
	/**allows user to set quality of images*/
	protected GUICombo qualityBox;
	
	/**scaling factors on the page*/
	protected GUICombo scalingBox;
	
	/**default scaling on the combobox scalingValues*/
	protected final int defaultSelection=0;
	
	/**allows user to toggle on/off bookmarks*/
	protected GUIButton bookmarksButton;
	
	/**title message on top if you want to over-ride JPedal default*/
	protected String titleMessage=null;
	
	protected Values commonValues;
	
	protected GUIThumbnailPanel thumbnails;
	
	//protected PropertiesFile properties;
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#allowScrolling()
	 */
	public boolean allowScrolling() {
		return allowScrolling;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setNoPagesDecoded()
	 */
	public void setNoPagesDecoded() {
		//<start-forms>
		pagesDecoded.clear();
		//<end-forms>		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setScalingToDefault()
	 */
	public void setScalingToDefault(){
		scaling = (float) 1.0;
		scalingBox.setSelectedIndex(2); 
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setRectangle(java.awt.Rectangle)
	 */
	public void setRectangle(Rectangle newRect) {
		currentRectangle=newRect;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getRectangle()
	 */
	public Rectangle getRectangle() {
		return currentRectangle;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#toogleAutoScrolling()
	 */
	public void toogleAutoScrolling() {
		allowScrolling=!allowScrolling;
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getRotation()
	 */
	public int getRotation() {
		return rotation;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getScaling()
	 */
	public float getScaling() {
		return scaling;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getPDFDisplayInset()
	 */
	public int getPDFDisplayInset() {
		return inset;
	}

}
