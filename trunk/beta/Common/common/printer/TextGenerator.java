package common.printer;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TextGenerator {
	
	ArrayList<StringBuilder> buffer;
	
	public TextGenerator() {
		buffer = new ArrayList<StringBuilder>();
	}
	
	public void addTextArea(String text,int row, int col, int width, int height) {
		
		text = text.replaceAll("\n", " ");
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
			if (i > linestart + width) {
				//if (i > linestart + width -1) {
				if (lastspace != -1) {
					buf.delete(lastspace,lastspace+1);
					buf.insert(lastspace,"\n");
					linestart = lastspace;
					lastspace = -1;
					
				} else {
					buf.insert(i, '\n');
					linestart = i + 1;
				}
			}
			i++;
		}
		StringTokenizer st = new StringTokenizer(buf.toString(),"\n");
		for (int j=0; j < height && st.hasMoreTokens(); j++) {
			addString(st.nextToken(),row++,col,null);
		}
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
