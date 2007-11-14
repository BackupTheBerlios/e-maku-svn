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
 * GUIThumbnailPanel.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.generic;

import java.awt.*;

import org.jpedal.objects.*;
import org.jpedal.render.*;
import org.jpedal.utils.repositories.*;

/**generic version to show thumbnails in panel on side*/
public interface GUIThumbnailPanel {

	boolean isShownOnscreen();

	void terminateDrawing();

	void setIsDisplayedOnscreen(boolean b);

	Object[] getButtons();

	void addComponentListener();

	void addDisplayedPageAsThumbnail(int currentPage,DynamicVectorRenderer currentDisplay);

	void generateOtherVisibleThumbnails(int currentPage);

	void setupThumbnails(int pages, Font textFont, String message, PdfPageData pdfPageData);

	void removeAll();

	void setupThumbnails(int i, int[] js, int pageCount);

	void generateOtherThumbnails(String[] strings, Vector_Object thumbnailsStored);

	void resetHighlightedThumbnail(int id);

	void resetToDefault();

	void removeAllListeners();

	void setThumbnailsEnabled();
	
	void setThumbnailsEnabled(boolean value);

	void refreshDisplay();


}
