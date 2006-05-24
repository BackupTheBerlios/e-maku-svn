package common.printer;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class TextGenerator {
	
	ArrayList<StringBuilder> buffer;
	
	public TextGenerator() {
		buffer = new ArrayList<StringBuilder>();
	}
	
	public void addTextArea(String text,int row, int col, int width, int height) {
		
		text = text.replaceAll("\n", "");
		int max = width*height;
		StringBuffer buf = new StringBuffer(text.trim());
		int lastspace = -1;
		int linestart = 0;
		int i = 0;
		
		
		while (i < buf.length()) {
			if (buf.charAt(i) == ' ') {
				lastspace = i;
			}
			if (buf.charAt(i) == '\n') {
				lastspace = -1;
				linestart = i + 1;
			}
			if (i > linestart + width -1) {
				if (lastspace != -1) {
					String add = "\n";
					for (int k=0; k < (i-lastspace)-1;k++) {
						add = " ".concat(add); 
					}
					buf.insert(lastspace+1,add);
					linestart = lastspace + 1;
					lastspace = -1;
				} else {
					buf.insert(i, '\n');
					linestart = i + 1;
				}
			}
			i++;
		}
		int end = max > buf.length() ? buf.length() : max;
		String textArea = buf.toString().substring(0,end);
		int j=0;
		String acum = "";
		while(j  < textArea.length() ) {
			if (textArea.charAt(j)!='\n') {
				acum += textArea.charAt(j); 
			}
			else {
				addString(acum.trim(),row++,col,null);
				acum ="";
			}
			j++;
		}
		addString(acum.trim(),row++,col,null);
	}
	
	public void addString(String str, int row, int col,Integer width) {
		if (row==0 || col ==0 || "".equals(str)) { return; }
		if (row > buffer.size() ) {
			while (row > buffer.size()) {
				buffer.add(new StringBuilder());
			}
		}
		if (col-1 > buffer.get(row-1).length()) {
			String string ="";
			for (int i=0 ; i < col-1 - buffer.get(row-1).length(); i++ ) {
				string+=" ";
			}
			buffer.get(row-1).insert(buffer.get(row-1).length(),string);
		}
		String fill=""; 
		if (width!=null ) {
			for (int i=0; i< width.intValue() - str.length();i++) {
				fill+=" ";
			}
		}
		str = fill+str;
		if ( row < buffer.size()  && col -1 < buffer.get(row-1).length()) {
			buffer.get(row-1).replace(col-1,str.length(),str);
		}
		else {
			buffer.get(row-1).insert(col-1,str);
		}
	}
	
	public String getBufferString() {
		String string = "";
		for (Object character : buffer) {
			string +=character+"\n";
		} 
		return string;
	}
	
	public ByteArrayInputStream getStream() {
		return new ByteArrayInputStream(getBufferString().getBytes()); 
	}
}
