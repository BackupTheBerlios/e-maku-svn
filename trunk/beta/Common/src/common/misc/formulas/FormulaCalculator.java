package common.misc.formulas;

import java.math.BigDecimal;
import java.util.Vector;
/**
 * FormulaCalculator.java Creado el 20-jun-2005
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
 * Esta clase se encarga de efectuar operaciones recibidas en una cadena
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class FormulaCalculator {

    public static synchronized Object operar(String formula) throws NumberFormatException,ArrayIndexOutOfBoundsException {
    	
        Vector <Object>cadena = new Vector<Object>();
        Vector <Object>pila = new Vector<Object>();
        Vector <Object>expresion = new Vector<Object>();
        String numero="";
        for (int i=0;i<formula.length();i++) {
            if ((formula.charAt(i)>=48 && formula.charAt(i)<=57) || formula.charAt(i)==69 ||formula.charAt(i)==46 || formula.charAt(i)==44) {
                numero+=formula.substring(i,i+1);
            }
            else {
                Object ultimo = new Double(0);
                if (cadena.size()>0) {
                    ultimo = cadena.get(cadena.size()-1);
                }
                else if (formula.substring(i+1,i+2).charAt(0)>=48 && formula.substring(i+1,i+2).charAt(0)<=57) {
                    ultimo="+";
                }
                
                
                if (numero.equals("") && formula.charAt(i)==45 && ultimo instanceof String && !ultimo.equals(")")) {
                    numero+=formula.substring(i,i+1);
                }
                else {
	                if (!numero.equals("")) {
	                    cadena.addElement(new Double(Double.parseDouble(numero)));
	                }
	                cadena.addElement(formula.substring(i,i+1));
	                numero="";
                }
            }
        }
        
        if (!numero.equals("")) {
            cadena.addElement(new Double(numero));
        }
        
        cadena.addElement(")");
        /*
         * La formula esta desglozada, ahora se procede a convertirla en una expresion posfija
         */
        pila.addElement("(");
        
        for (int i=0;i<cadena.size();i++) {
            
            
            if (cadena.get(i) instanceof String) {
                String op = (String)cadena.get(i);
                String cabPila = (String)pila.get(pila.size()-1);
                if (op.equals("(")) {
                    pila.addElement(cadena.get(i));
                }
                else if (op.equals(")")) {
                    for (int j=pila.size()-1;!pila.get(j).toString().equals("(");j--) {
                        String tmpOp = pila.get(j).toString();
                        expresion.addElement(tmpOp);
                        pila.removeElementAt(j);
                    }
                    /*
                     * Se elimina el primer objeto de la pila que en este caso seria un (
                     */
                    pila.removeElementAt(pila.size()-1);
                }
                /*
                 * Si no es ( o ) entonces es un operador
                 */
                else {
                    if ((op.equals("+") || op.equals("-")) && !cabPila.equals("(")) {
                        expresion.addElement(cabPila);
                        pila.removeElementAt(pila.size()-1);
                    }
                    else if ((op.equals("*") || op.equals("/")) && 
                            (!cabPila.equals("(") && !cabPila.equals("+") && !cabPila.equals("-"))) {
                        expresion.addElement(cabPila);
                        pila.removeElementAt(pila.size()-1);
                    }
                    else if (op.equals("&") && cabPila.equals("&")) {
                        expresion.addElement(cabPila);
                        pila.removeElementAt(pila.size()-1);
                    }
                    pila.addElement(op);
                }
            }
            else {
                expresion.addElement(cadena.get(i));
            }
        }
        
        /*
         * Una vez la ecuacion se encuentra en notacion posfija se procede a calcularla
         */
        for (int i=0;i<expresion.size();i++) {
            Object elemento = expresion.get(i);
            if (elemento instanceof Double) {
                pila.addElement(elemento);
            }
            else {
                double a = ((Double)pila.get(pila.size()-2)).doubleValue();
                double b = ((Double)pila.get(pila.size()-1)).doubleValue();
                double c = 0;
                switch (elemento.toString().charAt(0)) {
                    case 43:
                        c=a+b;
                        break;
                    case 45:
                        c=a-b;
                        break;
                    case 42:
                        c=a*b;
                        break;
                    case 47:
                        c=a/b;
                        break;
                    case 38:
                        c=Math.pow(a, b);
                        break;
                }
                pila.remove(pila.size()-1);
                pila.remove(pila.size()-1);
                pila.addElement(new Double(c));
            }
        }
        //System.out.println("pila: "+pila.get(0));
        BigDecimal bDecimal = new BigDecimal(((Double)pila.get(0)).doubleValue());
        bDecimal = bDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
        return Double.valueOf(bDecimal.doubleValue());
    }
    
}
