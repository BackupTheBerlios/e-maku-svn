package com.kazak.smi.server.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.kazak.smi.server.database.connection.ConnectionsPool;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;


/**
 * RunQuery.java Creado el 7-jul-2004
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
 * Esta clase es la encargada de ejecutar las sentencias Select
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class QueryRunner extends Element {

	private static final long serialVersionUID = -5369073295276204778L;
    private String sqlCode;
    private Statement st;
    private String sql;
    
    /**
     * Este constructor inicializa los objetos sql y bd nesesario para ejecutar
     * la consulta
     * 
     * @param bd Base de datos
     * @param sqlCode Codigo Sentencia SQL
     */
    public QueryRunner() {}
    public QueryRunner(String sqlCode) throws SQLNotFoundException, SQLBadArgumentsException {
        this.sqlCode = sqlCode;
        sql = SQLInstructions.getSentence(sqlCode);
    }
    
    public QueryRunner(String sqlCode,String[] args) throws SQLNotFoundException, SQLBadArgumentsException{
        this.sqlCode = sqlCode;
        sql = SQLInstructions.getSentence(sqlCode,args);
    }
    
    public QueryRunner(String sqlCode,String sql) {
        this.sqlCode = sqlCode;
        this.sql = sql;
    }
    
    
    /**
     * Metodo encargado de ejcutar la sentencia sql
     * 
     * @return retorna el resultado de la consulta
     */
    public ResultSet runSELECT() throws SQLException{
        st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
//        LogWriter.write("SENTENCIA SQL :: "+sql);
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

    public boolean runSQL(String cod_sql,String[] args) 
    throws SQLException,SQLNotFoundException, SQLBadArgumentsException {
        this.sqlCode = cod_sql;
        sql = SQLInstructions.getSentence(cod_sql,args);
        st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
//        LogWriter.write("SENTENCIA SQL :: "+sql);
        boolean status = st.execute(sql);
        st.close();
        return status;
    }

    public boolean runSQL(String[] args) 
    throws SQLException,SQLNotFoundException, SQLBadArgumentsException {
        sql = SQLInstructions.getSentence(sqlCode,args);
        st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
        LogWriter.write("SENTENCIA SQL :: "+sql);
        boolean status = st.execute(sql);
        st.close();
        return status;
        
    }
    /**
     * Metodo encargado de ejcutar la sentencia sql diferente 
     * a un Select
     * 
     * @return retorna TRUE si la transaccion es excitosa y FALSE si no
     */
    public boolean runSQL() throws SQLException{
        
        st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
        //System.out.println("SENTENCIA SQL :: "+sql);
        boolean status = st.execute(sql);
        st.close();
        return status;
    }
    
    /**
     * Este metodo se encarga de cerrar el Statement una vez el
     * la informacion del Resultset haya sido interpretada
     */
    public void closeStatement() {
        CloseSQL.close(st);
    }
    
    public void setAutoCommit(boolean autoCommit) {
        try {
        	ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void commit() {
        try {
        	ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).commit();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } 
    
    public void rollback() {
        try {
        	ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).rollback();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}