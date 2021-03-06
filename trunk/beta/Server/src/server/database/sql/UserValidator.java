package server.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.misc.ServerConstants;
import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * ValidarUsuario.java Creado el 23-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
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
 * Esta clase se utiliza para validar un usuario.
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class UserValidator {

    /**
     * Este metodo verifica si el usuario pertenece a la base
     * de datos que solicito ser autenticado
     * 
     * @param db Base de datos 
     * @param login Nombre de usuario
     * @param password Clave
     * @return retorna true si la validacion del usuario fue exitosa, de lo contrario
     * retorna false
     */
    public static boolean validdb(String db, String login, String password) {

        String [] args = { login, password };
        QueryRunner sql = null;
        ResultSet rs = null;
        boolean retorno = false;

        try {
            sql = new QueryRunner(db, "SCS0003",args);
            rs = sql.ejecutarSELECT();
            rs.next();
            if (rs.getInt(1) == 1){
                retorno = true;
            }
        }
        catch (SQLException SQLEe) {

            LogAdmin.setMessage(db + ": " + Language.getWord("ERR_RS")
                    + " " + SQLEe.getMessage(), ServerConstants.ERROR);
        }
        catch (SQLNotFoundException SQLNFEe) {

            LogAdmin.setMessage(db + ": " + SQLNFEe.getMessage(), ServerConstants.ERROR);

        }
        catch (SQLBadArgumentsException SQLBAEe) {
            
            String err = SQLBAEe.getMessage();
            LogAdmin.setMessage(err, ServerConstants.ERROR);
        }
        
        StatementsClosingHandler.close(rs);
        sql.closeStatement();
        
        return retorno;
    }
}