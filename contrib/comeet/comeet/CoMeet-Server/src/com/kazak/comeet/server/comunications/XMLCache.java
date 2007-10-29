package com.kazak.comeet.server.comunications;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import org.jdom.Document;

import com.kazak.comeet.lib.misc.Language;
import com.kazak.comeet.server.database.sql.QueryClosingHandler;
import com.kazak.comeet.server.database.sql.QueryRunner;
import com.kazak.comeet.server.database.sql.SQLBadArgumentsException;
import com.kazak.comeet.server.database.sql.SQLNotFoundException;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.ServerConstants;

/**
 * XMLCache.java Creado el 06-sep-2004
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
 * Esta clase se encarga de generar los paquetes cache-answer, apartir del codigo
 * solicitado.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class XMLCache extends Document {

	private static final long serialVersionUID = -3555258118537262861L;
	private String code;

	public XMLCache(String code) {
		this.code=code;
	}

	/**
	 * Metodo encargado de ejecutar y trancomeettir la sentencia sql
	 */
	public void trancomeett(SocketChannel sock) {
		
		try {
			try {            
				QueryRunner qRunner;

				/*
				 * Se obtiene las llaves del cache que se va a generar
				 * la consulta para obtener las llaves es la SEL0007,
				 * recibe como argumentos el codigo de CACHE 
				 */
				String[] argsArray = {code};
				String SQL = "";
				ResultSet resultSet = new QueryRunner("SEL0009",argsArray).select();
				resultSet.next();
				SQL= resultSet.getString("codigo");
				QueryClosingHandler.close(resultSet);
				qRunner = new QueryRunner("SEL0007",argsArray);
				resultSet = qRunner.select();

				/*
				 *  Se captura las llaves en una tabla hash y en un String
				 */

				Hashtable <String,String>keys = new Hashtable<String,String>();
				//String skey = ""; // TODO: Preguntar que hace skey ???
				String key = "";
				int columnsNum = 0;
				try {
					while (resultSet.next()) {
						key = resultSet.getString("codigo").trim();
						keys.put(key,"");
						//skey+=llave;
						columnsNum++;
					}

					QueryClosingHandler.close(resultSet);
					/*
					 * A medida que se empieza a generar el paquete CACHE-ANSWER, se empieza
					 * a trancomeettir 
					 */

					resultSet = new QueryRunner("SEL0010",argsArray).select();
					resultSet.next();
					String sqlCache = resultSet.getString("sentencia_cache");

					resultSet = new QueryRunner(code, sqlCache).select();
					ResultSetMetaData rsMetaData = resultSet.getMetaData();
					int columns = rsMetaData.getColumnCount();

					/*
					 * Generacion de etiquetas
					 * <CACHE-ANSWER>
					 * 		<header>
					 */
					SocketWriter.write(sock,
							ServerConstants.CONTEN_TYPE+
							ServerConstants.TAGS_CACHE_ANSWER[0]+
							ServerConstants.TAGS_SQL[0]+ SQL +
							ServerConstants.TAGS_SQL[1]+
							ServerConstants.TAGS_HEAD[0]);

					for (int i=1;i<=columns;i++) {
						/*
						 * Se crean las cabeceras diferentes a las llaves.
						 * Generacion de etiquetas de la forma:
						 * 		<col type="tipo">nombre</col>
						 */

						if (!keys.containsKey(rsMetaData.getColumnName(i)))
							SocketWriter.write(sock,
									ServerConstants.TAGS_COL_HEAD[0]+
									rsMetaData.getColumnTypeName(i)+
									ServerConstants.TAGS_COL_HEAD[1]+
									rsMetaData.getColumnName(i)+
									ServerConstants.TAGS_COL[1]);
					}

					/*
					 * Generacion de etiquetas
					 * 		</header>
					 * 		<value>
					 */
					SocketWriter.write(sock,ServerConstants.TAGS_HEAD[1]);

					/*
					 * Se recorre el resulset para a�adir los datos que contenga, y
					 * se escriben directamente en el socket en formato XML
					 */

					String newKey = "";
					String oldKey="";
					boolean closeTags = true;

					while (resultSet.next()) {
						newKey = "";
						for (int j=1;j<=columnsNum;j++) {
							newKey+=resultSet.getString(j).trim();
						}

						if (!newKey.equals(oldKey)) {
							SocketWriter.write(sock,ServerConstants.TAGS_VALUE[0]+
									ServerConstants.TAGS_KEY[0]+
									newKey+
									ServerConstants.TAGS_KEY[1]+
									ServerConstants.TAGS_ANSWER[0]
							);
						}
						/*
						 * <row>
						 * 	<col>value</col>
						 */
						SocketWriter.write(sock,ServerConstants.TAGS_ROW[0]);
						for (int j=columnsNum+1;j<=columns;j++) {
							SocketWriter.write(sock,ServerConstants.TAGS_COL[0] + 
									resultSet.getString(j).trim()+
									ServerConstants.TAGS_COL[1]
							);
						}
						SocketWriter.write(sock,ServerConstants.TAGS_ROW[1]);
						/*
						 * </row>
						 */

						if (!newKey.equals(oldKey) && !oldKey.equals("")) {
							SocketWriter.write(sock,ServerConstants.TAGS_ANSWER[1]+
									ServerConstants.TAGS_VALUE[1]
							);
							closeTags = false;
						} else {
							closeTags = true;
						}
						
                        oldKey = newKey;
					}

					if (closeTags) {
						SocketWriter.write(sock,ServerConstants.TAGS_ANSWER[1]+
								ServerConstants.TAGS_VALUE[1]);
					}

					SocketWriter.write(sock,ServerConstants.TAGS_CACHE_ANSWER[1]);
					QueryClosingHandler.close(resultSet);
					LogWriter.write(Language.getWord("OK_CREATING_XML"));

				}
				catch (SQLException SQLEe) {
					String errorMessage = Language.getWord("ERR_RS") + " " + SQLEe.getMessage();
					LogWriter.write(errorMessage);
					XMLError error = new XMLError();
					SocketWriter.write(sock,error.returnErrorMessage(ServerConstants.ERROR, errorMessage));
				}
				qRunner.closeStatement();
			}
			catch (SQLNotFoundException QNFEe) {
				String errorMessage = QNFEe.getMessage();
				LogWriter.write(errorMessage);
				XMLError error = new XMLError();
				SocketWriter.write(sock,error.returnErrorMessage(ServerConstants.ERROR,  errorMessage));

			} 
			catch (SQLException SQLEe) {
				String errorMessage = Language.getWord("ERR_ST") + " " + SQLEe.getMessage();
				LogWriter.write(errorMessage);
				XMLError error = new XMLError();
				SocketWriter.write(sock,error.returnErrorMessage(ServerConstants.ERROR,  errorMessage));
			}
			catch (SQLBadArgumentsException QBAEe) {
				String errorMessage = QBAEe.getMessage();
				LogWriter.write(errorMessage);
				XMLError error = new XMLError();
				SocketWriter.write(sock,error.returnErrorMessage(ServerConstants.ERROR,  errorMessage));
			}
		}
		catch (IOException e) {
			LogWriter.write("Error de entrada y salida");
			LogWriter.write("mensaje: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
