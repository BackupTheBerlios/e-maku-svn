package common.gui.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import common.gui.forms.GenericForm;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * GenericDataFiller.java Creado el 27-ene-2005
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
 * Esta clase se encarga de hacer una consulta y retornar sus valores en
 * los objetos referenciados, deacuerdo a los parametros recibidos
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class GenericDataFiller extends Thread {

    private GenericForm GFforma;
    private boolean ok_search;
    private String sql;
    private String[] args;
    private String arg;
    private XMLTextField[] XMLTFout=null;
    private String[] new_args = null;
    private String namebutton;
    private Vector Vfields=null;
    
    private Object[] obj;
    private Class[] clase;
    private String[] method;
    
    public GenericDataFiller(GenericForm GFforma,String namebutton,boolean ok_search,
            		 String sql,String[] args,String arg,Vector Vfields) {
		this.GFforma=GFforma;
		this.namebutton=namebutton;
		this.ok_search=ok_search;
		this.sql=sql;
		this.args=args;
		this.arg=arg;
		this.Vfields=Vfields;
    }
    
    public GenericDataFiller(GenericForm GFforma,String sql,String arg,XMLTextField[] XMLTFout) {
		this.GFforma=GFforma;
		this.sql=sql;
		this.arg=arg;
        this.XMLTFout=XMLTFout;
    }
    
    public GenericDataFiller(GenericForm GFforma,String namebutton,boolean ok_search,
            		 String sql,String[] args,String arg,XMLTextField[] XMLTFout) {
        this.GFforma=GFforma;
        this.namebutton=namebutton;
        this.ok_search=ok_search;
        this.sql=sql;
        this.args=args;
        this.arg=arg;
        this.XMLTFout=XMLTFout;
        
        
    }
    
    public GenericDataFiller(GenericForm GFforma,String namebutton,boolean ok_search,
   		 String sql,String[] args,String arg,Object[] obj,Class[] clase,String[] method) {
		this.GFforma=GFforma;
		this.namebutton=namebutton;
		this.ok_search=ok_search;
		this.sql=sql;
		this.args=args;
		this.arg=arg;
		this.obj=obj;
		this.clase=clase;
		this.method=method;
    }
    
    public void run() {
        searchQuery();
    }
    
    public boolean searchQuery() {
        try {
        	int size =0;
        	if (arg!=null) {
        		size++;
        	}
        	if (args!=null) {
        		size+=args.length;
        	}
        	new_args = new String[size];
            int ind=0;        	
            if (size>0) {
	            Document doc = null;
	            if (args!=null) {
	                for (;ind<args.length;ind++)
	                    new_args[ind]=args[ind];
	            }
	            if (arg!=null){
	            	new_args[ind] = arg;
	            }
	            ind=0;
	            doc = STResultSet.getResultSetST(sql,new_args);
	            
	            if (doc==null)
	                return false;
	            
	            Iterator i = doc.getRootElement().getChildren("row").iterator();
	            int row = doc.getRootElement().getChildren("row").size();
	            
                boolean enable_button=false;
                

                if (row>0) {
                    if (ok_search )
                        enable_button=false;
                    else
                        enable_button=true;
		            while (i.hasNext()) {
		                Element e = (Element) i.next();
	                    Iterator j = e.getChildren().iterator();
	                    int k=0;
	                    while (j.hasNext()) {
	                        Element f = (Element)j.next();
	                        setData(f.getValue(),k);
	                        k++;
	                    }
		            }
                }
                else {
                    if (ok_search) {
                        enable_button=true;
                    }
                    else {
                        enable_button=false;
                    }
                    setClean();
                }
                if (namebutton!=null)
                    GFforma.setEnabledButton(namebutton, enable_button);
            }
            else {
                if (namebutton!=null)
                    GFforma.setEnabledButton(namebutton, false);
                return false;
            }
        }
        catch (STException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void setClean() {
        if (XMLTFout!=null) {
            for (int i=0;i<XMLTFout.length;i++) {
                XMLTFout[i].setText("");
            }
        }
        else if (Vfields!=null){
            for (int i=1;i<Vfields.size();i++) {
                ((XMLTextField)Vfields.get(i)).setText("");
            }
        }
        else {
            for (int i=0;i<obj.length;i++) {
                setDataObj("",i);
            }
        }
    }
    
    private void setData(String value,int index) {
        if (XMLTFout!=null) {
            XMLTFout[index].setText(value);
        }
        else if (Vfields!=null){
        	try {
        		((XMLTextField)Vfields.get(index+1)).setText(value);
        	}catch (ArrayIndexOutOfBoundsException AIOOBEe) {
        		((XMLTextField)Vfields.get(index)).setText(value);
        	}
        }
        else {
            setDataObj(value,index);
        }
    }
    
    private void setDataObj(String value,int index) {
        Method meth;
        
        try {
            meth = clase[index].getMethod(method[index], new Class[]{String.class});
            meth.invoke(obj, new Object[]{(Object)value});
        }
        catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}



