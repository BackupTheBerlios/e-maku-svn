package com.kazak.smi.server.control;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

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
public class UserLogin {
    
    private Element data;
    private int userLevel;
    private Integer	uid;
    private String	login;
    private String	names;
    private String	email;
    private Boolean	admin;
    private Boolean audit;
    private Integer gid;
    private String	ip;
    private boolean validIp;
    private String psName;
    private String gName;
    
    public String getLogin() {
        return login;
    }
    public UserLogin(Element data){
    	this.data = data;
    }
    
    public boolean valid() {
    	
	    login = data.getChild("login").getValue();
	    String password = data.getChild("password").getValue();
	    ip = data.getChild("ip").getValue();
	    boolean validate = data.getChild("validate")!=null ? true : false ;
	    LogWriter.write("INFO: Inicio de autenticación para el usuario {"+login+"} con la clave {"+password+"}");
	    QueryRunner runQuery = null;
	    ResultSet rs = null;
	    int count = 0;
		try {
			runQuery = new QueryRunner("SEL0023",new String[]{login,password});
			rs = runQuery.runSELECT();
		    count = rs.next() ? rs.getInt(1) : 0;
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CloseSQL.close(rs);
			runQuery.closeStatement();
		}
	    if (count==1) {
    		
	    	try {
				runQuery = new QueryRunner("SEL0025",new String[]{login,login});
				rs = runQuery.runSELECT();
				if (rs.next()) {
					uid 	= rs.getInt(1);
					login	= rs.getString(2);
					names	= rs.getString(3);
					email	= rs.getString(4);
					admin	= rs.getBoolean(5);
					audit	= rs.getBoolean(6);
					gid		= rs.getInt(7);
					validIp	= rs.getBoolean(8);
					psName	= rs.getString(9);
					String ipPv = rs.getString(11);
					gName	= rs.getString(12);
					
					if ( validIp && (!ip.equals(ipPv))) {
						LogWriter.write("La ip {"+ip+"} no esta autorizada para el usuario " +login);
						return false;
					}
					if (validate) {
		    			if (admin) {
		    				LogWriter.write("INFO: Usuario Administrador autenticado {"+login+"} desde la ip "+ ip);
		    				userLevel = 1;
				    		return true;
		    			}
		    			else if (audit) {
		    				LogWriter.write("INFO: Auditor Autenticado {"+login+"} desde la ip "+ ip);
			    			userLevel = 2;
				    		return true;
		    			}
					}
					else {
		    			LogWriter.write("INFO: Colocador Autenticado {"+login+"} desde la ip "+ ip);
		    			userLevel = 3;
			    		return true;
					}
				}
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				CloseSQL.close(rs);
				runQuery.closeStatement();
			}
			
	    }
	    LogWriter.write(
	    		"INFO: Acceso denegado a {"+login+"} desde la ip "+ ip + 
	    		" ingresando como " + (validate ? "Administrador/Auditor" :"Colocador"));
	    return false;
    }
    
	public int getUserLevel() {
		return userLevel;
	}
	public String getEmail() {
		return email;
	}
	public Integer getGid() {
		return gid;
	}
	public String getNames() {
		return names;
	}
	public Integer getUid() {
		return uid;
	}
	public String getIp() {
		return ip;
	}
	public Boolean getAdmin() {
		return admin;
	}
	public Boolean getAudit() {
		return audit;
	}
	public String getPsName() {
		return psName;
	}
	public String getGName() {
		return gName;
	}
}