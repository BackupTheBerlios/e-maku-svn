package common.printer.plainViewer;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * TextReportUtils.java Creado el 1 de Febrero 2007
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


public class TextReportUtils {
		
	// Captures variables from a String
	public static String[] getLineVars(String line, int items) {
	    String[] array = new String[items];	   
	    StringTokenizer tokens = new StringTokenizer(line,"\t");
	    int i=0;
	    while(tokens.hasMoreTokens()){
	       array[i] = tokens.nextToken();
	       i++;
	    }	   
	    return array;
	}  

	// Captures variables from a String
	public static  Vector<String> getRecords(String line) {	   
		Vector <String>records = new Vector<String>();
	    StringTokenizer tokens = new StringTokenizer(line,"\t");
	    while(tokens.hasMoreTokens()){
	       String data = tokens.nextToken();
	       if (data != null) {
		       records.addElement(data );
	       }
	    }	   
	    return records;
	}
	
	// Get the Report Column Names
	public static Vector<String> getColumnNames(String line) {
		Vector <String>columns = new Vector<String>();	   
		StringTokenizer tokens = new StringTokenizer(line,"\t");
		while(tokens.hasMoreTokens()){
			columns.addElement((String)tokens.nextToken());
		}	   
		return columns;
	}
	  
	// Return Centered Aligned String
	public static String getCenteredString(String text, int maxWidth) {
		String centered = "";
		int titleLength = text.length();
		int spaces      = (maxWidth - titleLength)/2;
		  
		for (int i=0;i<spaces;i++) {
			   centered += " ";
		}
		//centered += text;
		  
		return centered;
	}
	  
	// Return Right Aligned String
	public static String getRightAlignedString(String text, int maxWidth) {
		String right = "";
		int wordLength = text.length();
		int spaces = maxWidth - wordLength;
		  
		for (int i=0;i<spaces;i++) {
			right += " ";
		}
		right += text;
		  
		return right;
	}
	  
	// Return Left Aligned String
	public static String getLeftAlignedString(String text, int maxWidth) {
		String left="";
		int wordLength = text.length();
		int spaces = maxWidth - wordLength;
		  
		left += text;
		for (int i=0;i<spaces;i++) {
			left += " ";
		}
		  
		return left;
	} 
	
	// Return an asterisk bar
	public static String getBar(int charactersPerLine){
		String bar = "";
		for (int i=0;i<charactersPerLine;i++) {
			bar += "=";
		}
		return bar;
	}
	
	// Check if an string var has only numbers
    public static boolean isNumber(String s) {
    	if (s.length() == 0) {
    		return false;
    	}
        for(int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if((!Character.isDigit(c)) && (c != ',') && (c != '.'))
              return false;
         }
        return true;
      }
}