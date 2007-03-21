package common.printer.plainViewer;

import java.util.StringTokenizer;
import java.util.Vector;

public class TextReportUtils {
	
	//int charactersPerLine;

	/*
    public TextReportUtils(int charactersPerLine) {
    	this.charactersPerLine = charactersPerLine;
	}*/
	
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

	// Return Right Aligned String
	public static String getRightAlignedHTMLString(String text, int maxWidth) {
		String right = "";
		int wordLength = text.length();
		int spaces = maxWidth - wordLength;
		  
		for (int i=0;i<spaces;i++) {
			right += "&nbsp;";
		}
		right += text;
		  
		return right;
	}
	  
	// Return Left Aligned String
	public static String getLeftAlignedHTMLString(String text, int maxWidth) {
		String left="";
		int wordLength = text.length();
		int spaces = maxWidth - wordLength;
		  
		left += text;
		for (int i=0;i<spaces;i++) {
			left += "&nbsp;";
		}
		  
		return left;
	} 	
	
	public static String getBar(int charactersPerLine){
		String bar = "";
		for (int i=0;i<charactersPerLine;i++) {
			bar += "=";
		}
		return bar;
	}
	
    public static boolean isNumber(String s) {
    	
    	if (s.length() == 0) {
    		return false;
    	}
    	
        for(int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if(!Character.isDigit(c))
              return false;
         }
        return true;
      }

}