/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2007, IDRsolutions and Contributors.
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
 * DefaultTransferHandler.java
 * ---------------
 * (C) Copyright 2007, by IDRsolutions and Contributors.
 *

 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui;

import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.xml.parsers.*;

import org.jpedal.examples.simpleviewer.*;
import org.jpedal.examples.simpleviewer.gui.generic.*;
import org.jpedal.utils.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class DefaultTransferHandler extends TransferHandler {
	private Commands currentCommands;
	private SwingGUI currentGUI;
	private GUIThumbnailPanel thumbnails;
	private Values commonValues;

	public DefaultTransferHandler(Values commonValues, GUIThumbnailPanel thumbnails, SwingGUI currentGUI, Commands currentCommands) {
		this.commonValues = commonValues;
		this.thumbnails = thumbnails;
		this.currentGUI = currentGUI;
		this.currentCommands = currentCommands;
	}

	public boolean canImport(JComponent dest, DataFlavor[] flavors) {
		return true;
	}

	public boolean importData(JComponent src, Transferable transferable) {
		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		DataFlavor listFlavor = null;
		int lastFlavor = flavors.length - 1;
	
		// Check the flavors and see if we find one we like.
		// If we do, save it.
		for (int f = 0; f <= lastFlavor; f++) {
			if (flavors[f].isFlavorJavaFileListType()) {
				listFlavor = flavors[f];
			}
		}

		// Ok, now try to display the content of the drop.
		try {
			DataFlavor bestTextFlavor = DataFlavor.selectBestTextFlavor(flavors);
			if (bestTextFlavor != null) { // this could be a file from a web page being dragged in
				Reader r = bestTextFlavor.getReaderForText(transferable);
				
				/** acquire the text data from the reader. */
				String textData = readTextDate(r);

//                System.out.println(textData);

                /** need to remove all the 0 characters that will appear in the String when importing on Linux */
                textData = removeChar(textData, (char) 0);

                /** get the URL from the text data */
                String url = getURL(textData);

                if (url.indexOf("file:/") != url.lastIndexOf("file:/")) // make sure only one url is in the String
                    currentGUI.showMessageDialog("You may only import 1 file at a time");
                else
                    openFile(url);

            } else if (listFlavor != null) { // this is most likely a file being dragged in
				List list = (List) transferable .getTransferData(listFlavor);
                //System.out.println("list = " + list);
                if (list.size() == 1) { // we can process
					File file = (File) list.get(0);
					openFile(file.getAbsolutePath());
				} else {
					currentGUI.showMessageDialog("You may only import 1 file at a time");
				}
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	private void openFile(String file) {
		// get any user set dpi
		String hiresFlag = System.getProperty("org.jpedal.hires");
		if (hiresFlag != null)
			commonValues.setUseHiresImage(true);
	
		// get any user set dpi
		String memFlag = System.getProperty("org.jpedal.memory");
		if (memFlag != null)
			commonValues.setUseHiresImage(false);
	
		// reset flag
		if (thumbnails.isShownOnscreen())
			thumbnails.resetToDefault();
	
		commonValues.maxViewY = 0;// ensure reset for any viewport
	
		/**
		 * open any default file and selected page
		 */
		if (file != null){
	
			File testExists = new File(file);
			boolean isURL = false;
			if (file.startsWith("http:") || file.startsWith("file:")) {
				LogWriter.writeLog("Opening http connection");
				isURL = true;
			}
	
			if ((!isURL) && (!testExists.exists())) {
				currentGUI.showMessageDialog(file + '\n' + Messages.getMessage("PdfViewerdoesNotExist.message"));
			} else if ((!isURL) && (testExists.isDirectory())) {
				currentGUI.showMessageDialog(file + '\n' + Messages.getMessage("PdfViewerFileIsDirectory.message"));
			} else {
	
				boolean isValid = ((file.endsWith(".pdf"))
						|| (file.endsWith(".fdf")) || (file.endsWith(".tif"))
						|| (file.endsWith(".tiff")) || (file.endsWith(".png"))
						|| (file.endsWith(".jpg")) || (file.endsWith(".jpeg")));
	
				if (isValid) {
					commonValues.setSelectedFile(file);
					commonValues.setFileSize(testExists.length() >> 10);
	
					currentGUI.setViewerTitle(null);
	
					currentCommands.openFile(file);
				} else {
					currentGUI.showMessageDialog("You may only import a valid PDF or image");
				}
	
			}
		}
	}

    private String removeChar(String s, char c) {
        String r = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) r += s.charAt(i);
        }
        return r;
    }

    /**
	 * Returns the URL from the text data acquired from the transferable object.
	 * @param textData text data acquired from the transferable.
	 * @return the URL of the file to open
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private String getURL(String textData) throws ParserConfigurationException, SAXException, IOException {
        if (!textData.startsWith("http://") && !textData.startsWith("file://")) { // its not a url so it must be a file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(textData.getBytes()));

            Element a = (Element) doc.getElementsByTagName("a").item(0);
            textData = getHrefAttribute(a);
        }
		
		return textData;
	}

	/**
	 * Acquire text data from a reader. <br/><br/>
	 * Firefox this will be some html containing an "a" element with the "href" attribute linking to the to the PDF. <br/><br/>
	 * IE a simple one line String containing the URL will be returned
	 * @param r the reader to read from
	 * @return the text data from the reader
	 * @throws IOException
	 */
	private String readTextDate(Reader r) throws IOException {
		BufferedReader br = new BufferedReader(r);
		
		String textData = "";
		String line = br.readLine();
		while (line != null) {
			textData += line;
			line = br.readLine();
		}
		br.close();
		
		return textData;
	}

	/**
	 * Returns the URL held in the href attribute from an element
	 * @param element the element containing the href attribute
	 * @return the URL held in the href attribute
	 */
	private String getHrefAttribute(Element element) {
		NamedNodeMap attrs = element.getAttributes();
	
		Node nameNode = attrs.getNamedItem("href");
		if (nameNode != null) {
			return nameNode.getNodeValue();
		}
		
		return null;
	}
}