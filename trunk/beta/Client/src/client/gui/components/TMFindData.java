package client.gui.components;

import static client.gui.components.Formula.BEANSHELL;
import static client.gui.components.Formula.BEANSHELLNQ;
import static client.gui.components.Formula.SIMPLE;
import static client.gui.components.Formula.SIMPLENQ;
import static client.gui.components.Formula.SUPER;
import static client.gui.components.Formula.SUPERBEANNQ;
import static client.gui.components.Formula.SUPERNQ;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdom.Document;
import org.jdom.Element;

import bsh.EvalError;
import client.Run;

import common.gui.components.ChangeValueEvent;
import common.gui.components.ChangeValueListener;
import common.gui.components.ErrorDataException;
import common.gui.components.VoidPackageException;
import common.gui.forms.ChangeExternalValueEvent;
import common.gui.forms.ChangeExternalValueListener;
import common.gui.forms.FinishEvent;
import common.gui.forms.GenericForm;
import common.gui.forms.InitiateFinishListener;
import common.gui.forms.NotFoundComponentException;
import common.misc.formulas.FormulaCalculator;
import common.misc.language.Language;
import common.transactions.STException;
import common.transactions.STResultSet;

/**
 * TMFindData.java Creado el 06-abr-2005
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
 * Esta clase se encarga de crear genear TableModel para la clase TableFindData
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class TMFindData extends AbstractTableModel 
implements ChangeValueListener,InitiateFinishListener, ChangeExternalValueListener{

	private static final long serialVersionUID = 337157273387332474L;
	private GenericForm GFforma;
    private String sqlCode;
    private boolean initSQL;
    private int rows;
    private ArrayList formulas;
    private HashMap exportTotalCols;
    private int[] totales;
    private ArgsTableFindData[] ATFDargs;
    private Vector<Vector<Object>> VdataRows;
    private boolean errFormula;
    private Hashtable<String,Double> totalCol;
    private Hashtable externalValues;
    private int valideLink = -1;
    private Vector<TableTotalListener> tableTotalListener = new Vector<TableTotalListener>();
    private Vector<String> deleteLink;
    private int keyLink;
    private Vector impValues;
    private HashMap<String,Integer> keysExports;
    private boolean updateQuery;
    private HashMap importTotalCol;
    private boolean loadingQuery = false;
    private int tagDataColumn = -1;
    private int currentIndex = 0;
    private boolean isInitQuery;
    
    public TMFindData(GenericForm GFforma,
            		  String sqlCode,
            		  int rows,
            		  ArrayList formulas,
            		  HashMap exportTotalCols,
            		  HashMap importTotalCols,
            		  Vector impValues,
            		  int[] totales,
            		  Hashtable externalValues,
            		  ArgsTableFindData[] ATFDargs) {
        Cargar(GFforma, sqlCode, rows, formulas,exportTotalCols,importTotalCols,impValues,totales,externalValues,ATFDargs);
        
    }

    public TMFindData(GenericForm GFforma,
			  		  String sqlCode,
			  		  Document doc,
			  		  ArrayList formulas,
			  		  HashMap exportTotalCols,
            		  int[] totales,
            		  Hashtable externalValues,
			  		  ArgsTableFindData[] ATFDargs) {
    	this.isInitQuery=true;
		this.GFforma=GFforma;
		this.sqlCode=sqlCode;
		this.formulas=formulas;
		this.exportTotalCols=exportTotalCols;
        this.totales=totales;
        this.externalValues=externalValues;
		this.ATFDargs=ATFDargs;
		this.initSQL=true;
		VdataRows = new Vector<Vector<Object>>();
        totalCol = new Hashtable<String,Double>();
        importTotalCol 	= new HashMap<String,String>();

		List Lrows = doc.getRootElement().getChildren("row");
        Iterator Irows = Lrows.iterator();
        GFforma.addInitiateFinishListener(this);
        rows = Lrows.size();
        
        /* Cargando informacion */
        int o=0;
        for (int i=0;Irows.hasNext();i++) {
			o++;
			Element Erow = (Element) Irows.next();
			List Lcol = Erow.getChildren();
			Vector<Object> col = new Vector<Object>();
			for (int j=0;j<ATFDargs.length;j++) {
			    col.add(addCols(j,Lcol));
			}
			/* Se adiciona la nueva fila al vector de filas */
			VdataRows.add(col);
			
        }
        totalizar();
    }
    
    public TMFindData(
    		GenericForm GFforma, 
    		String sqlCode, 
    		int rows, ArrayList formulas,
    		HashMap exportTotalCols,
    		HashMap importTotalCols,
    		Vector impValues, 
    		int[] totales, 
    		Hashtable externalValues, 
    		ArgsTableFindData[] ATFDargs, 
    		int valideLink, int keyLink) {
    	
	    	this.valideLink = valideLink;
	    	this.keyLink = keyLink;
	    	Cargar(GFforma, sqlCode, rows, formulas,exportTotalCols,importTotalCols,impValues,totales,externalValues,ATFDargs);
	    	
    }
    
    private void Cargar(GenericForm GFforma,
  		  String sqlCode,
		  int rows,
		  ArrayList formulas,
		  HashMap exportTotalCols,
		  HashMap importTotalCols,
		  Vector impValues,
		  int[] totales,
		  Hashtable externalValues,
		  ArgsTableFindData[] ATFDargs) {
    	
		this.GFforma=GFforma;
        this.sqlCode=sqlCode;
        this.rows=rows;
        this.formulas=formulas;
        this.exportTotalCols=exportTotalCols;
        this.totales=totales;
        this.externalValues=externalValues;
        this.ATFDargs=ATFDargs;
        this.impValues = impValues;
        VdataRows = new Vector<Vector<Object>>();
        totalCol = new Hashtable<String,Double>();
        keysExports = new HashMap<String,Integer>();
        deleteLink = new Vector<String>();
        this.importTotalCol = importTotalCols;
        GFforma.addInitiateFinishListener(this);
        
        for (int i=0;i<rows;i++) {
            Vector<Object> newRows = new Vector<Object>();
            for (int j=0;j<ATFDargs.length;j++) {
                /* Se define los objetos deacuerdo a su definicion de los
                   argumentos */
            		//newRows.addElement(null);
            		newRows.addElement(ATFDargs[j].getTypeDate());
            }
            /* Se adiciona la nueva fila al vector de filas */
            VdataRows.addElement(newRows);
        }
        for (int j=0;j<ATFDargs.length;j++) {
	        	if (ATFDargs[j].isExporValue()) {
	        		keysExports.put(ATFDargs[j].getExportValue(),Integer.valueOf(j));
	        	}	
        }
    }
	/**
     * Metodo encargado de levantar los eventos externos, 
     */
    private void loadExternalEvents() {
        Enumeration values = externalValues.elements();
        Hashtable<String,Object> cargados = new Hashtable<String,Object>();
        try {
	        while (values.hasMoreElements()) {
	            String driver = (String)values.nextElement();
	            // este if evita cargar el oyente de un componente mas de una vez
	            if (!cargados.containsKey(driver)) {
		            GFforma.invokeMethod(driver,
							  "addChangeValueListener",
							  new Class[]{ChangeValueListener.class},
							  new Object[]{this});
		            cargados.put(driver, new Object());
	           }
	        }
        }
        catch(InvocationTargetException ITEe) {
            ITEe.printStackTrace();
        }
        catch (NotFoundComponentException NFCEe) {
            NFCEe.printStackTrace();
        }

    }
    
    /**
     * Retorna el numero de Columnas de la tabla
     */

    public int getColumnCount() {
        return ATFDargs.length;
    }

    /**
     * Retorna el nombre de una columna especifica
     */
    
    public String getColumnName(int colIndex) {
        String columnName = Language.getWord(ATFDargs[colIndex].getName());
        if ("".equals(columnName)) {
            return ATFDargs[colIndex].getName();
        }
        else {
            return columnName;
        }
    }
    
    /**
     * Retorna el numero de filas de la tabla
     */
    public int getRowCount() {
        return rows;
    }

    /**
     * Retorna el valor de una celda especifica
     */
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return VdataRows.elementAt(rowIndex).get(columnIndex);
    }

    /**
     * Se verifica si la columna es editable
     */
    
    public synchronized boolean isCellEditable(int rowIndex, int colIndex) {
    	
    	if (rowIndex>=0) {
    		if ((ATFDargs[colIndex].getType().equals("COMBOSQL")) && (rowIndex > currentIndex)) {
    			return false;
    		}
    		int val = 0;
    		if (valideLink > 0) {
    			try {
    				val = Integer.parseInt((String)getValueAt(rowIndex,valideLink));
    			} catch (NumberFormatException NFEe) {
    				val = 0;
    			}
        	}
    		if (rowIndex == 0) {
    			if (ATFDargs[colIndex].isEditable() && val==0) {
    	            return true;
    	        }
    	        return false;
    		} 
    		else {
    			//if (getValueAt(rowIndex-1,colIndex).equals("") && val==0) {
    			if (val>0) {
    					return false;
    			}
    			else {
    				if (ATFDargs[colIndex].isEditable()  && val==0) {
        	            return true;
        	        }
        	        return false;
    			}
    		}
    	}
   		return false;
    }

    /**
     * Actualiza el valor de una celda especifica
     */
    
    public synchronized void setValueAt(Object value, int rowIndex, final int colIndex) {
        /*
         * Esta clase se encarga de solicitar la busqueda al Servidor de transacciones
         * y cargarla a sus respectivos campos
         */
        
        class Searching extends Thread {
		    
            private Object value;
            private int rowIndex;
            
            public Searching(Object value,int rowIndex) {
                this.value=value;
                this.rowIndex=rowIndex;
            }
            
            public void run() {    
		        try {
		        	
		            /* Generando la consulta  */
		        	int NroImps = impValues.size();
		        	String [] argsQuery = new String[NroImps+1];
		        	int ind = 0;
		        	argsQuery[ind] = (String)value;
		        	ind++;
		        	
		        	for ( ; ind < argsQuery.length ; ind++) {
		        		argsQuery[ind] = GFforma.getExteralValuesString(impValues.get(ind-1));
		        	}
		        	
		            Document Dquery = STResultSet.getResultSetST(sqlCode,argsQuery);
		            
		            Iterator Irows = Dquery.getRootElement().getChildren("row").iterator();
		            Element Ecol = (Element)Irows.next();
		            List Lcol = Ecol.getChildren();
		            
		            /* Cargando informacion a las celdas correspondientes */
		            
		            for (int i=0;i<ATFDargs.length;i++) {
		                if (ATFDargs[i].getOrderQuery()!=-1) {
		                    Element newValue = (Element)Lcol.get(ATFDargs[i].getOrderQuery());
		                    
		                    /* Que mierdero todo esto */

		                    if (ATFDargs[i].getType().equals("BOOLEAN")) {
		                        if (newValue.getValue().equals("t") ||
		                            newValue.getValue().equals("T") ||    
		                            newValue.getValue().equals("true") ||    
		                            newValue.getValue().equals("TRUE") ||
		                            newValue.getValue().equals("True") ||
		                            newValue.getValue().equals("1")) {
		                            updateCells(new Boolean(true),rowIndex,i);
		                        }
		                        else {
		                            updateCells(new Boolean(false),rowIndex,i);
		                        }
		                    }
		                    else if (ATFDargs[i].getType().equals("DECIMAL")) {
			                    	BigDecimal bd = new BigDecimal(Double.parseDouble(newValue.getValue()));
			                    	bd =  bd.setScale(ATFDargs[i].getDecimals(),BigDecimal.ROUND_HALF_UP);
			                    	updateCells(bd,rowIndex,i);
		                    }
		                    else if (ATFDargs[i].getType().equals("DATE")) {
			                    	DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
			                    	Date date;
								try {
									date = formato.parse(newValue.getValue());
									updateCells(date,rowIndex,i);
								}
								catch (ParseException e) {
									updateCells("",rowIndex,i);
								}

		                    }
		                    else {
			                    	Object obj = null;
			                    	try {
		    		                    Constructor cons = ATFDargs[i].getTypeDate().getClass().getConstructor(new Class[]{String.class});
		    		                    obj = cons.newInstance(new Object[]{newValue.getValue()});
			                    	}
			                    	catch (InvocationTargetException ITEe) {
			                    		obj = ATFDargs[i].getDefaultValue();
			                    	}
			                    	updateCells(obj,rowIndex,i);
		                    }
                        }
		            }
		            
                    calcular(rowIndex,colIndex);	
                    totalizar();
		        }
		        catch (STException STe) {
		        		STe.printStackTrace();
		            message("ERR_QUERY",STe.getMessage());
		        }
		        catch (NoSuchElementException NSEe) {
			        	updateCells(ATFDargs[0].getTypeDate(),rowIndex,0);
			        	updateCells(ATFDargs[2].getTypeDate(),rowIndex,2);
			        	message("ERR_NOCODE");
		        }
		        
		        /* Y ni que decir de este mundo de excepciones */
		        
		        catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (InstantiationException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (SecurityException e1) {
                    e1.printStackTrace();
                }
                catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
            }
        }
        /*
         * Por defecto cuando se necesita una consulta, esta se hace cuando se ingresa datos
         * en la columna uno
         */
        updateCells(value,rowIndex,colIndex);
        if (colIndex==0 && !"".equals(sqlCode) && !"".equals(value)) {
        	
	        	for (int j=0;j<ATFDargs.length;j++) {
	            	if (ATFDargs[j].getDefaultValue().doubleValue() != 0.00 && !updateQuery) {
	            		updateCells(ATFDargs[j].getDefaultValue(),rowIndex,j);
	            	}
	        	}
	        	updateQuery=false;
	        	Object valArg = value;
	        	if (ATFDargs[colIndex].getType().equals("COMBOSQL")) {
	    			StringTokenizer stk = new StringTokenizer((String) value," ");
	    			String tok="";
	    			while(true) {
	    				try {
	    					tok=stk.nextToken();
	    				}
	    				catch(NoSuchElementException NSEe) {
	    					break;
	    				}
	    			}
	                valArg = tok;
	        	}
	        	new Searching(valArg,rowIndex).start();
        }
        else {
	        calcular(rowIndex,colIndex);
	       	totalizar();
        }
        /*if (ATFDargs[colIndex].getType().equals("DATE")) {
	        	try {
	        		java.sql.Date.valueOf((String)value);
	        		/*Format formatter = new SimpleDateFormat("yyyy-mm-dd");
	        		value = formatter.format(value);*/
	        	/*}
	        	catch (IllegalArgumentException IAEe) {
	        		value = "";
	        	}
           	updateCells(value,rowIndex,colIndex);
        }*/
    }
    
    private void calcular(int rowIndex,int colIndex) {
    		calcular(rowIndex,colIndex,true);
    }

    /**
     * Este metodo se encarga de hace los calculos correspondientes, dependiendo de las
     * formulas asignadas
     * @param rowIndex almacena la fila sobre la que se calculara la informacion
     */
    
    private synchronized void calcular(int rowIndex,int colIndex,boolean initQuery) {
	    	boolean calc = false;
	    	if (rowIndex>=0) {
	    		if (rowIndex>0) {
		    		if (!"".equals(getValueAt(rowIndex-1,0)))
		    			calc=true;
	    		}
	    		if (rowIndex==0)
	    			calc= true;
	    	}
    		
        if (!errFormula && calc) {
	        	/* Procesando formulas con Clases Locales*/
	        	if (formulas!=null) {
	        		try {
		        		for (int i=0;i<formulas.size();i++) {
		                    Formula formula = (Formula)formulas.get(i);
		                    String var = formula.getFormula();
		                    if ( getColIndex(var) != colIndex ) {
			                    switch(formula.getType()) {
					            case SIMPLE:
					            		procesarFormulas(var,rowIndex,true);
					            		break;
					            case BEANSHELL:
					            		procesarFormulas(var,rowIndex,false);
					            		break;
					            case SUPER:
						            	for (int j = 0 ; j < rows && !"".equals(getValueAt(j,0)); j++ ) {
								        	procesarFormulas(var,j,false);
						            	}
						            	break;
					            case SIMPLENQ:
						            	if (initQuery) {
						            		procesarFormulas(var,rowIndex,true);
						            	}
						            	break;
					            case BEANSHELLNQ:
						            	if (initQuery) {
						            		procesarFormulas(var,rowIndex,false);
						            	}
						            	break;
					            case SUPERNQ:
						            	if (initQuery) {
							            	for (int j = 0 ; j < rows && !"".equals(getValueAt(j,0)); j++ ) {
									        	procesarFormulas(var,j,true);
									        }
						            	}
						            	break;
					            case SUPERBEANNQ:
						            	if (initQuery) {
						            		for (int j = 0 ; j < rows && !"".equals(getValueAt(j,0)); j++ ) {
									        	procesarFormulas(var,j,false);
						            		}
						            	}
						            	break;
			                    }
		                    }
		        		}
	        		}
	        		catch(NumberFormatException NFEe) {
	        				NFEe.printStackTrace();
	        		    message("ERR_FORMULA",NFEe.getMessage());
	        		    errFormula=true;
	        		}
	        		catch(ArrayIndexOutOfBoundsException AIOOBEe) {
	        				AIOOBEe.printStackTrace();
	        		    message("ERR_FORMULA");
	        		    errFormula=true;
	        		} catch (EvalError e) {
	        				e.printStackTrace();
	        		    message("ERR_FORMULA");
	        		    errFormula=true;
	        		}


	        	}
        }
        initQuery = true;
    }

    
	/**
	 * Este metodo se encarga de recorrer el contenido del vector de formulas
	 * para luego ser operadas.
	 * @param array vector de formulas
	 * @param tipoFormula define si la formula sera operada por BeanShell o por Clases Locales
	 * @param rowIndex define la fila a procesar
	 */    
    private void procesarFormulas(String var,int rowIndex,boolean tipoFormula) 
    throws NumberFormatException,ArrayIndexOutOfBoundsException,EvalError {
	    	Hashtable<String,Object> valueOld = new Hashtable<String,Object>();
        /* Recorriendo cada formula */
        String key=var.substring(0,1);
        String newVar=reemplazarFormula(var,rowIndex,valueOld);
    		Object result = null;
    		int col= getColIndex(key);
        if (tipoFormula) {
        		result = FormulaCalculator.operar(newVar);	
        }
        else {
        		result = Run.shellScript.eval(newVar);
        }
        if ("INTEGER".equals(ATFDargs[col].getType())) {
	        	Integer resultado;
	        	if (result instanceof Double) {
	        		resultado = new Integer(((Double)result).intValue());
	        	}else {
	        		resultado = (Integer)result;
	        	}
	        	Integer val = new Integer(resultado.intValue());
	        	updateCells(val,rowIndex,col);
	        	valueOld.put(key,val);
        }
        else if ("DECIMAL".equals(ATFDargs[col].getType())) {
	        	Object resultado;
	        	try {
	        		resultado = (Double) result;
	        		BigDecimal bd = new BigDecimal(((Double)resultado).doubleValue());
	            	bd =  bd.setScale(ATFDargs[col].getDecimals(),BigDecimal.ROUND_HALF_UP);
	            	updateCells(bd,rowIndex,col);
	            	valueOld.put(key,bd);
	        	} catch (ClassCastException CCEe) {
	        		resultado = (Integer) result;
	            	updateCells(resultado,rowIndex,col);
	            	valueOld.put(key,resultado);
	        	}
        }
        else if ("STRING".equals(ATFDargs[col].getType()) ||
        		 "COMBOSQL".equals(ATFDargs[col].getType()) ||
        		 "DATASEARCH".equals(ATFDargs[col].getTypeDate())) {
	        	Object resultado;
	    		resultado =  result;
	        	updateCells(resultado,rowIndex,col);
	        	valueOld.put(key,resultado);
        }
        //totalizar(col);
    }
    
    private int getColIndex(String key) {
    	 	int col=0;
    	 	if ((key.charAt(0)>=65 && key.charAt(0)<=90) || (key.charAt(0)>=97 && key.charAt(0)<=122)) {
			
    	 		/* cuando la letra es mayuscula */
             if (key.charAt(0)<=90) {
                 col = key.charAt(0)-65;
             }
             /* cuando es minuscula */
             else {
                 col = key.charAt(0)-97;
             }
         }
         return col;
    }
    
    private String reemplazarFormula(String var, int rowIndex, Hashtable valueOld) {
    		String newVar="";
        String key=var.substring(0,1);
    		for (int j=2;j<var.length();j++) {
            /*
             * se numerara las columnas en letras con un rango maximo de 10 columnas
             */
            
            if ((var.charAt(j)>=65 && var.charAt(j)<=90) || (var.charAt(j)>=97 && var.charAt(j)<=122)) {
                /*
                 * Este try/catch se utiliza para verificar que la columna introducida no existe en la tabla, 
                 * si es asi, es porque podria ser correspondiente a un valor de un componente externo,
                 * entonces se pasa a traer el valor de dicho componente, si el valor no se encuentra en
                 * la hash externalValue, se procede a lanzar un mensaje de error confirmando la no existencia
                 * de la columna introducida
                 * 
                 */
                try {
	                /*
	                 * Se verifica si el valor no fue anteriormente calculado, en caso de que si, entonces
	                 * se almacena en la formula el resultado del calculo anterior, si no se trae el valor
	                 * de la celda correspondiente
	                 */
	                if (valueOld!=null && valueOld.containsKey(key)) {
	                    newVar+=((Double)valueOld.get(key)).doubleValue();
	                }
	                else {
		                int col;
		                /*
		                 * Se verifica si en el contenido existe una funcion
		                 * de totalizacion
		                 */
		                if (var.substring(2,var.length()).startsWith("ROUND")) {
		                	String args = var.substring(6,var.length()-1);
		                	int sep = args.indexOf(',');
		                	String arg1  = args.substring(0,sep);
		                	String arg2  = args.substring(sep+1,args.length());
		                	newVar+= round(arg1, arg2, rowIndex);
		                	j+=var.length();
		                }
		                else if (var.length()>=j+5 && var.substring(j,j+3).toUpperCase().equals("SUM")) {
		                	newVar+=sum(var.substring(j+4,j+5));
		                	j+=5;
		                }
		                /* A�adido metodo equals para las formulas */
		                else if (var.length()>=j+8 && var.substring(j,j+6).equals("equals")) {
		                	int colind = getColIndex(var.substring(j+7,j+8));
		                	newVar+="equals(\""+getValueAt(rowIndex,colind)+"\")";
		                	j+=8;
		                }
		                /* Aqui se evalua si contiene palabras reservadas */
		                else if (var.length()>=j+3 && var.substring(j,j+3).equals("int")) {
		                	newVar+=var.substring(j,j+3);
		                	j+=2;
		                }
		                else if (var.length()>=j+6 && var.substring(j,j+6).equals("double")) {
		                	newVar+=var.substring(j,j+6);
		                	j+=5;
		                }
		                /* A�adida palabra reservada null, pendiente para analizar */
		                else if (var.length()>=j+4 && var.substring(j,j+4).equals("null")) {
		                	newVar+="\"\"";
		                	j+=3;
		                }
		                else {
		                	/* cuando la letra es mayuscula */
			                
			                if (var.charAt(j)<=90) {
			                    col = var.charAt(j)-65;
			                }
			                /* cuando es minuscula */
			                else {
			                    col = var.charAt(j)-97;
			                }
			                
			                /* Codigo pendiente para analizar */
			                String txt = getValueAt(rowIndex,col).toString();
			                if ("STRING".equals(ATFDargs[col].getType()) || 
			                	"COMBOSQL".equals(ATFDargs[col].getType())) {
			                	newVar+="".equals(txt)?"\"\"":"\""+txt+"\"";
			                }
			                else {
			                	newVar += txt;
			                }
			                /**/
		                }
	                }
                }
		        catch(ArrayIndexOutOfBoundsException AIOOBEe) {
		            String keyExternalValue = var.substring(j,j+1);
		            if (externalValues.containsKey(keyExternalValue)) {
		                try {
			    		    Double val = (Double)GFforma.invokeMethod((String)externalValues.get(keyExternalValue),
			    		            								  "getDoubleValue",
			    		            								  new Class[]{String.class},
			    		            								  new Object[]{keyExternalValue});
		    		        newVar+=val.doubleValue();
		                }
		                catch(InvocationTargetException ITEe) {
				            message("ERR_FORMULA");
				            ITEe.printStackTrace();
				            errFormula=true;
		                }
		                catch(NullPointerException NPEe) {
				            message("ERR_FORMULA");
				            NPEe.printStackTrace();
				            errFormula=true;
		                }
		                catch (NotFoundComponentException NFCEe) {
		                    NFCEe.printStackTrace();
		                }

		            }
		            else if (importTotalCol.containsKey(keyExternalValue.toUpperCase())){
		            	newVar+=GFforma.getExteralValues(importTotalCol.get(keyExternalValue.toUpperCase()));
		            }
		            else {
			            message("ERR_FORMULA");
			            errFormula=true;
		            }
		        }
            }

            else {
                newVar+=var.substring(j,j+1);
            }
        }
    	return newVar;
    }
    
    /**
     * Este metodo se encarga de totalizar las columnas parametrizadas
     * para ser totalizadas
     */

    public synchronized void totalizar() {
        try {
            if (totales!=null) {
		        for (int i=0;i<totales.length;i++) {
		            double tmpTotal=0;
		            for (int j=0;j<getRowCount() && getValueAt(j,0)!=null && !getValueAt(j,0).equals("") && getValueAt(j,totales[i])!=null;j++) {
		                tmpTotal+=Double.parseDouble(getValueAt(j,totales[i]).toString());
		            }
		            totalCol.remove(totales[i]+"");
		            totalCol.put(new String(totales[i]+""),new Double(tmpTotal)); 
		        }
		        
		        /*
	             * Se verifica si el valor de la columna totalizada sera exportado
	             */
		        Iterator it = exportTotalCols.keySet().iterator();
		        while (it.hasNext()) {
		        	String key = (String)it.next();
		        	String newVar="";
		            for (int j=0;j<key.length();j++) {
		                if ((key.charAt(j)>=65 && key.charAt(j)<=90) || (key.charAt(j)>=97 && key.charAt(j)<=122)) {
		                    newVar+=getTotalCol(key.substring(j,j+1));
		                }
		                else {
		                    newVar+=key.substring(j,j+1);
		                }
		            }
		            double val = ((Double)(FormulaCalculator.operar(newVar))).doubleValue();
		            GFforma.setExternalValues(exportTotalCols.get(key.toUpperCase()),val);
		        }
	            notificando();
            }
        }
        catch(NullPointerException NPEe) {
        	NPEe.printStackTrace();
            message("ERR_TOTAL");
            errFormula=true;
        }
    }
    
    /**
     * Este metodo se encarga de totalizar las columnas parametrizadas
     * para ser totalizadas, despues de operar una de sus celdas
     */

    public double sum(String key) {
        try {
        	int col = getColIndex(key);
            double tmpTotal=0;
            for (int j=0;j<getRowCount() && !getValueAt(j,0).equals("");j++) {
                tmpTotal+=Double.parseDouble(getValueAt(j,col).toString());
            }
            return tmpTotal;
        }
        catch(NullPointerException NPEe) {
            message("ERR_TOTAL");
            errFormula=true;
        }
		return 0.0;
    }
    
    public double round(String value, String aprox, int rowIndex) {
    	Hashtable<String,Object> valueOld = new Hashtable<String,Object>();
    	String var = reemplazarFormula(value, rowIndex, valueOld);
    	
    	Double val = null;
		try {
			val = (Double)GFforma.invokeMethod((String)externalValues.get(aprox),
					  "getDoubleValue",
					  new Class[]{String.class},
					  new Object[]{aprox});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NotFoundComponentException e) {
			e.printStackTrace();
		}
    	
    	Double result = (Double) FormulaCalculator.operar(var);
    	double decene = result % 100 ;
    	double daprox = val!= null ? val.doubleValue() : 0.00;
    	double adjust = Math.rint(decene/daprox);
    	int intvalue = (int) (result / 100) ;
    	return daprox > 0 ?  (intvalue + ((adjust * daprox) / 100)) * 100: result;
    }

    /**
     * Este metodo retorna el total de una columna
     * @param col contiene la columna de la cual se quiere retornar el total
     * @return retorna el total de la columna especificada.
     */
    public double getTotalCol(String col) {
        try {
	        int colIndex=0;
	        if (col.charAt(0)<=90) {
	            colIndex = col.charAt(0)-65;
	        }
	        /* cuando es minuscula */
	        else {
	            colIndex = col.charAt(0)-97;
	        }
	        return ((Double)totalCol.get(colIndex+"")).doubleValue();
        }
        catch(NullPointerException NPEe) {
            return 0;
        }
    }
    
    public synchronized void addTotalEventListener(TableTotalListener listener ) {
        tableTotalListener.add(listener);
        if(isInitQuery) {
        	notificando();
        }
    }

    public synchronized void removeTotalEventListener(TableTotalListener listener ) {
        tableTotalListener.remove(listener);
    }

    public synchronized void notificando() {
    	TableTotalEvent event = new TableTotalEvent(this);
        Vector lista;
        lista = (Vector)tableTotalListener.clone();
        for (int i=0; i<lista.size();i++) {
            TableTotalListener listener = (TableTotalListener) lista.elementAt(i);
            listener.totalColEvent(event);
        }
    }
    
    /**
     * Metodo encargado de actualizar el valor de una celda
     * @param value valor actualizado
     * @param rowIndex fila 
     * @param colIndex columna
     */
    public synchronized void updateCells(Object value,int rowIndex,int colIndex) {
		VdataRows.get(rowIndex).set(colIndex,value);
		currentIndex += (rowIndex < currentIndex) ? 0: 1;
        class RunInternalQuery extends Thread {
        	
        		int rowIndex;
        		int colIndex;
        		Object value;
        		
        		public RunInternalQuery(int row, int col, Object value) {
        			this.rowIndex = row;
        			this.colIndex = col;
        			this.value = value;
        		}
        		
        		public void run() {
        			
        	        /*if (ATFDargs[colIndex].isExporValue() && !ATFDargs[colIndex].getType().equals("COMBOSQL")) {
        	        	GFforma.setExternalValues(ATFDargs[colIndex].getExportValue(),String.valueOf(value));
        	        }*/
        	        fireTableCellUpdated(rowIndex, colIndex);
        		
			        /* Aqui vamos a realizar las consultas para quien lo necesite*/
			        if(ATFDargs[colIndex].isExporValue() && value!=null) {
			        		GFforma.setExternalValues((Object)ATFDargs[colIndex].getExportValue(),value.toString());	
				        	for (int col=0;col < ATFDargs.length ; col++) {
				        		if (ATFDargs[col].isImportValues()) {
				        			
				        			String [] impVals =ATFDargs[col].getImports();
				        			String [] argsQuery = new String[impVals.length];
				        			boolean search = true;
				        			
				        			for (int args=0; args <impVals.length;args++) {
				        				
				        				int indice = ((Integer)keysExports.get(impVals[args])).intValue();
				        				String valueArg = null; 
				        				valueArg =	String.valueOf(getValueAt(rowIndex,indice));
				        				
				        				if(!"".equals(valueArg)) {
				        					
				        					if (ATFDargs[indice].getType().equals("COMBOSQL")) {
								    			StringTokenizer stk = new StringTokenizer(valueArg," ");
								    			String tok = "";
								    			while(true) {
								    				try {
								    					tok=stk.nextToken();
								    				}
								    				catch(NoSuchElementException NSEe) {
								    					break;
								    				}
								    			}
								    			argsQuery[args] = tok;
				        					}
				        					else {
				        						argsQuery[args] = valueArg;
				        					}
				        				}
				        				else {
				        					search = false;
				        					break;
				        				}
				        			}
				        			if (search) {
				        				Object obj = ATFDargs[col].getTypeDate();
				        				String argConstructor = null;
										try {
											Document Dquery = STResultSet.getResultSetST(ATFDargs[col].getSqlCombo(),argsQuery);
											Element element = Dquery.getRootElement().getChild("row");
						                    Constructor cons = ATFDargs[col].getTypeDate().getClass().getConstructor(new Class[]{String.class});
						                    argConstructor = element.getChildText("col");
					                        obj = cons.newInstance(new Object[]{argConstructor});
										} 
										catch (STException STEe) {
											STEe.printStackTrace();
										} 
										catch (NullPointerException NPEe) {
											//NPEe.printStackTrace();
										} 
										catch (InvocationTargetException ITEe) {
											ITEe.printStackTrace();
										} 
										catch (SecurityException SEe) {
					                		SEe.printStackTrace();
										} 
										catch (NoSuchMethodException NSMEe) {
											NSMEe.printStackTrace();
										} 
										catch (IllegalArgumentException IAEe) {
											IAEe.printStackTrace();
										} 
										catch (InstantiationException IEe) {
											IEe.printStackTrace();
										} 
										catch (IllegalAccessException IAEe) {
											IAEe.printStackTrace();
										}
										
										//if (argConstructor!=null) {
											VdataRows.get(rowIndex).set(col,obj);
											fireTableCellUpdated(rowIndex,col);
											/* Este codigo analiza las columnas por beanshell antes de insertarlas */
											if (formulas!=null) {
												try{
									        		for (int i=0;i<formulas.size();i++) {
									        			
									                    Formula formula = (Formula)formulas.get(i);
									                    String var = formula.getFormula();
									                    switch(formula.getType()) {
												            case BEANSHELL:
												            		procesarFormulas(var,rowIndex,false);
												            		break;
									                    }
									        		}
												}
												catch(NumberFormatException NFEe) {
														NFEe.printStackTrace();
												    message("ERR_FORMULA",NFEe.getMessage());
												    errFormula=true;
												}
												catch(ArrayIndexOutOfBoundsException AIOOBEe) {
														AIOOBEe.printStackTrace();
												    message("ERR_FORMULA");
												    errFormula=true;
												} catch (EvalError e) {
														e.printStackTrace();
												    message("ERR_FORMULA");
												    errFormula=true;
												}


											}
										//}
				        			}
				        		}
				        	}
			        }
        		}
        }
        if (loadingQuery) {
        	fireTableCellUpdated(rowIndex, colIndex);
        } else {
        	new RunInternalQuery(rowIndex,colIndex,value).start();
        }
    }

    
    /**
     * Metodo encargado de limpiar la tabla
     */
    
    public void clean() {
    	int i=0;
    	for (Vector<Object> vrow : VdataRows) {
    		for (int j=0;j<ATFDargs.length;j++) {
                /* 
                 * Esta validacion es con el fin de solo inicializar los
                 * valores que no son retornados por una consulta, cuando
                 * la tabla se despliega con una consulta inicial
                 */
                
                //if (!initSQL || ATFDargs[j].getOrderQuery()==-1 || ATFDargs[j].isClean()) {
            	if (!initSQL || ATFDargs[j].isClean()) {
            		//updateCells(ATFDargs[j].getTypeDate(),i,j);
            		vrow.set(j,ATFDargs[j].getTypeDate());
            		//vrow.set(j,null);
            		fireTableCellUpdated(i,j);
                }
    		}
    		i++;
    	}
    	currentIndex = 0;
    }
    
    /**
     * Este metodo retorna el tipo de dato de una celda
     */
	public Class<?> getColumnClass(int colIndex) {
		return ATFDargs[colIndex].getColumnClass(); 
    }
    
    private void message(String keyError) {
    	final String error = keyError;
		//try {
			SwingUtilities.invokeLater(new Runnable() {
		          public void run() {
		        	  JLabel label = new JLabel(Language.getWord(error));
		              JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
		                      label,
		                      "Error", JOptionPane.ERROR_MESSAGE);	
		          }
			});
		/*} catch (InterruptedException e) {
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
		}*/
        
    }

   
    private void message(String keyError,String exceptionMessage) {
    	final String error = keyError;
    	final String exception = exceptionMessage;
        try {
			SwingUtilities.invokeAndWait(new Runnable()
			        {
			          public void run() {
			              JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
			                      Language.getWord(error)+"\n"+exception,
			                      "Error", JOptionPane.ERROR_MESSAGE);	
			          }
			        });
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }

    /**
     * Metodo partucular utilizado para la validacion de un paquete 
     * para generar las unidades de venta
     * @return retorna un elemento para generar el paquete
     * @throws ErrorDataException
     */
    public Element getUVPackage() throws ErrorDataException {
        Element pack = new Element("package");
        int valorDef = 0;
        /*
         * Variable encargada de verificar por lo menos un factor de conversi�n
         * en el producto.
         */
        boolean factor = false;
        /*
         * Se recorre toda la tabla buscando factores de conversi�n
         */
        
        for (int i=0;i<rows; i++) {
            Element subpack = new Element("subpackage");
            
            /*
             * Si el valor del factor de conversi�n es diferente de 0 es
             * porque existe un nuevo factor de conversi�n.
             */
            if (!getValueAt(i,4).toString().equals("0.0")) {
	            for (int j=0;j<getColumnCount();j++) {
	            	/*
	            	 * Se ordena la informaci�n deacuerdo a su orden de retorno
	            	 * y se empieza a querar los subpaquetes <xml/>
	            	 */
	                for (int k=0;k<getColumnCount();k++) {
		                if (ATFDargs[k].getOrderReturn()==j) {
		                    Element field = new Element("field");
		                    field.setText(getValueAt(i, k).toString());
		                    subpack.addContent(field);
		                    factor=true;
		                }
	                }
	                
                    /*
                     * Validaci�n encargada de verificar la existencia de un solo
                     * valor por defecto
                     */
                    
                    if (j==2 && getValueAt(i, 2).toString().equals("true")) {
                        valorDef++;
                    }

	            }
	            pack.addContent(subpack);
            }
        }

        /*
         * Se verifica que existan factores de conversi�n en el producto
         */
        
        if (factor) {
        	/*
        	 * Se verifica que exista un valor por defecto en el producto
        	 */
        	
	        if (valorDef==1) {
	            return pack;
	        }
	        else {
	            throw new ErrorDataException("\n"+Language.getWord("ERR_DEFAULT"));
	        }
        }
        else {
            throw new ErrorDataException("\n"+Language.getWord("ERR_FACTOR"));
        }
    }
    
    public Element[] getMultiPackage() throws VoidPackageException {
    	 Element [] mpack = new Element[2];
    	 Element pack1 = new Element("package");
    	 Element pack2 = new Element("package");
    	 boolean primerElemento = true;
    	    
         /*
          * Este paquete almacena los id de las filas que seran eliminadas
          */
         Element Efield = null;
         for (int i=0;i<deleteLink.size();i++) {
             Element subpack = new Element("subpackage");
             Efield = new Element("field");
             if (i==0)
                 Efield.setAttribute("attribute","disableKey");

             Efield.setText(deleteLink.get(i));
             subpack.addContent(Efield);
             pack1.addContent(subpack);
         }
         mpack[0] = pack1;
         for (int i=0;i<rows; i++) {
             Element subpack = new Element("subpackage");
             if ("".equals(getValueAt(i,keyLink))) {
	             boolean valueBlank = false;
	             int j =0;
	             for (;j<getColumnCount();j++) {
	                 for (int k=0;k<getColumnCount();k++) {
	                	 if (ATFDargs[k].getOrderReturn()==j) {
	 	                    String value = getValueAt(i, k).toString();
	 	                    if (value.equals("")) {
	 	                        valueBlank=true;
	 	                        break;
	 	                    }
	 	                    Element field = new Element("field");
	 	                    if (ATFDargs[k].getType().equals("COMBOSQL")) {
	 	 	                   StringTokenizer values = new StringTokenizer(getValueAt(i, k).toString()," ");
	 		                   String tmp="";
	 		                   while (true)
	 		                       try {
	 		                           tmp = values.nextToken();
	 		                       }
	 		                   	   catch(NoSuchElementException e1) {
	 		                   	       break;
	 		                   	   }
	 		                   	   field.setText(tmp);
	 	                    }
	 	                    else {
	 	                    	field.setText(getValueAt(i, k).toString());
	 	                    }
	 	                    if (primerElemento) {
	 	                    	primerElemento = false;
	 	                    	field.setAttribute("attribute","enableKey");
	 	                    }
	 	                    subpack.addContent(field);
	 	                }
	                 }
	                 if (valueBlank) {
	                     break;
	                 }
	             }
	             if (j>0 && j<getColumnCount()) {
	            	 throw new VoidPackageException(Language.getWord("VOID_PACKAGE"));
	             }
	             
	             if (subpack.getContentSize() != 0){
	                 pack2.addContent(subpack);
	             }
             }
         }
         mpack[1] = pack2;

         return mpack;
     }
   
    /**
     * Metodo partucular utilizado para la validacion de un paquete 
     * para generar grupos de impuestos
     * @return retorna un elemento para generar el paquete
     */
    public Element getGAPackage() {
        Element pack = new Element("package");

        /*
         * La validacion de este tipo de paquetes consiste en seleccionar todos
         * los elementos los cuales la primera columna retorne un valor boleano
         * true y la segunda columna contenga un valor diferente de ""
         */
 
        Vector cols;
        int i;
        for (i=0;i<VdataRows.size();i++) {
            cols = (Vector)VdataRows.get(i);
            
            /*
             * Verificacion de que no se estan validando celdas en blanco
             */
            
            if ("".equals((String)cols.get(1))) {
                    break;
            }
            
            Element subpack = new Element("subpackage");
            Element field = new Element("field");
            boolean selection = ((Boolean)cols.get(0)).booleanValue();
            String value = cols.get(1).toString();
            if (selection && !value.equals("")) {
                field.setText(value);
                subpack.addContent(field);
                pack.addContent(subpack);
            }
        }
        
        return pack;
    }
    
    /**
     * Metodo particular utilizado para la validacion de un paquete para
     * generar asientos predefinidos        	
     * @return retorna un elemento para generar el paquete
     * @throws ErrorDataException esta excepcion se lanza en caso de que
     *         en el momento de la validacion, se vea que los datos no son
     *         consistentes para armar un elemento.
     */
    
    public Element getAPPackage() throws ErrorDataException {
        Element pack = new Element("package");
        /*
         * Para armar la estructura de este paquete, primero se debe validar
         * que no existan cuentas repetidas y que exista partida doble, esto
         * quere decir que por lo menos exista una cuenta debitandoce y una
         * acreditandoce
         */
        Vector cols;
        
        boolean[] naturaleza = new boolean[VdataRows.size()];
        String[] partidaDoble = new String[VdataRows.size()];
        
        int i;
        for (i=0;i<VdataRows.size();i++) {
            cols =VdataRows.get(i);
            /*
             * Verificacion de que no se estan validando celdas en blanco
             */
            
            if ("".equals((String)cols.get(0))) {
                    break;
            }
            
            Element subpack = new Element("subpackage");
	        for (int j=0;j<3;j++) {
	            
	            /*
	             * la columna 2 contiene el nombre de la cuenta, no es necesario
	             * para la generacion del paquete
	             */
	            
	            if (j!=1) {
		            Element field = new Element("field");
		            String value = cols.get(j).toString();
		            /*
		             * El elemento 0 contiene el # de la cuenta
		             * y el elemento 2 contiene la naturaleza
		             */
		            if (j==0) {
		                /*
		                 * Verificacion de que las cuentas no esten repetidas
		                 */
		                
		                for (int k=0;k<i;k++) {
		                    if (value.equals(partidaDoble[k])) {
		    	                throw new ErrorDataException(value);
		                    }
		                }
		                partidaDoble[i] = value;
		            }
		            else if (j==2){
		                naturaleza[i] = ((Boolean)cols.get(j)).booleanValue();
		            }
		                
		            field.setText(value);
		            subpack.addContent(field);
	            }
	        }
            pack.addContent(subpack);
        }
        
        /*
         * Verificacion si las cuentas hacen una partida doble, o no
         *
        
        boolean debito = false;
        boolean credito = false;
        for (int k=0;k<i ;k++) {
            if (naturaleza[k]) {
                debito=true;
            }
            else { 
                credito=true;
            }
            if (debito && credito) {
                break;
            }
        }
        
        if (debito && credito) {
            return pack;
        }
        else
            throw new ErrorDataException("\n"+Language.getWord("ERR_PARTIDA"));
        */
        return pack;
    }
 
    /**
     * Metodo particular para retornar un paquete para el componente
     * asignacion de asientos<package/>
     * @return retorna un <package/>
     */
    public Element getAAPackage() {
        Element pack = new Element("package");
        for (int i=0;i<rows; i++) {
            Element subpack = new Element("subpackage");
            boolean valueBlank = false;
            for (int j=0;j<getColumnCount();j++) {
                for (int k=0;k<getColumnCount();k++) {
	                if (ATFDargs[k].getOrderReturn()==j) {
	                    String value = getValueAt(i, k).toString();
	                    if (value.equals("")) {
	                        valueBlank=true;
	                        break;
	                    }
	                    Element field = new Element("field");
	                    if (ATFDargs[k].getType().equals("COMBOSQL")) {
	 	                   StringTokenizer values = new StringTokenizer(getValueAt(i, k).toString()," ");
		                   String tmp="";
		                   while (true)
		                       try {
		                           tmp = values.nextToken();
		                       }
		                   	   catch(NoSuchElementException e1) {
		                   	       break;
		                   	   }
		                   	   field.setText(tmp);

	                    }
	                    else {
	                    	field.setText(getValueAt(i, k).toString());
	                    }
	                    subpack.addContent(field);
	                }
                }
                if (valueBlank) {
                    break;
                }
            }
            if (!valueBlank) {
            	pack.addContent(subpack);
            }
        }
        return pack;
    }
    
    public void ValidPackage (Element args) throws VoidPackageException {
    	Iterator it = args.getChildren().iterator();
    	Vector<String> vector = new Vector<String>();
    	while (it.hasNext()) {
    		Element element = (Element)it.next();
    		String formula = element.getValue();
    		if ("beanshell".equals(element.getAttributeValue("attribute"))) {
    			vector.add(formula);
    		}
    	}
    	for (int j = 0; j< vector.size(); j++) {
    		String formula = vector.get(j);
    		int count = 0;
	    	for (int i=0; i < rows && !getValueAt(i,0).equals("") ; i++) {
	    		try {
	    			String script = reemplazarFormula(formula,i,null);
					Integer result = (Integer)Run.shellScript.eval(script);
					if (result.intValue() == 0) {
						count ++;
					}
				} catch (EvalError e) {
					System.out.println("Capturada EvalError mensaje: " + e.getMessage());
				}
	    	}
	    	if (count ==0 ) {
	    		throw new VoidPackageException("Sin Mensaje Aun: pero es en El ValidPackage");
	    	}
    	}
    }
    
    
    /**
     * Metodo generico para retornar un <package/> 
     * @return retorna un <package/>
     */
    public Element getPackage() {
        Element pack = new Element("package");
        for (int i=0;i<rows; i++) {
	            Element subpack = new Element("subpackage");
	            StructureSubPackage subpackage = new StructureSubPackage();
	            for (int j=0;j<getColumnCount();j++) {
	            	subpackage = makeSubPackage(i,j,subpack);
	                if (subpackage.isValidPackage()) {
	                    break;
	                }    	
	            }
	            if (subpackage.isValidPackage()) {
	                break;
	            }
	            pack.addContent(subpackage.getSubPackage());
        }
        return pack;
    }

    /**
     * Metodo generico para retornar un <package/> validando el contenido
     * de una columna
     * @return retorna un <package/>
     */
    public Element getPackage(boolean maxmin,int col,double validValue) {
        Element pack = new Element("package");
        for (int i=0;i<rows; i++) {
        	if ((maxmin && ((Number)getValueAt(i,col)).intValue()<=validValue) ||
        	    (!maxmin && ((Number)getValueAt(i,col)).intValue()>validValue))	{
	            Element subpack = new Element("subpackage");
	            StructureSubPackage subpackage = new StructureSubPackage();
	            for (int j=0;j<getColumnCount();j++) {
	            	subpackage = makeSubPackage(i,j,subpack);
	                if (subpackage.isValidPackage()) {
	                    break;
	                }    	
	            }
	            if (subpackage.isValidPackage()) {
	                break;
	            }
	            pack.addContent(subpackage.getSubPackage());
        	}
        }
        return pack;
    }
    
    public StructureSubPackage makeSubPackage(int i,int j,Element subpack) {
    	StructureSubPackage subpackage = new StructureSubPackage();
    	
        for (int k=0;k<getColumnCount();k++) {
            if (ATFDargs[k].getOrderReturn()==j) {
                Object value = getValueAt(i, k);
                if ((value==null || "".equals(value)) && !(ATFDargs[k].isReturnBlankCol() || ATFDargs[k].isReturnNullCol())) {
                    subpackage.setValidPackage(true);
                    break;
                }
                Element field = new Element("field");
                if (ATFDargs[k].getType().equals("COMBOSQL")) {
	                   StringTokenizer values = new StringTokenizer(value.toString()," ");
                   String tmp="";
                   while (true)
                       try {
                           tmp = values.nextToken();
                       }
                   	   catch(NoSuchElementException e1) {
                   	       break;
                   	   }
                       if (ATFDargs[k].getNameField()!=null) {
                    	   field.setAttribute("name",ATFDargs[k].getNameField());
                       }
                   	   field.setText(tmp);

                }
                else {
                    if (ATFDargs[k].getNameField()!=null) {
                    	field.setAttribute("name",ATFDargs[k].getNameField());
                    }
                    Object valueAt = getValueAt(i,k);
                    if (ATFDargs[k].isReturnNullCol() && valueAt.equals("")) {
                    		field.setText("NULL");
                    }
                    else {
                    		field.setText(value.toString());
                    }
                }
                subpack.addContent(field);
            }
        }
        subpackage.setSubPackage(subpack);
        return subpackage;
    }
    
    public Element getPrintPackage() {
        Element pack = new Element("package");
        
        for (int i=0;i<VdataRows.size(); i++) {
            Element subpack = new Element("subpackage");
            for (int j=0;j<getColumnCount();j++) {
            	Object valueAt = getValueAt(i,j);
            	if (ATFDargs[j].isPrintable()) {
            		if (valueAt==null || "".equals(valueAt)) {
                		i=rows;
                		break;
                	}
            		Element field = new Element("field");
            		if (ATFDargs[j].getType().equals("DATE")) {
            			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            			valueAt = sdf.format(valueAt);
            		}
            		field.setText(valueAt.toString());
            		subpack.addContent(field);
            	}
            }
            if (subpack.getContentSize()>0) {
            	pack.addContent(subpack);
            }
        }
        return pack;
    }
    @SuppressWarnings("unchecked")
	public Element getAgrupedPrintPackage(Element arguments) {
    	
    	Element pack = new Element("package");
    	
		int columnGroup =  Integer.parseInt(arguments.getChild("arg").getValue());
		Iterator iterator = arguments.getChildren("subarg").iterator();
		
		HashMap<String, Vector> tuples = new HashMap<String, Vector>();
		
		
		while (iterator.hasNext()) {
			
			Element subarg = (Element) iterator.next();
			String operation = ((Element)subarg.getChildren().get(0)).getValue();
			int column = Integer.parseInt(((Element)subarg.getChildren().get(1)).getValue());
			Object cell = null;
			HashMap<String, Integer> indexes = new HashMap<String, Integer>();
			
			for (int j=0; j < rows ; j++) {
			
				cell = VdataRows.get(j).get(columnGroup);
				if ( cell==null || "".equals(cell)) { break; }
				if (!indexes.containsKey(cell)) {
					indexes.put(cell.toString(),1);
				}
				else {
					int index = indexes.get(cell)+1;
					indexes.put(cell.toString(),index);
				}
				if (!tuples.containsKey(cell)) {
					Vector rowData = (Vector) VdataRows.get(j).clone();
					for (int i=0 ; i < ATFDargs.length ; i++) {
						if ((ATFDargs[i].getType().equals("INT") || 
							ATFDargs[i].getType().equals("INTEGER") ||
							ATFDargs[i].getType().equals("DECIMAL"))&&
							i!=column) {
							rowData.set(i,0);
						}
					}
					tuples.put(cell.toString(),rowData);
				}
				else {
					Vector rowCalculate = tuples.get(cell);
					Object tableValue = VdataRows.get(j).get(column);
					if (tableValue!=null) {
						Object currentValue = rowCalculate.get(column);
						Double newValue = null;
						Double val1 = Double.valueOf(tableValue.toString());
						Double val2 = Double.valueOf(currentValue.toString());
						if ("SUM".equals(operation)) {
							newValue = val1 + val2;
						}
						else if ("AVG".equals(operation)) {
							newValue =  val1 + val2;
						}
						else if ("MAX".equals(operation)) {
							newValue = 	val1 > val2 ? val1 : val2;
						}
						else if ("MIN".equals(operation)) {
							newValue = 	val1 > val2 ? val1 : val2;
						}
						if (newValue!=null) {
							rowCalculate.set(column,newValue);
						}
					}
				}
			}
			if ("AVG".equals(operation)) {
				Set<String> keys = tuples.keySet();
				for (String key : keys) {
					Double dCurrent = Double.valueOf(tuples.get(key).get(column).toString());
					dCurrent = dCurrent / indexes.get(key);
					tuples.get(key).set(column,dCurrent);
				}
			}
		}
		
		Set<String> keys = tuples.keySet();
		for (String key : keys) {
			Element subpackage = new Element("subpackage");
			Vector vector = tuples.get(key);
			for (int i=0; i< ATFDargs.length ; i++) {
				if (ATFDargs[i].isPrintable()) {
					Element field = new Element("field");
					field.setText(vector.get(i).toString());
					subpackage.addContent(field);
				}
			}
			pack.addContent(subpackage);
		}
		return pack;
    }
    
    public void setQuery(Document doc) {
    	setQuery(doc,false);
    }
    
    public synchronized void setQuery(Document doc,boolean search) {
    	loadingQuery = true;
        List Lrows = doc.getRootElement().getChildren("row");
        Iterator Irows = Lrows.iterator();
        int max = Lrows.size();
        
        if (tagDataColumn==-1 && max > 0) {
            /*
             * Se limpia la tabla antes de desplegar la consulta nueva
             */
            clean();
            /*
             * Cargando informacion
             */
            
            //for (int i=0;Irows.hasNext() && i<rows;i++) {
            for (int i=0;Irows.hasNext();i++) {
                Element Erow = (Element) Irows.next();
                List Lcol = Erow.getChildren();

	            for (int j=0;j<ATFDargs.length;j++) {
	                updateCells(addCols(j,Lcol),i,j);
	            }
	            if (formulas!=null) {
                    calcular(i,0,false);
                }
            }
        }
        else if (tagDataColumn>-1 && max > 0) {
        	Element Erow = null;
        	String tagDataValue = null;
        	Erow = (Element) Lrows.get(0);
        	Element element = (Element) Erow.getChildren().get(tagDataColumn);
        	tagDataValue = element.getValue().trim();
        	
        	for(int i=0; i < VdataRows.size(); i++) {
        		Object strData = getValueAt(i,tagDataColumn);
        		if (tagDataValue.equals(strData.toString().trim())){
        			deleteRow(i);
       				i --;
        		}
        	}
        	/*Aqui va la llenada de datos.*/
        	int currentRow = currentIndex;
        	for (int i=0;  i < max ; i++) {
        		Element RowQuery = (Element) Lrows.get(i);
        		List Lcol = RowQuery.getChildren();
        		for (int j=0;j<ATFDargs.length;j++) {
        			if (search && j==0){
        				setValueAt(addCols(j,Lcol), currentRow,0);
        			}
        			else {
        				updateCells(addCols(j,Lcol),currentRow,j);
        			}
	            }
        		currentRow++;
	            if (formulas!=null) {
                    calcular(i,0,false);
                }
        	}
        }
        else if (max==0) {
        	clean();
        }
        totalizar();
        loadingQuery = false;
    }

    private Object addCols(int j,List Lcol) {
        Object obj = null;
        try {
            Element Ecol = (Element) Lcol.get(j); 
            String newValue = Ecol.getValue();
                
            /*  Que mierdero todo esto */
            if (ATFDargs[j].getType().equals("BOOLEAN")) {
		            	if (newValue.equals("t") ||
                        newValue.equals("T") ||    
                        newValue.equals("true") ||    
                        newValue.equals("TRUE") ||
                        newValue.equals("True") ||
                        newValue.equals("1")) {
		            		obj= new Boolean(true);
		            	}
                    else {
                        obj =new Boolean(false);
                    }
            }
            else if (ATFDargs[j].getType().equals("DATE")) {
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            	try {
					obj = sdf.parse(newValue);
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
            }
            else {
                try {
                    Constructor cons = ATFDargs[j].getColumnClass().getConstructor(new Class[]{String.class});
                    Object object = cons.newInstance(new Object[]{newValue});
                    obj = object;
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
                catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (InstantiationException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e) {
                    if (e.getCause().getClass().getName().equals("java.lang.NumberFormatException")) {
                        obj = ATFDargs[j].getTypeDate();
                    	//obj = null;
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(IndexOutOfBoundsException e) {
            obj = ATFDargs[j].getTypeDate();
        	//obj = null;
        }
        
        return obj;
    }

    public void deleteRow(int index) {
    	Vector<Object> vptype = new Vector<Object>();
        for (int j=0;j<ATFDargs.length;j++) {
        	if (ATFDargs[j].getType().equals("DATE")) {
        		vptype.add(null);
        	}
        	else {
        		vptype.add(ATFDargs[j].getTypeDate());
        	}
        }
        VdataRows.add(vptype);
        VdataRows.remove(index);
        currentIndex --;
        fireTableDataChanged();
    }
    /**
     * Metodo encargado de recalcular la informacion de la tabla cuando sucede un evento.
     */
    
    public void changeValue(ChangeValueEvent e) {
        for (int i=0;i<rows;i++) {
            if (!getValueAt(i,0).equals("")) {
                calcular(i,0);
            }
            else {
                break;
            }
        }
        totalizar();
    }

    /**
     * Metodo encargado de informar a esta clase que todos los componentes de GenericForm
     * terminaron de ser instanciados
     */
    public void initiateFinishEvent(FinishEvent e) {
    	
        loadExternalEvents();
        GFforma.addChangeExternalValueListener(this);
    }
    
    public void addKeyLink(String keyLink) {
    	deleteLink.add(keyLink);
    }

	public Vector getImpValues() {
		return impValues;
	}
	
	public int impValuesSize() {
		if (impValues!=null) {
			return impValues.size();
		}
		return 0;
	}
	
	public void setQuery(String sqlCode) {
		this.sqlCode = sqlCode;
		for (int i=0; i < rows && !getValueAt(i,0).equals("") ; i++ ) {
			updateQuery=true;
			setValueAt(getValueAt(i,0),i,0);
		}
	}
	
	public void changeExternalValue(ChangeExternalValueEvent e) {
		if (importTotalCol.containsValue(e.getExternalValue())) {
			for (int i=0;i<rows;i++) {
		        if (!getValueAt(i,0).equals("")) {
		            calcular(i,0);
		        }
		        else {
		            break;
		        }
		    }
		    totalizar();
		}
	}

	public void setTagDataColumn(int tagDataColumn) {
		this.tagDataColumn = tagDataColumn;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}
}

class StructureSubPackage {
	boolean validPackage;
	Element subPackage;
	
	public Element getSubPackage() {
		return subPackage;
	}
	public void setSubPackage(Element subPackage) {
		this.subPackage = subPackage;
	}
	public boolean isValidPackage() {
		return validPackage;
	}
	public void setValidPackage(boolean validPackage) {
		this.validPackage = validPackage;
	}
}