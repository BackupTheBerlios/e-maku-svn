package common.printer.plainViewer;
//import java.io.FileInputStream;
import java.io.IOException;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import common.printer.plainViewer.TextReportUtils;
import common.printer.plainViewer.PrinterViewsArray;

/*
 *  This class generates a Text File Report from a CSV file
 */ 

public class TextReportGenerator {
	
  int charactersPerLine = 80;
  int columnsNum = 0;
  int pagesNum = 0;
  Vector <String>fieldsTypes = new Vector<String>();
  ByteArrayOutputStream[] reportViews;
  
  String header     = "";
  String htmlHeader = "";
  String blankLine  = "";
  String title      = "";
  String nit        = "";
  String today      = "";
  String bar        = "";
  Vector <String>columnsNames = new Vector<String>();
  int[] maxlong;
  String columnsTitle     = "";
  String htmlColumnsTitle = "";
  String reportTitle      = "";
  String company          = "";
  int totalPages          = 0;
  TextReportUtils tools = new TextReportUtils(charactersPerLine);
  PrinterViewsArray printerViews;
  String input = "";

  public TextReportGenerator(int charactersPerLine, byte[] bytes){
	  this.charactersPerLine = charactersPerLine;
	  System.out.println("*** Instanciando TextReportGenerator...");
	  input = new String(bytes);
	  scanFirstPage();
	  processCsv();
  }
  
  
  // Captures the report header and find the max lenght of columns
  public void scanFirstPage() {
	   
	  StringTokenizer tokens = new StringTokenizer(input,"\n");
   	  String line = "";
   	  String data[];
   	  bar = tools.getBar() + "\n";
   	
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
           		data = tools.getLineVars(line,2);
           		company = data[1];
           	
           		// Processing Nit
           		line = tokens.nextToken();
           		data = tools.getLineVars(line,1);
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
           		data = tools.getLineVars(line,1);
           		reportTitle = data[0];
       	    
           		header += tools.getCenteredString(data[0]);
           		htmlHeader += "<center><b>" + reportTitle + "</b></center>";
       	    
           		// Processing Columns
           		line = tokens.nextToken();
           		
           		columnsNames = tools.getColumnNames(line);
           		columnsNum = columnsNames.size();
       	    
           		for(int i=0;i<columnsNum;i++){
           			String columnName = (String) columnsNames.get(i);
           			maxColumnsLength[i] = columnName.length();  
           		}	       	           	    
           	}
           	else {
           			// Process the footer page message
           			if (line.contains("eMaku")) {
           				data = tools.getLineVars(line,3);
          				totalPages = Integer.valueOf(data[2].trim());
           				
           				break;
           			} 
           			else {
           					// Process the report records
           					data = tools.getLineVars(line,columnsNum);
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
	        String value = tools.getLeftAlignedString((String) columnsNames.get(i),maxColumnsLength[i]);
	        String htmlValue = tools.getLeftAlignedHTMLString((String) columnsNames.get(i),maxColumnsLength[i]);
	        columnsTitle += value + "   ";
	        htmlColumnsTitle += "<td><b>" + htmlValue + "</b></td><td> </td>";
	        blankLine +=  "<td> </td><td> </td>";
	     }
	    blankLine += "</tr>";
  }
  
  // Process the csv input file
  public void processCsv() {
	  
   try {    
	   reportViews  = new ByteArrayOutputStream[totalPages];
	   printerViews = new PrinterViewsArray(totalPages);
	          
       String line  = "";
	   String[] data;
	   StringTokenizer tokens = new StringTokenizer(input,"\n");
	   line = tokens.nextToken();
	   
	   int lineNumber = 0;
	   pagesNum = 0;
	   
       while (tokens.hasMoreTokens()) { 
  
   	       lineNumber++;
   	       line = tokens.nextToken();
    	   
    	   if (lineNumber == 1) {       	                 
    		   // Processing Header
    		   
    		   for(int i=0;i<4;i++) {
    			   line = tokens.nextToken();
    		   }
       
    		   String pageLabel = "Pagina ";
    		   if (pagesNum < 10) {
    			   pageLabel += " ";
    		   }	 
          
    		   pageLabel += pagesNum + 1;
        
    		   int length = charactersPerLine - (company.length() + pageLabel.length());
    		   String space = "";
    		   for (int i=0;i<length;i++) {
    			   space += " ";
    		   }
        
    		   reportViews[pagesNum] = new ByteArrayOutputStream();
    		   printerViews.initPrinterPage(pagesNum);
        
    		   // Printing header
    		   // For the viewer
    		   // Note: #808080
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
    	   		else {
    	   				// Process the footer page message
    	   				if (line.contains("eMaku")) {
    	   					
    	   					printStringToReportViewer("</table>");
        		  
    	   					/* data = tools.getLineVars(line,2);
    	   					data[1] = data[1].substring(1,data[1].length());*/ 
    	   					String pageFooter = tools.getRightAlignedString("Pagina " + (pagesNum + 1) + " de " + totalPages, charactersPerLine);
                  
    	   					printerViews.addStringToPrinterView(pageFooter + "\n\n");
    	   					printerViews.startNewPage();
        		  
    	   					printStringToReportViewer("<p align=\"right\">" + pageFooter 
    	   							                  + "&nbsp;&nbsp;&nbsp;<br></p></td></tr></table></center><br><br>");
        		  
    	   					// Closing a report page of the array
    	   					reportViews[pagesNum].close();
    	   					printerViews.closePrinterPage();  
    	   					pagesNum++;
    	   					lineNumber=0;
    	   				} 
    	   				else {
    	   						// Process the report records
    	   						data = tools.getLineVars(line,columnsNum);
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
    	   								value = tools.getLeftAlignedString(data[i],maxlength);
    	   								htmlValue = tools.getLeftAlignedHTMLString(data[i],maxlength);
    	   							}
    	   							if (type.equals("java.lang.Integer") || type.equals("java.lang.Float")) {
    	   								// align to right relative
    	   								value = tools.getRightAlignedString(data[i],maxlength);
    	   								htmlValue = tools.getRightAlignedHTMLString(data[i],maxlength);
    	   							}
    	   							printStringToReportViewer("<td>");
    	   							printStringToReportViewer(htmlValue + "&nbsp;&nbsp;&nbsp;");
    	   							printStringToReportViewer("</td><td>|</td>");
    	   							printerViews.addStringToPrinterView(value + " | ");
               	       
    	   						}
    	   						printStringToReportViewer("</tr>");
    	   						printStringToReportViewer("<br>\n");
    	   						printerViews.addStringToPrinterView("\n");
    	   				}
    	   		}
       }
   }
   catch(IOException ex){
	 System.out.println("Error: No se pudo leer el archivo");   
   }
   
  }  
   
  // Write a String to report viewer
  
  public void printStringToReportViewer(String textString) {
		  try {
			   	final StringBuffer buffer = new StringBuffer("");
			    buffer.append(textString);
			    final byte[] text = buffer.toString().getBytes();
			    reportViews[pagesNum].write(text, 0, text.length);
	        }
	      catch (Exception e) {
	    		//System.out.println("ERROR: No se pudo escribir la linea en el arreglo[" + pagesNum + "]: " + textString);
	    		e.printStackTrace();
	    	} 	
	}
    
  // Returns the Report Pages as an ByteArrayOutputStream
 
  public ByteArrayOutputStream[] getReportViews() {
	  return reportViews;
  }
  
  public ByteArrayOutputStream[] getPrinterViews() {
	  return printerViews.getReportFile();
  }
 
  // Gets current date and returns it as string var
  public String getDate() {
	  String newDate = "Fecha: ";
	  Date now = new Date();
	  SimpleDateFormat textDate = new SimpleDateFormat("dd/MM/yyyy");
	  newDate += textDate.format(now) ;
	  
	  return newDate;
  }
  
}  //Class end
