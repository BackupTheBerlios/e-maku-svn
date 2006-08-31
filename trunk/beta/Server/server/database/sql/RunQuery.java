package server.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.database.connection.PoolConexiones;

import org.jdom.Element;


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

public class RunQuery extends Element {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5369073295276204778L;
	private String bd;
    private String cod_sql;
    private Statement st;
    private String sql;
    
    /**
     * Este constructor inicializa los objetos sql y bd nesesario para ejecutar
     * la consulta
     * 
     * @param bd Base de datos
     * @param cod_sql Codigo Sentencia SQL
     */
    
    public RunQuery(String bd, String cod_sql) throws SQLNotFoundException, SQLBadArgumentsException{
        this.bd = bd;
        this.cod_sql = cod_sql;
        sql = InstruccionesSQL.getSentencia(bd,cod_sql);
    }
    
    public RunQuery(String bd, String cod_sql,String[] args) throws SQLNotFoundException, SQLBadArgumentsException{
        this.bd = bd;
        this.cod_sql = cod_sql;
        sql = InstruccionesSQL.getSentencia(bd,cod_sql,args);
    }
    
    public RunQuery(String bd,String cache,String sql) {
        this.bd=bd;
        this.cod_sql=cache;
        this.sql=sql;
    }
    
    public RunQuery(String bd) {
        this.bd=bd;
    }
    
    /**
     * Metodo encargado de ejcutar la sentencia sql
     * 
     * @return retorna el resultado de la consulta
     */
    public ResultSet ejecutarSELECT() throws SQLException{
        st = PoolConexiones.getConnection(bd).createStatement();
//        System.out.println("SENTENCIA SQL :: "+sql);
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

    public boolean ejecutarSQL(String cod_sql,String[] args) 
    throws SQLException,SQLNotFoundException, SQLBadArgumentsException {
        this.cod_sql = cod_sql;
        sql = InstruccionesSQL.getSentencia(bd,cod_sql,args);
        st = PoolConexiones.getConnection(bd).createStatement();
//        System.out.println("SENTENCIA SQL :: "+sql);
        boolean status = st.execute(sql);
        st.close();
        return status;
    }

    public boolean ejecutarSQL(String[] args) 
    throws SQLException,SQLNotFoundException, SQLBadArgumentsException {
        sql = InstruccionesSQL.getSentencia(bd,cod_sql,args);
        st = PoolConexiones.getConnection(bd).createStatement();
        System.out.println("SENTENCIA SQL :: "+sql);
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
    public boolean ejecutarSQL() throws SQLException{
        
        st = PoolConexiones.getConnection(bd).createStatement();
   //     System.out.println("SENTENCIA SQL :: "+sql);
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
            PoolConexiones.getConnection(bd).setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void commit() {
        try {
            PoolConexiones.getConnection(bd).commit();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } 
    
    public void rollback() {
        try {
            PoolConexiones.getConnection(bd).rollback();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}