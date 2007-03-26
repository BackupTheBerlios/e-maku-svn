package client.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import client.gui.components.MainWindow;
import client.gui.forms.Splash;

import common.comunications.SocketConnector;
import common.comunications.SocketWriter;
import common.misc.ZipHandler;

/**
 * ACPHandler.java Creado el 13-ago-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de validar las formas, los query y solicitar los caches
 * al ST <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ACPHandler {

	private static Hashtable<String,TransactionsData> Htransactions = new Hashtable<String,TransactionsData>();;
	private static Hashtable<String,String> Hquery = new Hashtable<String,String>();
	private static int numTrans = 0;
	public static int countData = 0;
	private static Vector<ACPFormListener> ACPFormListener = new Vector<ACPFormListener>();

	public static void ACPBegin(Document doc) {
		numTrans = Integer.parseInt(doc.getRootElement().getChild("transactions").getValue());
		Splash.ShowSplash();
		Splash.setProgresRank(0, numTrans + 1);
	}

	public static void ACPData(Document documento) {
		
		Document doc = null;
		SAXBuilder builder = new SAXBuilder(false);
		try {
			doc = builder.build(new ZipHandler().getDataDecodeInputStream(documento.getRootElement().getValue()));
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		Iterator i = doc.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element elm =(Element)i.next();
			Iterator j = elm.getChildren().iterator();
			countData++;
			while (j.hasNext()) {
				Element e = (Element) j.next();
	
				/*
				 * Si el elemento es una transaccion entonces se procede a cargarla
				 * en una tabla
				 */
				if (e.getName().equals("transaction")) {
					List Lsubdatos = e.getChildren();
					Iterator k = Lsubdatos.iterator();
					transactions(k);
				}
	
				/*
				 * Si el elemento es un query entonces se procede a cargar todas las
				 * consultas en una hash
				 */
				else if (e.getName().equals("query")) {
					List Lsubdatos = e.getChildren();
					Iterator k = Lsubdatos.iterator();
					query(k);
				}
	
				/*
				 * Si el elemento es un cache, se procede a solicitar dicho cache al
				 * ST
				 */
	
				else if (e.getName().equals("CACHE-QUERY")) {
					SocketChannel socket = SocketConnector.getSock();
					SocketWriter.writing(socket, new Document((Element)e.clone()));
				}
			}
		}
		Splash.DisposeSplash();
		MainWindow.getRefWindow().setVisible(true);
	}

	/**
	 * Este metodo carga los perfiles de las formas en una tabla hash
	 * 
	 * @param e
	 *            contiene el xml de la forma
	 */
	private static void transactions(Iterator j) {
			String id = "";
			TransactionsData valores = new TransactionsData();

			while (j.hasNext()) {
				Element f = (Element) j.next();
				String name = f.getName();
				if (name.equals("driver")) {
					id = f.getValue();
					valores.setAuthentication(f.getAttributeValue("type"));

				} else if (name.equals("FORM")) {
					valores.setForm(f);
				}
			}
			Htransactions.put(id, valores);
			Splash.incrementProgresValue();
			ACPFormEvent event = new ACPFormEvent(new ACPHandler(), id);
			notifyACPForm(event);
	}

	private static void notifyACPForm(ACPFormEvent event) {
		/*Vector lista;
		lista = (Vector) ACPFormListener.clone();*/
		for (int i = 0; i < ACPFormListener.size(); i++) {
			ACPFormListener listener = ACPFormListener.elementAt(i);
			listener.arriveACPForm(event);
		}
	}

	public static synchronized void addACPFormListener(ACPFormListener listener) {
		ACPFormListener.addElement(listener);
	}

	public static synchronized void removeACPFormListener(
			ACPFormListener listener) {
		ACPFormListener.removeElement(listener);
	}

	/**
	 * Este metodo carga las query en una tabla hasth
	 * 
	 * @param j
	 *            contiene un iterador el cual contiene toda la informacion de
	 *            los query.
	 */
	private static void query(Iterator j) {
		while (j.hasNext()) {
			Element e = (Element) j.next();
			Hquery.put(e.getValue(), e.getAttributeValue("type"));
		}
		Splash.incrementProgresValue();
	}

	/**
	 * Este metodo verifica que la forma exista en la tabla hash.
	 * 
	 * @param key
	 *            contiene el identificador de la forma
	 * @return retorn true si la forma existe en la hash o false si no existe
	 */

	public static boolean isContenedTransaction(String key) {
		return Htransactions.containsKey(key);
	}

	/**
	 * Este metodo retorna el documento que genera una forma especifica
	 * 
	 * @param key
	 *            contiene el identificador de la clase
	 * @return Retorna el Documento parametrizador de una forma
	 */

	public static String getAuthForm(String key) {
		return  Htransactions.get(key).getAuthentication();
	}

	public static Document getDocForm(String key) {
		return Htransactions.get(key).getForm();
	}

	/**
	 * Este metodo verifica la existencia de una forma en la hash.
	 * 
	 * @param key
	 *            contiene el identificador de la forma
	 * @return retorn true si el query existe en la hash o false si no existe
	 */

	public static boolean isContenedQuery(String key) {
		return Hquery.containsKey(key);
	}

	/**
	 * Este metodo retorna el sql de un query
	 * 
	 * @param key
	 *            contiene el identifcador de la forma
	 * @return retorna el sql del query
	 */
	public static String getAuthQuery(String key) {
		return Hquery.get(key);
	}
}

class TransactionsData {

	private String authentication;

	private Document form;

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public Document getForm() {
		return form;
	}

	public void setForm(Element e) {
		Element el = (Element) e.clone();
		form = new Document();
		form.setRootElement(el);
	}
}