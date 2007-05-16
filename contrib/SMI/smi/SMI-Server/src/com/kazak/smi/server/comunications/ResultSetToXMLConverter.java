package com.kazak.smi.server.comunications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.database.sql.QueryClosingHandler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConstants;

/**
 * ResultSetToXMLConverter.java Creado el 14-jul-2004
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
 * Esta clase se encarga de generar la estructura ANSWER de tal forma que pueda
 * ser enviada directamente al cliente que corresponda.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class ResultSetToXMLConverter extends Document {

	private static final long serialVersionUID = 6067284901344862308L;
	private String sql;
	private String [] argsArray;

	private ByteArrayOutputStream bufferOutputStream = null;

	/**
	 * Este constructor inicializa los objetos sql y bd nesesario para ejecutar
	 * la consulta
	 * 
	 * @param bd
	 *            Base de datos
	 * @param sql
	 *            Sentencia SQL
	 */

	public ResultSetToXMLConverter(String sql) {
		this.sql = sql;
	}
	
	public ResultSetToXMLConverter(String sql, String [] args) {
		this.sql = sql;
		this.argsArray = args; 
	}

	/**
	 * Metodo encargado de ejcutar y transmitir la sentencia sql
	 */
	public void transmit(SocketChannel socket, String id) {

		synchronized(socket) {
			try {
				try {
					bufferOutputStream = new ByteArrayOutputStream();
					XMLOutputter XMLformat = new XMLOutputter();
					QueryRunner qRunner;

					if(argsArray==null ) {
						qRunner = new QueryRunner(sql);
					}
					else {
						qRunner = new QueryRunner(sql,argsArray);
					}

					ResultSet resultSet = qRunner.select();

					try {

						ResultSetMetaData rsMetaData = resultSet.getMetaData();
						int columns = rsMetaData.getColumnCount();
						writeSocketBuffer(socket,
								ServerConstants.CONTEN_TYPE+
								ServerConstants.TAGS_ANSWER[0]+
								ServerConstants.TAGS_ID[0]+id+ServerConstants.TAGS_ID[1]+
								ServerConstants.TAGS_HEAD[0]);

						for (int i = 1; i <= columns; i++) {
							writeSocketBuffer(socket,
									ServerConstants.TAGS_COL_HEAD[0]+
									XMLformat.escapeAttributeEntities(rsMetaData.getColumnTypeName(i))+
									ServerConstants.TAGS_COL_HEAD[1]+
									XMLformat.escapeAttributeEntities(rsMetaData.getColumnName(i))+
									ServerConstants.TAGS_COL[1]);
						}
						writeSocketBuffer(socket,ServerConstants.TAGS_HEAD[1]);

						/**
						 * Se recorre el resulset para añadir los datos que contenga, y
						 * se escriben directamente en el socket en formato XML
						 */
						byte [] data;
						
						while (resultSet.next()) {
							writeSocketBuffer(socket,ServerConstants.TAGS_ROW[0]);
							for (int j = 1; j <= columns; j++) {

								data = resultSet.getBytes(j);

								if (data==null)
									data= new String("").getBytes();
								writeSocketBuffer(socket,ServerConstants.TAGS_COL[0] + 
										escapeCharacters(new String(data,"ISO-8859-1"))+
										ServerConstants.TAGS_COL[1]
								);
							}
							writeSocketBuffer(socket,ServerConstants.TAGS_ROW[1]);
						}	
							
						writeSocketBuffer(socket,ServerConstants.TAGS_ANSWER[1]);
						bufferOutputStream.write(new String ("\n\r\f").getBytes());
						SocketWriter.write(socket,bufferOutputStream);
						bufferOutputStream.close();
						QueryClosingHandler.close(resultSet);
					}
					catch (SQLException SQLEe) {
						String errorMessage = Language.getWord("ERR_RS") + " " +sql+" "+SQLEe.getMessage();
						LogWriter.write(errorMessage);
						XMLError error = new XMLError();
						SocketWriter.write(socket,error.returnErrorMessage(ServerConstants.ERROR, errorMessage));
						SQLEe.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					qRunner.closeStatement();
				}
				catch (SQLNotFoundException QNFEe) {
					String errorMessage = QNFEe.getMessage();
					LogWriter.write(errorMessage);
					XMLError error = new XMLError();
					SocketWriter.write(socket,error.returnErrorMessage(ServerConstants.ERROR,errorMessage));
					QNFEe.printStackTrace();

				} 
				catch (SQLException SQLEe) {
					String errorMessage = Language.getWord("ERR_ST") + " "+sql+" "+ SQLEe.getMessage();
					LogWriter.write(errorMessage);
					XMLError error = new XMLError();
					SocketWriter.write(socket,error.returnErrorMessage(ServerConstants.ERROR, errorMessage));
					SQLEe.printStackTrace();
				}
				catch (SQLBadArgumentsException QBAEe) {
					String errorMessage = QBAEe.getMessage();
					LogWriter.write(errorMessage);
					XMLError error = new XMLError();
					SocketWriter.write(socket,error.returnErrorMessage(ServerConstants.ERROR,errorMessage));
					QBAEe.printStackTrace();
				}
			}catch (IOException e) {
				LogWriter.write("ERROR: Falla de entrada y salida");
				LogWriter.write("Causa: " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	// Este metodo se encarga de traducir los caracteres especiales de los mensajes
	// en valores codificados en XML
	private String escapeCharacters(String word) {

		word = word.replaceAll("&","&#38;");
		word = word.replaceAll("'","&#39;");
		word = word.replaceAll("\"","&#34;");
		word = word.replaceAll("<","&#60;");
		word = word.replaceAll(">","&#62;");
		word = word.replaceAll("ñ","&#241;");
		word = word.replaceAll("Ñ","&#209;");
		word = word.replaceAll("á","&#225;");
		word = word.replaceAll("é","&#233;");
		word = word.replaceAll("í","&#237;");
		word = word.replaceAll("ó","&#243;");
		word = word.replaceAll("ú","&#250;");
		word = word.replaceAll("Á","&#201;");
		word = word.replaceAll("É","&#241;");
		word = word.replaceAll("Í","&#205;");
		word = word.replaceAll("Ó","&#211;");
		word = word.replaceAll("Ú","&#218;");
				
		return word;
	}
	
	private void writeSocketBuffer(SocketChannel sock,String data) {

		byte[] bytes = data.getBytes();

		for (int i=0;i<bytes.length;i++) {

			if (bufferOutputStream.size()<8192) {
				bufferOutputStream.write(bytes[i]);
			}
			else {
				SocketWriter.write(sock,bufferOutputStream);
				bufferOutputStream = new ByteArrayOutputStream();
				i--;
			}
		}
	}

}