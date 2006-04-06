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
 * LoadToolbarXML.java
 *
 * JButtonXML.java
 *
 * Created on 4 de abril de 2004, 17:24
 */

package client.gui.toolbarXML;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;

import client.control.ACPFormEvent;
import client.control.ACPFormListener;
import client.control.ACPHandler;
import client.gui.components.MainWindow;
import client.miscelanea.ClientConst;
import common.gui.formas.GenericForm;

/**
 * Clase encargada de generar los JButton y la manipulaci?n de los eventos de cada
 * uno de ellos
 */

public class JButtonXML extends JButton implements ActionListener,ACPFormListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 886572059145569723L;
	private String ClassName;
    private String method;
    private String transaction;
    private boolean activo;
    private Object[] ArgConstructor = null;
    private Class[] TypeArgConstructor = null;

    /**
     * Como esta clase es una implementacion de ActionListener, se aprovecha esta propiedad
     * para crear de si misma los eventos de cada JMenuItem
     */
    
    public JButtonXML() {
        this.addActionListener(this);
        ACPHandler.addACPFormListener(this);
    }
    
    /**
     * Este metodo asigna la propiedad si el boton puede ser desactivado o no
     */
    
    public void setActivo(boolean activo) {
        this.activo=activo;
    }
    /**
     * Este metodo retorna si el boton puede ser desactivado o no
     */
    
    public boolean getActivo() {
        return activo;
    }
    
    /**
     * Este metodo se encarga de almacenar el nombre de la clase que se instanciara en el evento
     * del boton. Todo lo relacionado con los botones se maneja de forma abstracta
     */
    
    public void setClassName(String ClassName) {
        this.ClassName=ClassName;
    }
    
    /**
     * Este metodo almacenara el metodo a ejecutarce en caso de que exista, una vez instanciada
     * la clase
     */
    
    public void setMethod(String method) {
        this.method=method;
    }
    
    /**
     * Este metodo se encarga de almacenar el arreglo de Argumentos para el constructor a instanciarse
     */
    
    public void setArgConstructor(Object[] ArgConstructor) {
        this.ArgConstructor=ArgConstructor;
    }
    
    /**
     * Este metodo se encarga de almacenar el arreglo de Tipos de Argumentos para el constructor
     */
    
    public void setTypeArgConstructor(Class[] TypeArgConstructor) {
        this.TypeArgConstructor=TypeArgConstructor;
    }
    
    /**
     * Metodo encargado de ejecutar el evento de accion
     */
    
    public void actionPerformed(ActionEvent e) {
    	if (ClassName!=null) {
	        try {
	            Class cls = Class.forName(ClassName);
	            if (method==null) {
	                if (TypeArgConstructor!=null)
	                    validarArgumentos();
	                
	                Constructor cons = cls.getConstructor(TypeArgConstructor);
	                cons.newInstance(ArgConstructor);
	            }
	            else {
	                Method meth = cls.getMethod(method,new Class[]{});
	                meth.invoke(cls.newInstance(),new Object[]{});
	            }
	        }
	        catch(ClassNotFoundException CNFEe) {
	            CNFEe.printStackTrace();
	            System.out.println("Exception : "+CNFEe.getMessage());
	        }
	        catch(NoSuchMethodException NSMEe) {
	            NSMEe.printStackTrace();
	            System.out.println("Exception : "+NSMEe.getMessage());
	        }
	        catch(InstantiationException IEe) {
	            IEe.printStackTrace();
	            System.out.println("Exception : "+IEe.getMessage());
	        }
	        catch(IllegalAccessException IAEe) {
	            IAEe.printStackTrace();
	            System.out.println("Exception : "+IAEe.getMessage());
	        }
	        catch(InvocationTargetException ITEe) {
	            ITEe.printStackTrace();
	            System.out.println("Exception: "+ITEe.getMessage());
	        }
    	}
    	else {
    		 Dimension size = new Dimension();
             size.height = MainWindow.getAncho();
             size.width = MainWindow.getAlto();
             new GenericForm(
            		 ACPHandler.getDocForm(transaction),
            		 MainWindow.getJDPanel(),
            		 ClientConst.KeyClient,
            		 size,
                     transaction);
    	}
    }

    /**
     * Metodo encargado de reorganizar los argumentos dependiendo de su tipo, el tipo
     * se lo obtiene del arreglo TypeArgConstructor
     */
    
    private void validarArgumentos() {
        /**
         * Por el momento solo manejo argumentos de tipo entero, una vez existan de otro tipo
         * se formatearan adicionando sentencias if
         */
        for (int i=0;i<TypeArgConstructor.length;i++) {
            if (TypeArgConstructor[i].getName().equals("int"))
                ArgConstructor[i]=new Integer(ArgConstructor[i].toString());
            if (TypeArgConstructor[i].getName().equals("String"))
                ArgConstructor[i]=ArgConstructor[i].toString();
        }        
    }

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
		setEnabled(false);
	}

	public void arriveACPForm(ACPFormEvent e) {
		if (e.getTransaction().equals(transaction)) {
			setEnabled(true);
		}
	}
}
