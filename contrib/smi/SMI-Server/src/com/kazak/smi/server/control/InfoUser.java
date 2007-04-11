package com.kazak.smi.server.control;


public class InfoUser extends Thread {
	
    /*private boolean loged = false;
    private String login;
    private String password;
    
    private SocketChannel sock;
    
    
    private ByteArrayOutputStream buffTmp;
    
    public void setFrom(String[] data) {
    	login    = data[0];
    	password = data[1];
    	email    = data[3];
    	admin    = Boolean.parseBoolean(data[4]);
    	audit    = Boolean.parseBoolean(data[5]);
    	gid      = Integer.parseInt(data[6]);
    }
    
    
    
    public void setSocket(SocketChannel sock) {
        this.sock = sock;
        buffTmp = new ByteArrayOutputStream();
    }
    
    public SocketChannel getSocket() {
    	return sock;
    }
    public void run() {
        try {
            Thread.sleep(5000);
            if (!isLoged()) {
                UsersCache.removeSocketForUser(sock);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

  
	
    public boolean isLoged() {
        return loged;
    }

    
    public void setLoged() {
        this.loged = true;
    }

    
    public String getLogin() {
        return login;
    }

    
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
	
	public boolean validIp(String ip) {
		Set<String > keys = pointSales.keySet();
		boolean retorno = false;
		if (isAdmin() || isAudit()) {
			return true;
		}
		for (String key : keys) {
			InfoPointSale ifp = pointSales.get(key);
			boolean valid = ifp.getValidIp();
			if (valid) {
				if(ifp.getIp().equals(ip) ) {
					retorno=true;
					break;
				}
			}
			else  {
				retorno =  true;
				break;
			}
		}
		return retorno;
	}
	
	
	
	
	
	public int getIDGroup(String name) {
		Set<String > keys = pointSales.keySet();
		for (String key : keys) {
			InfoPointSale ifp = pointSales.get(key);
			if (ifp.getGroupname().equals(name)) {
				return Integer.parseInt(ifp.getGroupid());
			}
		}
		return -1;
	}

	

	public void addPointSale(Vector<String[]> arrWs) {
		for (String[] arrPs :arrWs) {
			String code = arrPs[1];
			InfoPointSale ifp = new InfoPointSale();
			ifp.setCode(code);
			ifp.setValidIp(Boolean.parseBoolean(arrPs[2]));
			pointSales.put(code,ifp);
		}
	}

	
	
	public Collection<InfoPointSale> getPointSales() {
		return pointSales.values();
	}*/
}