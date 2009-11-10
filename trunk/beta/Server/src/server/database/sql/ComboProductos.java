package server.database.sql;

public class ComboProductos {

    int idProdServ=0;
    int cantidad=0;

    public ComboProductos(int idProdServ,int cantidad) {
    	this.idProdServ=idProdServ;
    	this.cantidad=cantidad;
    }

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getIdProdServ() {
		return idProdServ;
	}

	public void setIdProdServ(int idProdServ) {
		this.idProdServ = idProdServ;
	}
    
}
