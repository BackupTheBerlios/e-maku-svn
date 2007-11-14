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
 * PropertiesFile.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.utils;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.jpedal.examples.simpleviewer.*;
import org.jpedal.utils.*;
import org.w3c.dom.*;
                                      
/**holds values stored in XML file on disk*/
public class PropertiesFile {
	
	private String separator=System.getProperty( "file.separator" );
	private String userDir=System.getProperty("user.dir");
	
	private String configFile=userDir+separator+".properties.xml";
	
	private Document doc;
	
	private int noOfRecentDocs = 6;
	
	public PropertiesFile(){
		
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			if(new File(configFile).exists()){
				try{
                    //TODO @mark
					doc =  db.parse(configFile);
				}catch(Exception e){
					doc =  db.newDocument();
				}
			}else
				doc =  db.newDocument();

			checkAllElementsPresent();

			writeDoc();
			
		}catch(Exception e){
			LogWriter.writeLog("Exception " + e + " generating properties file");
		}
	}
	
	public String[] getRecentDocuments(){
		String[] recentDocuments=new String[noOfRecentDocs];
		
		try{
			NodeList nl =doc.getElementsByTagName("recentfiles");
			List fileNames = new ArrayList();
			
			if(nl != null && nl.getLength() > 0) {
				NodeList allRecentDocs = ((Element) nl.item(0)).getElementsByTagName("*");
				
				for(int i=0;i<allRecentDocs.getLength();i++){
					Node item = allRecentDocs.item(i);
					NamedNodeMap attrs = item.getAttributes();
					fileNames.add(attrs.getNamedItem("name").getNodeValue());
				}
			}
			
			//prune unwanted entries
			while(fileNames.size() > noOfRecentDocs){
				fileNames.remove(0);
			}
			
			Collections.reverse(fileNames);
			
			recentDocuments = (String[]) fileNames.toArray(new String[noOfRecentDocs]);
		}catch(Exception e){
			LogWriter.writeLog("Exception " + e + " getting recent documents");
        	return null;
		}
		
		return recentDocuments;
	}
	
	public void addRecentDocument(String file){
		try{
			Element recentElement = (Element) doc.getElementsByTagName("recentfiles").item(0);
			
			checkExists(file, recentElement);
			
			Element elementToAdd=doc.createElement("file");
			elementToAdd.setAttribute("name",file);
			
			recentElement.appendChild(elementToAdd);
			
			removeOldFiles(recentElement);
			
			writeDoc();
		}catch(Exception e){
			LogWriter.writeLog("Exception " + e + " adding recent document to properties file");
		}
	}
	
	public void setValue(String elementName, String newValue) {
		try {
			NodeList nl =doc.getElementsByTagName(elementName);
			Element element=(Element) nl.item(0);
			element.setAttribute("value",newValue);
			
			writeDoc();
		}catch(Exception e){
			LogWriter.writeLog("Exception " + e + " setting value in properties file");
		}
	}
	
	public String getValue(String elementName){
		NamedNodeMap attrs = null;
		try {
			NodeList nl =doc.getElementsByTagName(elementName);
			Element element=(Element) nl.item(0);
			if(element==null)
				return "";
			attrs = element.getAttributes();
			
		}catch(Exception e){
			LogWriter.writeLog("Exception " + e + " generating properties file");
			return "";
		}

		return attrs.getNamedItem("value").getNodeValue();
	}
	
	private void removeOldFiles(Element recentElement) throws Exception{
		NodeList allRecentDocs = recentElement.getElementsByTagName("*");
		
		while(allRecentDocs.getLength() > noOfRecentDocs){
			recentElement.removeChild(allRecentDocs.item(0));
		}	
	}
	
	private void checkExists(String file, Element recentElement) throws Exception{
		NodeList allRecentDocs = recentElement.getElementsByTagName("*");
		
		for(int i=0;i<allRecentDocs.getLength();i++){
			Node item = allRecentDocs.item(i);
			NamedNodeMap attrs = item.getAttributes();
			String value = attrs.getNamedItem("name").getNodeValue();
			
			if(value.equals(file))
				recentElement.removeChild(item);
		}
	}

    //
    private void writeDoc() throws Exception{
		
		InputStream stylesheet = this.getClass().getResourceAsStream("/org/jpedal/examples/simpleviewer/res/xmlstyle.xslt");
	
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(stylesheet));
		transformer.transform(new DOMSource(doc), new StreamResult(configFile));

	}
	
	private void checkAllElementsPresent() throws Exception{
		NodeList allElements = doc.getElementsByTagName("*");
		List elementsInTree=new ArrayList(allElements.getLength());
		
		for(int i=0;i<allElements.getLength();i++)
			elementsInTree.add(allElements.item(i).getNodeName());
		
		Element propertiesElement = null;
		
		if(elementsInTree.contains("properties")){
			propertiesElement = (Element) doc.getElementsByTagName("properties").item(0);
		}else{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			doc =  db.newDocument();	
			
			propertiesElement = doc.createElement("properties");
			doc.appendChild(propertiesElement);
			
			allElements = doc.getElementsByTagName("*");
			elementsInTree=new ArrayList(allElements.getLength());
			
			for(int i=0;i<allElements.getLength();i++)
				elementsInTree.add(allElements.item(i).getNodeName());
		}
		
		if(!elementsInTree.contains("recentfiles")){
			Element recent = doc.createElement("recentfiles");
			propertiesElement.appendChild(recent);
		}
		
		if(!elementsInTree.contains("showfirsttimepopup")){
			Element showFormsSaveMessage = doc.createElement("showfirsttimepopup");
			showFormsSaveMessage.setAttribute("value","true");
			propertiesElement.appendChild(showFormsSaveMessage);
		}
		
		if(!elementsInTree.contains("showsaveformsmessage")){
			Element showFormsSaveMessage = doc.createElement("showsaveformsmessage");
			showFormsSaveMessage.setAttribute("value","true");
			propertiesElement.appendChild(showFormsSaveMessage);
		}
		
		if(!elementsInTree.contains("showitextmessage")){
			Element showItextMessage = doc.createElement("showitextmessage");
			showItextMessage.setAttribute("value","true");
			propertiesElement.appendChild(showItextMessage);
		}
		
		if(!elementsInTree.contains("showddmessage")){
			Element showDDMessage = doc.createElement("showddmessage");
			showDDMessage.setAttribute("value","true");
			propertiesElement.appendChild(showDDMessage);
		}
		
		if(!elementsInTree.contains("searchWindowType")){
			Element showDDMessage = doc.createElement("searchWindowType");
			showDDMessage.setAttribute("value","0");
			propertiesElement.appendChild(showDDMessage);
		}
		
		if(!elementsInTree.contains("borderType")){
			Element showDDMessage = doc.createElement("borderType");
			showDDMessage.setAttribute("value","1");
			propertiesElement.appendChild(showDDMessage);
		}
		
		if(!elementsInTree.contains("DPI")){
			Element showDDMessage = doc.createElement("DPI");
			showDDMessage.setAttribute("value","96");
			propertiesElement.appendChild(showDDMessage);
		}
		
		if(!elementsInTree.contains("autoScroll")){
			Element showDDMessage = doc.createElement("autoScroll");
			showDDMessage.setAttribute("value","true");
			propertiesElement.appendChild(showDDMessage);
		}
		
		if(!elementsInTree.contains("pageMode")){
			Element showDDMessage = doc.createElement("pageMode");
			showDDMessage.setAttribute("value",String.valueOf(Commands.SINGLE));
			propertiesElement.appendChild(showDDMessage);
		}
		
	}
	
	public int getNoRecentDocumentsToDisplay() {
		return this.noOfRecentDocs;
	}
}
