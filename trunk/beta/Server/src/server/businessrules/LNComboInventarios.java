package server.businessrules;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import server.database.sql.ComboProductos;
import server.database.sql.LinkingCache;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

/**
 * LNInventarios.java Creado el 02-nov-2009
 * 
 * Este archivo es parte de E-Maku <A
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
 * Esta clase se encarga de procesar y reorganizar todo lo referente al
 * movimiento de Inventarios. <br>
 * 
 * @author <A href='mailto:pipelx@gmail.com'>Luis Felipe Hernandez</A>
 */

public class LNComboInventarios extends LNInventarios {

	private String bd;
	
	public LNComboInventarios(Element parameters, String bd) {
		super(parameters,bd);
		this.bd=bd;
	}

	/**
	 * Este metodo se llama cuando se va a generar una movimiento de inventarios
	 * 
	 * @param pack
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 * @throws SQLException
	 * @throws InterruptedException 
	 */

	public void movimientos(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException, InterruptedException {
		super.movimientos(pack);
		
		System.out.println("-------------");
        try {
	        XMLOutputter xmlOutputter = new XMLOutputter();
	        xmlOutputter.setFormat(Format.getPrettyFormat());
	        xmlOutputter.output(pack,System.out);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    
		String idProducto = null;	    
		int cantidad = 0;	    
		String vunitario = null;	    
		String bodegaSaliente = null;
		
	    Iterator i = pack.getChildren().iterator();
	    while (i.hasNext()) {
			Element field = (Element) i.next();
			String nameField = field.getAttributeValue("name");
			if ("idProducto".equals(nameField))
				idProducto = field.getValue();	    
			if ("cantidad".equals(nameField))
				cantidad = Integer.parseInt(field.getValue());	    
			if ("vunitario".equals(nameField))
				vunitario = field.getValue();	    
	    }
	    
	    ArrayList<ComboProductos> combos = LinkingCache.getComboProductos(bd,idProducto);
	    
	    if (combos!=null) {
	    	Iterator<ComboProductos> j = combos.iterator();
	    	while(j.hasNext()) {
	    		ComboProductos cb = j.next();
	    		Element comboPack = new Element("subPackage");
	    		
	    		Element fieldProd = new Element("field");
	    		fieldProd.setAttribute("name", "idProducto");
	    		fieldProd.setText(String.valueOf(cb.getIdProdServ()));
	    		comboPack.addContent(fieldProd);
	    		
	    		Element fieldCant = new Element("field");
	    		fieldCant.setAttribute("name", "cantidad");
	    		fieldCant.setText(String.valueOf(cb.getCantidad()*cantidad));
	    		comboPack.addContent(fieldCant);

	    		Element fieldVunit = new Element("field");
	    		fieldVunit.setAttribute("name", "vunitario");
	    		fieldVunit.setText(vunitario);
	    		comboPack.addContent(fieldVunit);
	    		
	    		super.movimientos(comboPack);
	    		
	    	}
	    }
	}

	/**
	 * Este metodo se encarga de efectuar traslados entre diferentes bodegas.
	 */

	public void traslados(Element pack) throws LNErrorProcecuteException,
			SQLBadArgumentsException, SQLBadArgumentsException,
			SQLNotFoundException, SQLException {
	}
}
