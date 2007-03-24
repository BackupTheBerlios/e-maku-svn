package common.printer.plainViewer;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import common.printer.plainViewer.PrinterViewsArray;

/**
 * TextReportGenerator.java Creado el 1 de Febrero 2007
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
 * @author <A href='mailto:xtingray@qhatu.net'>Gustavo González</A>
 */

/*
 *  This class generates a Text File Report from a CSV file
 */ 

public class TextReportGenerator {
	
  private int charactersPerLine = 80;
  private int columnsNum = 0;
  private int pagesNum = 0;
  private Vector <String>fieldsTypes = new Vector<String>();
  private Vector <ByteArrayOutputStream>reportViews = new Vector<ByteArrayOutputStream>();
  private ByteArrayOutputStream byteArray;
  
  private String header     = "";
  private String htmlHeader = "";
  private String blankLine  = "";
  private String nit        = "";
  private String today      = "";
  private String bar        = "";
  private Vector <String>columnsNames = new Vector<String>();
  private int[] maxlong;
  private String columnsTitle     = "";
  private String htmlColumnsTitle = "";
  private String reportTitle      = "";
  private String company          = "";
  private int totalPages          = 0;
  private PrinterViewsArray printerViews;
  private String input = "";
  private int linesPerPage = 52;

  // Class Constructor
  public TextReportGenerator(int charactersPerLine, byte[] bytes){
	  this.charactersPerLine = charactersPerLine;
	  input = new String(bytes);
	  scanFirstPage();
	  processCsv();
  }
  
  
  // Captures the report header and find the max lenght of columns
  public void scanFirstPage() {
	   
	  StringTokenizer tokens = new StringTokenizer(input,"\n");
   	  String line = "";
   	  String data[];
   	  bar = TextReportUtils.getBar(charactersPerLine) + "\n";
   	
   	  // Capturing columns data types 
   	  line = tokens.nextToken();
   	  StringTokenizer varTypes = new StringTokenizer(line,"\t");
      columnsNum = varTypes.countTokens();
      
      while (varTypes.hasMoreTokens()) {
    	  String type = varTypes.nextToken();
    	  fieldsTypes.add(type); 
      }
      
	  int[] maxColumnsLength = new int[columnsNum];
	  maxlong = new int[columnsNum];
	  
      int lineNumber = 0;
      while (tokens.hasMoreTokens()) { 
           
    	    lineNumber++;
    	    line = tokens.nextToken();
    	    
    	    if (lineNumber == 1) {
           		// Processing company name
           		data = TextReportUtils.getLineVars(line,2);
           		company = data[1];
           	
           		// Processing Nit
           		line = tokens.nextToken();
           		data = TextReportUtils.getLineVars(line,1);
           		header += data[0] + "\n";
           		nit = data[0];
           	
           		// Processing Date
           		line = tokens.nextToken();
           		String date = getDate();
           		today = date;
           		header += date + "\n";
       	    
           		header += bar;
           		htmlHeader += bar + "<br>";
       	    
           		// Processing Title
           		line = tokens.nextToken();
           		data = TextReportUtils.getLineVars(line,1);
           		reportTitle = data[0];
       	    
           		header += TextReportUtils.getCenteredString(data[0],charactersPerLine);
           		htmlHeader += "<center><b>" + reportTitle + "</b></center>";
       	    
           		// Processing Columns
           		line = tokens.nextToken();
           		
           		columnsNames = TextReportUtils.getColumnNames(line);
           		columnsNum = columnsNames.size();
       	    
           		for(int i=0;i<columnsNum;i++){
           			String columnName = (String) columnsNames.get(i);
           			maxColumnsLength[i] = columnName.length();  
           		}	       	           	    
           	}
           	else {
           			// Process the footer page message
           			if (line.contains("eMaku")) {
           				data = TextReportUtils.getLineVars(line,3);
          				totalPages = Integer.valueOf(data[2].trim());
           				
           				break;
           			} 
           			else {
           					// Process the report records
           					data = TextReportUtils.getLineVars(line,columnsNum);
           					for(int i=0;i<columnsNum;i++) {  	  
           						// processing length of fields to find max values
           						if (data[i].length()>maxlong[i]) {
           							maxlong[i] = data[i].length(); 
           						}           	            	  
           					}
           			}
           	}
        } // While end
       
  	    for(int i=0;i<columnsNum;i++){
   	    	if (maxColumnsLength[i] > maxlong[i]) {
   	    		maxlong[i] = maxColumnsLength[i];
   	    	}
   	    }	       
 
  	    blankLine = "<tr>";
	    for(int i=0;i<columnsNum;i++) {
	        String value = TextReportUtils.getLeftAlignedString((String) columnsNames.get(i),maxColumnsLength[i]);
	        String htmlValue = (String) columnsNames.get(i);
	        columnsTitle += value + "   ";
	        htmlColumnsTitle += "<td><b>" + htmlValue + "</b></td><td> </td>";
	        blankLine +=  "<td> </td><td> </td>";
	     }
	    blankLine += "</tr>";
  }
  
  // Process the csv input file
  private void processCsv() {
	  
   try {    
	   printerViews = new PrinterViewsArray();	             
	   String line  = "";
	   String[] data;
	   StringTokenizer tokens = new StringTokenizer(input,"\n");
	   line = tokens.nextToken();

	   int lineNumber = 0;
	   int currentInputPage=0;
	   pagesNum = 0;

	   int currentLines=0;
	   printPageHeader();

	   while (tokens.hasMoreTokens()) { 

		   lineNumber++;
		   line = tokens.nextToken();

		   if (lineNumber == 1) {       	                 
			   // Processing Header
			   for(int i=0;i<4;i++) {
				   line = tokens.nextToken();
			   }
			   lineNumber = 4;
			   currentInputPage++;

		   } else {
			   // Process the footer page message
			   if (line.contains("eMaku")) {
				   
				   lineNumber=0;
				   
				   if (currentInputPage == totalPages) {
					   
					   if (currentLines < linesPerPage) {
						   int blankSpaces = linesPerPage - currentLines;
						   for(int i=0;i<blankSpaces;i++) {
							   	printStringToReportViewer("<tr>");
							   	printerViews.addStringToPrinterView("\n");
							   	for(int j=0;j<columnsNum;j++) {
							   		printStringToReportViewer("<td>");
							   		printStringToReportViewer("&nbsp;");
							   		printStringToReportViewer("</td><td>&nbsp;</td>");
							   	}
							   	printStringToReportViewer("</tr>\n");
						   }
					   }
					   printStringToReportViewer("</table>");
					   byteArray.close();
					   reportViews.add(pagesNum,byteArray);
					   printerViews.closePrinterPage();
					   break;
				   }				   				   
			   } 
			   else {
				   	// Process the report records
				   	currentLines++;

				   	data = TextReportUtils.getLineVars(line,columnsNum);
				   	printStringToReportViewer("<tr>");

				   	for(int i=0;i<columnsNum;i++) {
				   		String value = "";
				   		String htmlValue = "";
				   		String type = fieldsTypes.get(i);
				   		int maxlength = maxlong[i];
				   		if(data[i] == null) {
				   			data[i] = "";
				   		}

				   		if (type.equals("java.lang.String")) {
				   			// align to left relative
				   			if (data[i].length()>maxlength) {
				   				data[i] = data[i].substring(0, maxlength);
				   			}
				   			value = TextReportUtils.getLeftAlignedString(data[i],maxlength);
				   			htmlValue = TextReportUtils.getLeftAlignedHTMLString(data[i],maxlength);
				   		}
				   		if (type.equals("java.lang.Integer") || type.equals("java.lang.Float")) {
				   			// align to right relative
				   			value = TextReportUtils.getRightAlignedString(data[i],maxlength);
				   			htmlValue = TextReportUtils.getRightAlignedHTMLString(data[i],maxlength);
				   		}
				   		printStringToReportViewer("<td>");
				   		printStringToReportViewer(htmlValue + "&nbsp;&nbsp;&nbsp;");
				   		printStringToReportViewer("</td><td>|</td>");
				   		printerViews.addStringToPrinterView(value + " | ");
				   	}
				   	printStringToReportViewer("</tr>\n");
				   	printerViews.addStringToPrinterView("\n");

				   	if (currentLines == linesPerPage) {
				   		// Closing a report page of the array
				   		printStringToReportViewer("</table>");
				   		currentLines = 0;
				   		byteArray.close();
				   		reportViews.add(pagesNum,byteArray);
				   		printerViews.closePrinterPage();  
				   		pagesNum++;

				   		printPageHeader();
				   }
			   }
		   }

	   }
   }
   catch(IOException ex){
	 System.out.println("Error: No se pudo leer el archivo");   
   }
   
  }  
  
  // Write a header report page
  
  private void printPageHeader() {
	  
		   String pageLabel = "Página ";
   		   if (pagesNum < 10) {
   			   pageLabel += " ";
   		   }	 
         
   		   pageLabel += pagesNum + 1;
       
   		   int length = charactersPerLine - (company.length() + pageLabel.length());
   		   String space = "";
   		   for (int i=0;i<length;i++) {
   			   space += " ";
   		   }
       
   		   byteArray = new ByteArrayOutputStream();
   		   printerViews.initPrinterPage(pagesNum);
       
   		   // Printing header for the viewer
   		   printStringToReportViewer("<body bgcolor=\"#FFFFFF\"><center><table border=\"0\" bgcolor=\"#FFFFFF\" width=\"900\"><tr></td>" 
       		                   + "<br>" + bar + "<br><table border=\"0\" width=\"882\"><tr><td>" + company + "</td><td align=\"right\">" 
       		                   + pageLabel + "</td></tr><tr><td>" + nit + "</td><td> </td></tr><tr><td>" + today 
       		                   + "</td><td> </td></tr></table>\n" + htmlHeader  
       		                   + bar + "<table border=0><tr>" + htmlColumnsTitle + "</tr>" + blankLine);

   		   // For the printer
   		   printerViews.addStringToPrinterView(bar + company + space + pageLabel + "\n");
   		   printerViews.addStringToPrinterView(header);
   		   printerViews.activeBold();        
   		   printerViews.addStringToPrinterView(reportTitle);
   		   printerViews.disableBold();
   		   printerViews.addStringToPrinterView("\n" + bar + "\n");
   		   printerViews.activeBold();
   		   printerViews.addStringToPrinterView(columnsTitle);
   		   printerViews.disableBold();
   		   printerViews.addStringToPrinterView("\n\n");    	   							  
  }
   
  // Write a String to report viewer
  
  private void printStringToReportViewer(String textString) {
		  try {
			   	final StringBuffer buffer = new StringBuffer("");
			    buffer.append(textString);
			    final byte[] text = buffer.toString().getBytes();
			    byteArray.write(text, 0, text.length);
	        }
	      catch (Exception e) {
	    		e.printStackTrace();
	    	} 	
	}
    
  // Returns the Report Pages as an ByteArrayOutputStream
  public Vector<ByteArrayOutputStream> getReportViews() {
	  return reportViews;
  }
  
 
  // Returns the Rerport Pages to printer output
  public Vector<ByteArrayOutputStream> getPrinterViews() {  
	  return printerViews.getReportFile();
  }
 
  // Gets current date and returns it as string var
  private String getDate() {
	  String newDate = "Fecha: ";
	  Date now = new Date();
	  SimpleDateFormat textDate = new SimpleDateFormat("dd/MM/yyyy");
	  newDate += textDate.format(now) ;
	  
	  return newDate;
  }
  
}  //Class end
