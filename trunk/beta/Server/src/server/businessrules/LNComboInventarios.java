package server.businessrules;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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


	public LNComboInventarios(Element parameters, String bd) {
		super(parameters,bd);
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
		
        try {
	        XMLOutputter xmlOutputter = new XMLOutputter();
	        xmlOutputter.setFormat(Format.getPrettyFormat());
	        xmlOutputter.output(pack,System.out);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
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
