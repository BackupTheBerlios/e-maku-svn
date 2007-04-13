package com.kazak.smi.server.comunications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;
/**
 * SocketServer.java Creado el 21-jul-2004
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
 * Esta clase es la encargada de abrir los sockets servidores para atender las
 * peticiones de el un PA o un PC. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class SocketServer {

    /**
     * Este vector almacena todos los canales de las conexiones activas de los
     * clientes, es necesario en caso de que se quiera mandar un paquete a todas
     * las conexiones activas.
     */

    private static Hashtable <SocketChannel,InfoSocket>Hchannelclients = new Hashtable<SocketChannel,InfoSocket>();
    private static ServerSocketChannel SSCcanal1 = null;
    private static int SocketsCount = 0;


    /**
     * Desde el constructor de esta clase se trabaja la administracion de
     * conexiones cliente
     */

    public SocketServer() throws IOException{
        try {

            SSCcanal1 = ServerSocketChannel.open();
            SSCcanal1.configureBlocking(false);
            ServerSocket SSclient = SSCcanal1.socket();


            SSclient.bind(new InetSocketAddress(ConfigFile.getPort()));

            Selector selector = Selector.open();
            SSCcanal1.register(selector, SelectionKey.OP_ACCEPT);

            LogWriter.write(Language.getWord("SOCKET_SERVER_OPEN") + " "+ ConfigFile.getPort());

            while (true) {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterador = selector.selectedKeys().iterator();
                    while (iterador.hasNext()) {
                        SelectionKey clave = (SelectionKey) iterador.next();
                        try {
                            if (clave.isAcceptable()) {
                                ServerSocketChannel server = (ServerSocketChannel) clave.channel();

                                SocketChannel canalsocket = server.accept();
                                canalsocket.configureBlocking(false);
                                canalsocket.register(selector,SelectionKey.OP_READ);
                                Hchannelclients.put(canalsocket, new InfoSocket(canalsocket.socket()));
                                LogWriter.write(Language.getWord("NEW_SOCKET_CLIENT")
                                            + " "
                                            + canalsocket.socket()
                                            + " "
                                            + (setIncrementSocketsCount()));
                                Thread.sleep(50);

                            } else if (clave.isReadable()) {
                                SocketChannel canalsocket = (SocketChannel) clave.channel();
                                PackageToXML packageXML2 = new PackageToXML(canalsocket);
                                packageXML2.start();
                                Thread.sleep(70);
                            }
                            iterador.remove();
                        }
                        catch (CancelledKeyException e) {
                            Thread.sleep(50);
                        }
                    }
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este metodo sirve para saber si una conexion ha sido autenticada
     * @param sock
     *            Socket al que se valida
     * @return Retorna true si el socket a sido autenticado, de lo contrario
     *         retorna false
     */
    public static boolean isLoged(SocketChannel sock) {
        return Hchannelclients.get(sock).isLoged();
    }
    
    /**
     * Este metodo retorna 
     * @param sock
     * @return algo
     */
    public static String getLoging(SocketChannel sock) {
        return Hchannelclients.get(sock).getLogin();
    }
    
    /**
     * Este metodo remueve una coneccion (socket) de la
     * hash de conecciones.
     * @param sock Socket que se quiere remover.
     * @throws IOException
     */
    public static void removeSock(SocketChannel sock) throws IOException {
        setDecrementSocketsCount();
        sock.close();
        Hchannelclients.remove(sock);
    }

    public static ByteArrayOutputStream getBufferTmp(SocketChannel sock) {
        return Hchannelclients.get(sock).getBuffTmp();
    }

    public static void setBufferTmp(SocketChannel sock,ByteArrayOutputStream buffTmp) {
        Hchannelclients.get(sock).setBuffTmp(buffTmp);
    }
    
    /**
     * Este metodo retorna la base de datos un socket
     * @param sock Socket del cual se quiere obtener la informacion
     * @return El nombre de la base de datos
     */
    public static String getBd(SocketChannel sock){
    	return Hchannelclients.get(sock).getBd();
    	
    }

    /**
     * Este metodo actualiza el valor de las conexiones logeadas
     * @param sock
     * @param bd
     */

    public static void setLogin(SocketChannel sock, String bd, String login) {
        Hchannelclients.get(sock).setLoged();
        Hchannelclients.get(sock).setBd(bd);
        Hchannelclients.get(sock).setLogin(login);
    }

    /**
     * Este metodo retorna unicamente el numero de socket's
     * conectados
     * @return algo
     */
    public static int getSocketsCount() {
        return SocketsCount;
    }

    /**
     * Este metodo Incrementa el contador de socket's
     * conectados 
     * @return El numero de socket's conectados.
     */
    public static int setIncrementSocketsCount() {
        return ++SocketsCount;
    }

    /**
     * Este metodo decrementa el contador de socket's
     * conectados 
     * @return El numero de socket's conectados.
     */
    public static int setDecrementSocketsCount() {
        return --SocketsCount;
    }
    public static Hashtable getHchannelclients() {
        return Hchannelclients;
    }

    public static String getCompanyNameKey(SocketChannel sock) {
        return "K-"+ getBd(sock) + "-company";
    }
    
    public static String getCompanyIDKey(SocketChannel sock) {
        return "K-"+ getBd(sock) + "-companyID";    
    }
    
    public static InfoSocket getInfoSocket (SocketChannel sock) {
    	return Hchannelclients.get(sock);
    }
    
    public static InfoSocket getInfoSocket(String login) {
    	for (InfoSocket ifs : Hchannelclients.values()) {
    		if (ifs.getLogin().equals(login)) {
    			return ifs;
    		}
    	}
    	return null;
    }
    public static InfoSocket getInstaceOfInfoSocket() {
    	return new InfoSocket();
    }
    /**
     * Esta clase sirve como valor para guardar
     * la conexiones en la hash de sockets
     */
    public static class InfoSocket extends Thread {

        private boolean loged = false;
        private String bd;
        private String login;
        private Socket sock;
        private String names;
        private int uid;
        private String email;
        private boolean admin;
        private boolean audit;
        private int gid;
        private String currIp;
        private String password;
        private String psName;
        private String gName;
        
    	public InfoSocket() {}
    	public String getCurrIp() {
    		return currIp;
    	}

    	public void setCurrIp(String currIp) {
    		this.currIp = currIp;
    	}

    	public int getGid() {
    		return gid;
    	}

    	public void setGid(int gid) {
    		this.gid = gid;
    	}
    	public boolean isAudit() {
    		return audit;
    	}

    	public void setAudit(boolean audit) {
    		this.audit = audit;
    	}

    	public String getNames() {
    		return names;
    	}

    	public void setNames(String names) {
    		this.names = names;
    	}
    	
        public int getUid() {
    		return uid;
    	}

    	public void setUid(int idUser) {
    		this.uid = idUser;
    	}

    	public boolean isAdmin() {
    		return admin;
    	}

    	public void setAdmin(boolean admin) {
    		this.admin = admin;
    	}

    	public String getEmail() {
    		return email;
    	}

    	public void setEmail(String email) {
    		this.email = email;
    	}
        private ByteArrayOutputStream buffTmp;
        
        /**
         * @param sock
         */
        public InfoSocket(Socket sock) {
            this.sock = sock;
            buffTmp = new ByteArrayOutputStream();
            start();
        }

        /**
         * 
         */
        public void run() {
            try {
                Thread.sleep(10000);
                if (!isLoged()) {
                    SocketServer.removeSock(sock.getChannel());
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Este metodo se utiliza para verificar si una
         * coneccion ha sido autenticada.
         * @return <code><b>true</b></code> si la conexion esta autenticada,
         * de lo contrario <code><b>false</b></code> 
         */
        public boolean isLoged() {
            return loged;
        }

        /**
         * Este metodo se encarga de pasar el estado
         * de no autenticado a autenticado.
         */
        public void setLoged() {
            this.loged = true;
        }

        /**
         * Este metodo se encarga de retornar la
         * base de datos a la que la coneccion(socket) hace
         * referencia.
         * @return Nombre de la base de datos
         */
        public String getBd() {
            return bd;
        }

        /**
         * Este metodo establece la base de datos
         * para la coneccion(socket).
         * @param bd Nombre de la base de datos.
         */
        public void setBd(String bd) {
            this.bd = bd;
        }

        /**
         * Este metodo se encarga de retornar
         * el nombre del usuario referente a la coneccion(socket).
         * @return Nombre de usuario
         */
        public String getLogin() {
            return login;
        }

        /**
         * Este metodo se encarga de retornar el nombre
         * de usuario de una coneccion(socket).
         * @param login Nombre de usuario.
         */
        public void setLogin(String login) {
            this.login = login;
        }
        
        public ByteArrayOutputStream getBuffTmp() {
            return buffTmp;
        }
        
        public void setBuffTmp(ByteArrayOutputStream buffTmp) {
            this.buffTmp = buffTmp;
        }
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public Socket getSock() {
			return sock;
		}
		public String getPsName() {
			return psName;
		}
		public void setPsName(String psName) {
			this.psName = psName;
		}
		public String getGName() {
			return gName;
		}
		public void setGName(String name) {
			gName = name;
		}
    }
    
	public static Vector<InfoSocket> getAllClients(int gidInt) {
		Vector<InfoSocket> vusers = new Vector<InfoSocket>();
		RunQuery runQuery = null;
	    ResultSet rs = null;
	    
	    for (InfoSocket ifs : Hchannelclients.values()) {    
            if (ifs.getGid()==gidInt) {
                vusers.add(ifs);
            }
        }
	    
		try {
			runQuery = new RunQuery("SEL0027");
			rs = runQuery.runSELECT();
			while(rs.next()) {
				InfoSocket ifuFrom = SocketServer.getInstaceOfInfoSocket();
				ifuFrom.setUid(rs.getInt(1));
				ifuFrom.setLogin(rs.getString(2));
				ifuFrom.setNames(rs.getString(3));
				ifuFrom.setEmail(rs.getString(4));
				ifuFrom.setAdmin(rs.getBoolean(5));
				ifuFrom.setAudit(rs.getBoolean(6));
				ifuFrom.setGid(rs.getInt(7));
				ifuFrom.setPsName(rs.getString(9));
				ifuFrom.setGName(rs.getString(12));
				
				if (!containsInfoSocket(vusers, ifuFrom)) {
					boolean cont = ifuFrom.getGid()==gidInt ? true : false;
					if (ifuFrom.getGid()==gidInt) {
						vusers.add(ifuFrom);
					}
					else if (cont) {
						vusers.add(ifuFrom);
					}
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
		return vusers;
	}
	
	private static boolean containsInfoSocket(Vector<InfoSocket> vusers,InfoSocket ifs) {
		for (InfoSocket rifs : vusers) {
			if (ifs.getLogin().equals(rifs.getLogin())) {
				return true;
			}
		}
		return false;
	}
	
	public static Vector<InfoSocket> getAllClients(String toName) {
		Vector<InfoSocket> vusers = new Vector<InfoSocket>();
		RunQuery runQuery = null;
	    ResultSet rs = null;
	    
        for (InfoSocket ifs : Hchannelclients.values()) {    
            boolean cont = toName.equals(ifs.getGName());
            if (toName.equals(ifs.getLogin())) {
                vusers.add(ifs);
            }
            else if (cont) {
                vusers.add(ifs);
            }
        }
        
		try {
			runQuery = new RunQuery("SEL0027");
			rs = runQuery.runSELECT();
			while(rs.next()) {
				InfoSocket ifuFrom = SocketServer.getInstaceOfInfoSocket();
				ifuFrom.setUid(rs.getInt(1));
				ifuFrom.setLogin(rs.getString(2));
				ifuFrom.setNames(rs.getString(3));
				ifuFrom.setEmail(rs.getString(4));
				ifuFrom.setAdmin(rs.getBoolean(5));
				ifuFrom.setAudit(rs.getBoolean(6));
				ifuFrom.setGid(rs.getInt(7));
				ifuFrom.setPsName(rs.getString(9));
				ifuFrom.setGName(rs.getString(12));
				if (!containsInfoSocket(vusers, ifuFrom)) {
					boolean cont = ifuFrom.getGName().equals(toName);
					if (ifuFrom.getLogin().equals(toName)) {
						vusers.add(ifuFrom);
					}
					else if (cont) {
						vusers.add(ifuFrom);
					}
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
		return vusers;
	}
}