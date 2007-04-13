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
import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConst;

/**
 * ResultSetToXML.java Creado el 14-jul-2004
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
public class ResultSetToXML extends Document {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6067284901344862308L;
	private String sql;
	private String [] args;

	private ByteArrayOutputStream bufferSocket = null;

	/**
	 * Este constructor inicializa los objetos sql y bd nesesario para ejecutar
	 * la consulta
	 * 
	 * @param bd
	 *            Base de datos
	 * @param sql
	 *            Sentencia SQL
	 */

	public ResultSetToXML(String sql) {
		this.sql = sql;
	}
	public ResultSetToXML(String sql, String [] args) {
		this.sql = sql;
		this.args = args; 
	}

	/**
	 * Metodo encargado de ejcutar y transmitir la sentencia sql
	 */
	public void transmition(SocketChannel sock, String id) {

		synchronized(sock) {
			try {
				try {
					bufferSocket = new ByteArrayOutputStream();
					XMLOutputter XMLformat = new XMLOutputter();
					RunQuery rselect;

					if(args==null ) {
						rselect = new RunQuery(sql);
					}
					else {
						rselect = new RunQuery(sql,args);
					}

					ResultSet RSdatos = rselect.runSELECT();

					try {

						ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
						int columns = RSMDinfo.getColumnCount();
						writeBufferSocket(sock,
								ServerConst.CONTEN_TYPE+
								ServerConst.TAGS_ANSWER[0]+
								ServerConst.TAGS_ID[0]+id+ServerConst.TAGS_ID[1]+
								ServerConst.TAGS_HEAD[0]);

						for (int i = 1; i <= columns; i++) {
							writeBufferSocket(sock,
									ServerConst.TAGS_COL_HEAD[0]+
									XMLformat.escapeAttributeEntities(RSMDinfo.getColumnTypeName(i))+
									ServerConst.TAGS_COL_HEAD[1]+
									XMLformat.escapeAttributeEntities(RSMDinfo.getColumnName(i))+
									ServerConst.TAGS_COL[1]);
						}
						writeBufferSocket(sock,ServerConst.TAGS_HEAD[1]);

						/**
						 * Se recorre el resulset para a�adir los datos que contenga, y
						 * se escriben directamente en el socket en formato XML
						 */
						 byte [] res;
						 String record;
						while (RSdatos.next()) {
							writeBufferSocket(sock,ServerConst.TAGS_ROW[0]);
							for (int j = 1; j <= columns; j++) {

								res = RSdatos.getBytes(j);

								if (res==null)
									res= new String("").getBytes();

								/*
								record = new String(res,"ISO-8859-1");
								if(record.startsWith("NI")) {
								   System.out.println("DATA: " + record);
								}
								
								char[] data = record.toCharArray();
								boolean flag = false;
								for(int i=0;i<data.length;i++) {
									int p = data[i];
									if (p == 65533) {
										data[i] = 'N';
										flag = true;
									}
								}
								if (flag) {
									System.out.println("DATA: " + record);
									record = new String(data);
									System.out.println("DATA2: " + record);
								} 
								*/

								record = new String(res,"ISO-8859-1");
								writeBufferSocket(sock,ServerConst.TAGS_COL[0] + 
										XMLformat.escapeAttributeEntities(record)+
										ServerConst.TAGS_COL[1]
								);
							}
							writeBufferSocket(sock,ServerConst.TAGS_ROW[1]);
						}
						writeBufferSocket(sock,ServerConst.TAGS_ANSWER[1]);
						bufferSocket.write(new String ("\n\r\f").getBytes());
						SocketWriter.writing(sock,bufferSocket);
						bufferSocket.close();
						CloseSQL.close(RSdatos);
//						LogAdmin.setMessage(Language.getWord("OK_CREATING_XML"),
//						ServerConst.MESSAGE);

					}
					catch (SQLException SQLEe) {
						String err = Language.getWord("ERR_RS") + " " +sql+" "+SQLEe.getMessage();
						LogWriter.write(err);
						ErrorXML error = new ErrorXML();
						SocketWriter.writing(sock,error.returnError(ServerConst.ERROR, err));
						SQLEe.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					rselect.closeStatement();
				}
				catch (SQLNotFoundException QNFEe) {
					String err = QNFEe.getMessage();
					LogWriter.write(err);
					ErrorXML error = new ErrorXML();
					SocketWriter.writing(sock,error.returnError(ServerConst.ERROR,err));
					QNFEe.printStackTrace();

				} 
				catch (SQLException SQLEe) {
					String err = Language.getWord("ERR_ST") + " "+sql+" "+ SQLEe.getMessage();
					LogWriter.write(err);
					ErrorXML error = new ErrorXML();
					SocketWriter.writing(sock,error.returnError(ServerConst.ERROR, err));
					SQLEe.printStackTrace();
				}
				catch (SQLBadArgumentsException QBAEe) {
					String err = QBAEe.getMessage();
					LogWriter.write(err);
					ErrorXML error = new ErrorXML();
					SocketWriter.writing(sock,error.returnError(ServerConst.ERROR,err));
					QBAEe.printStackTrace();
				}
			}catch (IOException e) {
				LogWriter.write("Error de entrada y salida");
				LogWriter.write("mensaje: " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private void writeBufferSocket(SocketChannel sock,String data) {

		byte[] bytes = data.getBytes();

		for (int i=0;i<bytes.length;i++) {

			if (bufferSocket.size()<8192) {
				bufferSocket.write(bytes[i]);
			}
			else {
				SocketWriter.writing(sock,bufferSocket);
				bufferSocket = new ByteArrayOutputStream();
				i--;
			}
		}
	}   
}