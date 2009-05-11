package server.database.sql;

public class InfoInventario {
    
    double pcosto=0;
    double saldo=0;
    double vsaldo=0;
    
    public InfoInventario(double pcosto,double saldo,double vsaldo) {
        this.pcosto=pcosto;
        this.saldo=saldo;
        this.vsaldo=vsaldo;
    }
    
    public double getPcosto() {
        return pcosto;
    }
    public void setPcosto(double pcosto) {
        this.pcosto = pcosto;
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    public double getVsaldo() {
        return vsaldo;
    }
    public void setVsaldo(double vsaldo) {
        this.vsaldo = vsaldo;
    }
}