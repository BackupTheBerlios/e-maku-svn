/**
 * 
 * DontHaveBalance.java Creado 11/10/2005 
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
 * Esta excepcion se lanza cuando no hay partida doble o sumas iguales
 * en el momento de realizar un asiento contable.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
package server.businessrules;

import common.miscelanea.idiom.Language;

public class DontHaveBalanceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2647631515026819058L;
	private String message;
	/**
	 * 
	 */
	public DontHaveBalanceException(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return Language.getWord("ERR_DONT_HAVE_BALANCE_EXCEPTION") + this.message;
	}

}
