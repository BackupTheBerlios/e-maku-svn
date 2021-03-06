package server.control;

import server.database.sql.UserValidator;

import org.jdom.Element;

/**
 * LoginUser.java Creado el 23-jul-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class UserDataStructure {
    
    private Element data;
    private String bd;
    private String login;
    private String tipoDoc;
    
    public String getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	public String getBD(){
        return bd;
    }
    public String getLogin() {
        return login;
    }
    public UserDataStructure(Element data){
    	this.data = data;
        
    }
    
    
    public boolean valid() {
        try {
        	
	        bd = data.getChild("db").getValue();
	        login = data.getChild("login").getValue();
	        String password = data.getChild("password").getValue();
        	Element td = data.getChild("tipoDoc");
        	if (td!=null) {
        		tipoDoc = td.getValue();
        	}
	        return UserValidator.validdb(bd,login,password);

        }
        catch (NullPointerException NPEe) {
        	return false;
        }
    }
}
