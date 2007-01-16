package common.printer;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import common.misc.CommonConst;

/**
 * Esta clase se encarga de crear un buffer de texto para acceso aleatorio, el 
 * buffer está representado en forma de una matriz.
 * 
 * @author cristian@qhatu.net
 */
public class TextPrinterBuffer {
	
	private ArrayList<StringBuilder> buffer;
	private Vector<Object[]> scpCodes  = new Vector<Object[]>(); 
	
	/**
	 * Constructor que inicializa el buffer
	 */
	public TextPrinterBuffer() {
		buffer = new ArrayList<StringBuilder>();
	}
	
	/**
	 * Adiciona los codigos de escape para manejar impresoras sin filtro
	 * @param scpCode
	 */
	public void addScpCode(Object[] scpCode) {
		scpCodes.add(scpCode);
	}
	
	/**
	 * Metodo que se encarga de adicionar texto multinea
	 * 
	 * @param text Texto a insertar
	 * @param row fila
	 * @param col columna
	 * @param width ancho para ajuste del texto.
	 * @param height alto
	 * @param trim si es true limpia espacios al inicio y al final del texto
	 */
	public void insertTextArea(String text,int row, int col, Integer width, Integer height, boolean trim) {
		if (trim) {
			text = text.replaceAll("\n", " ");
		}
		StringBuffer buf = new StringBuffer( trim ? text.trim() : text);
		int lastspace = -1;
		int linestart = 0;
		int i = 0;
		if (trim) {
			while ( i < buf.length()) {
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
		}
		StringTokenizer st = new StringTokenizer(buf.toString(),"\n");
		for (int j=0; j < height && st.hasMoreElements(); j++) {
			String tok = st.nextToken();
			//tok = "".equals(tok) ? " " : tok;
			//System.out.println(":-) {" + tok+ "}= " +height );
			insertString(tok,row++,col,null);
		}
	}
	
	/**
	 * Inserta un texto de una sola linea
	 * @param str
	 * @param row
	 * @param col
	 * @param width
	 */
	public void insertString(String str, int row, int col,Integer width) {
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
			buffer.get(row-1).replace(col-1,str.length()+(col-1),str);
		}
		else {
			buffer.get(row-1).insert(col-1,str);
		}
	}
	
	/**
	 * Retorna el contenido del buffer
	 * @return
	 */
	public String getBufferString() {
		StringBuilder string = new StringBuilder();
		int i=0;
		for (; i< buffer.size()-1;i++) {
			string.append(buffer.get(i)+"\n");
		}
		string.append(buffer.get(i));
		return string.toString();
	}
	
	/**
	 * Retorna un flujo de entrada para ser procesado con el PlainManager
	 * @return
	 */
	public ByteArrayInputStream getStream() {
		for (Object[] scpCode :scpCodes) {
			int row = (Integer)scpCode[0];
			int col = (Integer)scpCode[1];
			String val = (String)scpCode[2];
			if (row > buffer.size() ) {
				while (row > buffer.size()) {
					buffer.add(new StringBuilder());
				}
			}
			buffer.get(row-1).insert(col-1,val);
		}

		return new ByteArrayInputStream(getBufferString().getBytes()); 
	}
	
	/**
	 * Convierte los nombres de los codigos de escape a entero
	 * @param key cadena con el codigo de escape en formato numerico
	 * @return
	 */
	public String Convert(String key) {
		try {
			int Int = 	CommonConst.ScpCodes.containsKey(key)?
						CommonConst.ScpCodes.get(key):
						Integer.parseInt(key);
			return String.valueOf((char)Int);
		} catch (NumberFormatException NFEe) {
			NFEe.printStackTrace();
			return null;
		}
	}
}
