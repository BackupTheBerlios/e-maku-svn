package jmserver2.logicanegocios;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jmlib.miscelanea.idiom.Language;
import jmserver2.basedatos.sql.RunQuery;
import jmserver2.basedatos.sql.SQLBadArgumentsException;
import jmserver2.basedatos.sql.SQLNotFoundException;
import jmserver2.comunicaciones.SocketServer;

import org.jdom.Document;
import org.jdom.Element;

/**
 * LNGenericSQL.java Creado el 21-ene-2005
 * 
 * Este archivo es parte de JMServerII <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de efectuar operaciones sql simples, ya sean inserts,
 * updates o deletes, dependiendo del parametro recibido. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class LNGenericSQL {

	private Element pack;

	private RunQuery RQtransaction;

	private String args[];

	private boolean keyfield;

	private boolean finalKey;

	private boolean generable = true;

	private Map <String,String>keyvalue;

	public LNGenericSQL(SocketChannel sock) {
		keyvalue = new LinkedHashMap<String,String>();
		RQtransaction = new RunQuery(SocketServer.getBd(sock));
	}

	public LNGenericSQL(SocketChannel sock, Document doc, Element sn_pack,
			String id_transaction) {
		this.pack = (Element) sn_pack.getChildren().iterator().next();
		keyvalue = new LinkedHashMap<String,String>();

		Iterator i = doc.getRootElement().getChildren().iterator();

		/*
		 * Esta variable almacena el codigo sql de la instruccion que sera
		 * ejecutada
		 */
		RQtransaction = new RunQuery(SocketServer.getBd(sock));
		compactarArgumentos();
		setAutoCommit(false);
		try {
			while (i.hasNext()) {
				Element elm = (Element) i.next();
				generar(elm.getValue());
			}
			commit();
			RunTransaction.successMessage(sock, id_transaction, Language
					.getWord("TRANSACTION_OK"));
		} catch (SQLNotFoundException SQLNFEe) {
			RunTransaction.errorMessage(sock, id_transaction, SQLNFEe
					.getMessage());
			rollback();
		} catch (SQLBadArgumentsException SQLBAEe) {
			RunTransaction.errorMessage(sock, id_transaction, SQLBAEe
					.getMessage());
			rollback();
		} catch (SQLException SQLe) {
			RunTransaction
					.errorMessage(sock, id_transaction, SQLe.getMessage());
			rollback();
		} catch (NullPointerException NPEe) {
			NPEe.printStackTrace();
			rollback();
		}
		setAutoCommit(true);
	}

	/**
	 * Este metodo se encarga combertir los argumentos de <xml> a un arreglo
	 * para luego ser generada la sentencia sql
	 * 
	 * Si una tag bien de la siguiente forma <field attributo="key"> significa
	 * que ese campo es una llave, la cual sera enviada a todos los demas
	 * paquetes y/o subpaquetes.
	 */

	public void compactarArgumentos() {

		List lista = pack.getChildren();
		Iterator iterador = lista.iterator();
		int countParams = pack.getContentSize();
		Element Efirst = (Element) iterador.next();
		String SvalueFirst = Efirst.getValue().trim();
		String SattributeFirst = Efirst.getAttributeValue("attribute");
		
		/*
		 * puede venir de las dos formas
		 */
		String SattributeName = Efirst.getAttributeValue("name");
		if (Efirst.getAttributeValue("nameField")!=null  && !"".equals(Efirst.getAttributeValue("nameField"))) {
			SattributeName = Efirst.getAttributeValue("nameField");
		}

		if ("".equals(SvalueFirst))
			countParams = 0;

		// Si la etiqueta contiene el atributo disableKey, se le asigna el valor
		// de falso a la
		// variable keyfield, si eliminar el contenido del vector de llaves
		if ("disableKey".equals(SattributeFirst)) {
			keyfield = false;
		}
		// Si la etiqueta contiene el atributo enableKey, se le asigna el valor
		// de true a la
		// variable keyfield.
		else if ("enableKey".equals(SattributeFirst)) {
			keyfield = true;
		}
		int j = 0;

		/*
		 * Se verifica si existen llaves activas para el procesamiento del
		 * paquete actual, esto se hace por medio de la variable keyfield, si el
		 * valor de esta es true, entonces se inicializa el arreglo de
		 * argumentos con el contenido del paquete mas el numero de llaves y se
		 * asigna como argumentos el contenido del vector keyvalue
		 */

		if (keyfield) {
			args = new String[countParams + keyvalue.size()];

			if (!"finalKey".equals(SattributeFirst)) {
				Iterator val = keyvalue.values().iterator();
				
				for (; val.hasNext(); j++) {
					args[j] = (String) val.next();
				}
			} else {
				finalKey = true;
			}
		} else
			args = new String[countParams];
		// Si la primera etiqueta del paquete contiene una llave en el primer
		// registro, se la
		// asigna al vector de llaves y se le da el valor de true a la
		// variable keyfield.

		if ("key".equals(SattributeFirst)) {
			keyfield = true;
			if (SattributeName == null) {
				SattributeName = new String("");
			}
			keyvalue.put(SattributeName, SvalueFirst);
		}

		if (countParams > 0)
			args[j] = SvalueFirst;

		j++;

		for (; iterador.hasNext(); j++) {
			pack = (Element) iterador.next();
			if (pack.getName().equals("field")) {
				if (!pack.getValue().equals("")) {
					args[j] = pack.getValue();
				} else if (pack.getAttributeValue("attribute") != null
						&& "NULL".equals(pack.getAttributeValue("attribute"))) {
					args[j] = "NULL";
				} else {
					args[j] = "";
				}
				try {
					// Si existen mas llaves en el contenido del paquete, estas
					// se van adicionando al vector de
					// llaves y se le da el valor de true a la variable
					// keyfield.
					if ("key".equals(pack.getAttribute("attribute").getValue())) {
						keyfield = true;
						if (SattributeName == null) {
							SattributeName = new String("");
						}
						keyvalue.put(SattributeName, args[j]);

					}
				} catch (NullPointerException NPEe) {
				}
			}

		}

		if (finalKey) {
			Iterator val = keyvalue.values().iterator();
			for (int i = 0; val.hasNext(); i++, j++) {
				args[j] = (String) val.next();
				// System.out.println("args: "+args[j]);
			}

			finalKey = false;
		}
		/*
		 * 
		 * System.out.println("No. de argumentos: "+args.length); for (int i=0;i<args.length;i++)
		 * System.out.println("Argumento "+i+" posicion "+args[i]);
		 * 
		 */
	}

	public void generar(String sql) throws SQLException, SQLNotFoundException,
			SQLBadArgumentsException {
		if (generable) {
			RQtransaction.ejecutarSQL(sql, args);
		}
	}

	public void setArgs(Element pack, String id_transaction) {
		this.pack = pack;
		compactarArgumentos();
	}

	public void setArgs(String[] argUpdate, String id_transaction) {
		this.args = argUpdate;
	}

	public String getKey(int index) {
		Vector <String>vector = new Vector<String>(keyvalue.values());
		return vector.get(index);
	}

	public void removeKey(String key) {
		keyvalue.remove(key);
	}

	public void setKey(String key, String value) {
		keyvalue.put(key, value);
		keyfield = true;
	}

	public Map getKeys() {
		return keyvalue;
	}

	public String getArg(int index) {
		return args[index];
	}

	public void setAutoCommit(boolean value) {
		RQtransaction.setAutoCommit(value);
	}

	public void commit() {
		RQtransaction.commit();
	}

	public void rollback() {
		RQtransaction.rollback();
	}

	public boolean isGenerable() {
		return generable;
	}

	public void setGenerable(boolean generable) {
		this.generable = generable;
	}
}