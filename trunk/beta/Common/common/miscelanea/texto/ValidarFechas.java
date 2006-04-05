/**
 * Este archivo es parte de E-Maku (http://comunidad.qhatu.net)
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
 *
 *
 * @author  Luis Felipe Hernandez Z.
 * @see e-mail felipe@qhatu.net
 * ValidarFechas.java
 *
 * Created on 9 de abril de 2004, 13:31
 */

package common.miscelanea.texto;

/**
 *
 * @author  felipe
 */
public class ValidarFechas {
    
    private String fecha;
    /** Creates a new instance of ValidarFechas */
    public ValidarFechas(String fecha) {
        this.fecha=fecha;
    }

    public boolean valid_date() {
        try {
            int anio = Integer.parseInt(fecha.substring(0,4));
            String sep1 = fecha.substring(4,5);
            int mes = Integer.parseInt(fecha.substring(5,7));
            String sep2 = fecha.substring(7,8);
            int dia = Integer.parseInt(fecha.substring(8,10));
            String esp = fecha.substring(10,11);
            int hora = Integer.parseInt(fecha.substring(11,13));
            String sep3 = fecha.substring(13,14);
            int minuto = Integer.parseInt(fecha.substring(14,16));
            String sep4 = fecha.substring(16,17);
            int segundo = Integer.parseInt(fecha.substring(17,19));
            
            return valida_sep(sep1,sep2,sep3,sep4,esp,anio,mes,dia,hora,minuto,segundo);
        }
        catch(NumberFormatException NFEe) {
            return false;
        }
        catch(StringIndexOutOfBoundsException SIOOBEe) {
            return false;
        }
    }

    private boolean valida_sep(String sep1,String sep2,String sep3,String sep4,String esp,int anio,int mes,int dia,int hora,int minuto,int segundo) {
        if (sep1.equals(sep2) && sep2.equals("-"))
            if (esp.equals(" "))
                if (sep3.equals(sep4) && sep4.equals(":"))
                    return valida_fecha(anio,mes,dia,hora,minuto,segundo);
                else
                    return false;
            else
                return false;
        else
            return false;
    }
    
    private boolean valida_fecha(int anio,int mes,int dia,int hora,int minuto,int segundo) {
        if (anio>1950 && anio<2050)
            if (mes>0 && mes<13)
                return validar_dia(anio,mes,dia,hora,minuto,segundo);
            else
                return false;
        else
            return false;
    }
    
    private boolean validar_dia(int anio,int mes,int dia,int hora,int minuto,int segundo) {
        if ((mes == 1    ||
            mes == 3    ||
            mes == 5    ||
            mes == 7    ||
            mes == 8    ||
            mes == 10   ||
            mes == 12)  && 
            (dia>0 && dia<32))
            return validar_hora(hora,minuto,segundo);
        
        if ((mes == 4   ||
             mes == 6   ||
             mes == 9   ||
             mes ==11)  &&
             (dia>0 && dia<31))
             return validar_hora(hora,minuto,segundo);
        
        if (mes == 2)
            if ((anio%4)==0) { 
                if (dia>0 && dia < 30)
                    return validar_hora(hora,minuto,segundo);
            }
            else {
                if (dia>0 && dia < 29)
                    return validar_hora(hora,minuto,segundo);
            }
        
        return false;
    }

    private boolean validar_hora(int hora,int minuto,int segundo) {
        if (hora>=0 && hora<24)
            if (minuto>=0 && minuto<60)
                if (segundo>=0 && segundo<60)
                    return true;
                else
                    return false;
            else
                return false;
        else
            return false;
    }
    
}
