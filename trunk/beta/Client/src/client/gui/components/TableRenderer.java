package client.gui.components;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * QueryTableData.java Creado el 11-mar-2005
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
 * Esta clase genera un objeto AbstractTableModel, apartir de una paquete <ANSEWER>
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class TableRenderer extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2374521310714502106L;
	private Vector<Vector> rows;
    private String[] columnNames = {};
    private int columnNumber;
    
    /*public void addSuccesEventLisnter(SuccessEvent e) {
        
    }*/
    
    /**
     * Constructor con envio de sentencia sin argumentos
     * @param sql sentencia sql
     */
    public TableRenderer(String sql) {
        searchQuery(sql,null);
    }
    /**
     * Constructor con envio de sentencia con argumentos
     * @param sql sentencia sql a llamar
     * @param args argumentos de la sentencia
     */
    public TableRenderer(String sql,String[] args) {
        searchQuery(sql,args);
    }

    /**
     * Metodo encargado de generar la consulta para cargar la informacion
     * en el AbstracTableModel
     * @param sql id de la consulta
     * @param args argumentos de la consulta
     */
    private void searchQuery(String sql,String[] args) {
        try {
            Document Dquery;
            if (args!=null) 
                Dquery = TransactionServerResultSet.getResultSetST(sql,args);
            else
                Dquery = TransactionServerResultSet.getResultSetST(sql);
            
            Iterator Iheader = Dquery.getRootElement().getChildren("header").iterator();
            Iterator Irows = Dquery.getRootElement().getChildren("row").iterator();
            
            /*
             * Cargando cabeceras
             */
            Element Eheader = (Element) Iheader.next();
            List Lcol = Eheader.getChildren();
            Iterator Icolheader = Lcol.iterator();
            
            columnNumber = Lcol.size();
            columnNames = new String[columnNumber];
                
            for (int i=0;Icolheader.hasNext();i++) {
                Element Ecol = (Element) Icolheader.next();
                columnNames[i]=Ecol.getValue();
            }
            
            /*
             * Cargando informacion
             */
            rows = new Vector<Vector>();
            while (Irows.hasNext()) {
                Element Erow = (Element) Irows.next();
                Iterator Icol = Erow.getChildren().iterator();
                Vector<String> col = new Vector<String>();
                while (Icol.hasNext()) {
                    Element Ecol = (Element) Icol.next();
                    col.add(Ecol.getValue());
                }
                String nCuenta = col.get(0);
                int nSpaces = nCuenta.trim().length();
                
                String spaces = new String();
		        	for (int j=0; j < nSpaces; j++) {
		        		spaces+=" ";
		        	}
           	
	            	nCuenta = spaces+nCuenta;
	            	col.setElementAt(nCuenta,0);
	            	rows.add(col);
            }
        }
        catch (TransactionServerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna el numero de columnas
     * @return entero con # de columnas
     */
    public int getColumnCount() {
        return columnNumber;
    }

    /**
     * Retorna los nombres de las columnas
     * @return columnNames
     */
    public String getColumnName(int columnIndex) {
        if (columnNames[columnIndex] != null) {
            return columnNames[columnIndex];
        } else {
            return "";
        }
    }
    
    /**
     * Retorna el numero de filas
     * @return entero con el numero de filas
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Retorna el contenido de una columna
     * @param rowIndex coordenada de la fila
     * @param columnIndex coordenada de la columna
     * @return retorna el objeto seleccionado
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Vector row = rows.elementAt(rowIndex);
        return row.elementAt(columnIndex);
    }
}
