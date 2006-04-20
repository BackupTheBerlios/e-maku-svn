package server.businessrules;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import common.misc.language.Language;
import server.database.sql.CacheEnlace;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import org.jdom.Element;

/**
 * LNInventarios.java Creado el 25-jul-2005
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
 * Esta clase se encarga de procesar y reorganizar todo lo referente al 
 * movimiento de Inventarios.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNInventarios {

    private boolean externalKeys;
    private int campoMov;
    private int colValid = -1;
    private String validCols;
    private String bd;
    private String tipoMovimiento;
    private final String ENTRADA = "entrada";
    private final String SALIDA = "salida";
    private final String ANULARSALIDA = "anularsalida";
    private final String ANULARENTRADA = "anularentrada";
    private final String GASTOS = "gastos";
    private final String DESCUENTOS = "descuentos";
    /**
     * Este es el constructor de la clase, los parametros recibidos
     * son un String que almacena el numero de columnas de las cuales
     * tomara los valores para generar el movimiento y una variable
     * boleana que le informara si se tendra en cuenta los datos de
     * las llaves externas, o si no.
     */
    public LNInventarios(Element parameters,String bd) {
        
        this.bd=bd;
        Iterator i = parameters.getChildren().iterator();
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String attribute = e.getAttributeValue("attribute");
            String value = e.getValue();
            if (attribute.equals("externalKeys")) {
                if (value.equals("1") || value.equals("true") || value.equals("True") || value.equals("TRUE")) {
                    externalKeys = true;
                }
            } 
            else if (attribute.equals("validCols")) {
                validCols = e.getValue();
            }
            else if (attribute.equals("campoMov")){
                campoMov=Integer.parseInt(e.getValue());
            }
            else if (attribute.equals("colGasto") || attribute.equals("colDescuento")) { 
            		colValid=Integer.parseInt(e.getValue());
            }
            else if (attribute.equals("tipoMovimiento")) {
            	tipoMovimiento = e.getValue();
            }
            
        }
        

    }

    /**
     * Este metodo es el encargado de procesar las columnas validas para un movimiento de inventario
     * @param pack contiene la informacion a procesar.
     * @throws SQLBadArgumentsException
     * @throws SQLBadArgumentsException
     * @throws SQLNotFoundException
     * @throws SQLException
     */
    
    public void movimientos(Element pack) throws LNErrorProcecuteException, 
    SQLBadArgumentsException,SQLBadArgumentsException, SQLNotFoundException, SQLException {
        try {
            
            /*
             * Primer paso, se agrega las columnas validas del paquete en un
             * vector
             */
	        StringTokenizer STcols = new StringTokenizer(validCols,",");
	        int cols[] = new int[STcols.countTokens()];
	        for (int i=0;STcols.hasMoreTokens();i++) {
	            cols[i] = Integer.parseInt(STcols.nextToken());
	        }
	        
	        /*
	         * Se valida el numero de columnas por registro, teniendo en cuenta si se
	         * utilizara llaves externas y el numero de columnas a utilizar segun la
	         * parametrizacion.
	         */
	        
            int lenght;
            if (externalKeys) {
                lenght=CacheKeys.size()+cols.length;
            }
            else {
                lenght=cols.length;
            }

            RunQuery RQmovimiento = null;
            boolean movimiento = false;
            if (SALIDA.equals(tipoMovimiento.trim().toLowerCase()) ||
            	ANULARENTRADA.equals(tipoMovimiento.trim().toLowerCase())) {
        			movimiento=true;
                RQmovimiento = new RunQuery(bd,"INS0038");
        		} 
            else if (ENTRADA.equals(tipoMovimiento.trim().toLowerCase()) ||
        			  ANULARSALIDA.equals(tipoMovimiento.trim().toLowerCase())) {	
        		    RQmovimiento = new RunQuery(bd,"INS0052");
            }
            else if (GASTOS.equals(tipoMovimiento.trim().toLowerCase()) || DESCUENTOS.equals(tipoMovimiento.trim())) {
            		RQmovimiento = new RunQuery(bd,"INS0069");
            }

            /*
	         * Se verifica si el paquete entregado contiene un solo registro <field/>
	         * o varios registros <package/>
	         */
	        
	        
	        /*
	         * Si tiene un solo registro entonces
	         */
            
            if (((Element)pack.getChildren().iterator().next()).getName().equals("field")) {
                if (externalKeys) {
                    RQmovimiento.ejecutarSQL(makeRecordWithKeys(pack,cols,lenght,movimiento));
                }
                else {
                    RQmovimiento.ejecutarSQL(makeRecordWithOutKeys(pack,cols,lenght,movimiento));
                }
            }
            /*
             * Si no es por que tiene un conjuto de registros o <subpackage/>
             */
            else {
    	        Iterator ipack = pack.getChildren().iterator();
                if (externalKeys) {
                    while (ipack.hasNext()) {
                        Element epack = (Element)ipack.next();
                        /*
                         * Si se va a ingresar un gasto, se verifica que el valor de este sea mayor a 0 
                         */
                        if ((GASTOS.equals(tipoMovimiento.trim().toLowerCase()) || 
                        		 DESCUENTOS.equals(tipoMovimiento.trim())) && !tieneGastos(epack)) {
                        		break;
                        }
                        RQmovimiento.ejecutarSQL(makeRecordWithKeys(epack,cols,lenght,movimiento));
                    }
                }
                else {
                    while (ipack.hasNext()) {
                        Element epack = (Element)ipack.next();
                        /*
                         * Si se va a ingresar un gasto, se verifica que el valor de este sea mayor a 0 
                         */
                        if ((GASTOS.equals(tipoMovimiento.trim().toLowerCase()) || 
                        		 DESCUENTOS.equals(tipoMovimiento.trim())) && !tieneGastos(epack)) {
                        		break;
                        }

                        RQmovimiento.ejecutarSQL(makeRecordWithOutKeys(epack,cols,lenght,movimiento));
                    }
                }
            }

        }
        catch(NumberFormatException NFEe) {
            throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
        }
        catch(IndexOutOfBoundsException IOOBEe) {
        	IOOBEe.printStackTrace();
            throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
        }
    }
    
    /**
     * Este metodo verifica si el paquete contiene gastos adicionales
     * @param pack paquete a verificar
     * @return retorna true si el valor del gasto es mayor a 0 o retorna false si no
     */
    
    private boolean tieneGastos(Element pack) {

    		List lpack = pack.getChildren();
    		
    		if (colValid<0) {
    			return false;
    		}
    		String value = ((Element)lpack.get(colValid)).getValue();
    		try {
    			double val = Double.parseDouble(value.trim());
    			if (val>0) {
    				return true;
    			}
    		}
    		catch(NumberFormatException NFEe) {
    			return false;
    		}
    		return false;
    }
    
    /**
     * Se crean dos metodos similares, el uno genera el registro teniendo en cuenta llaves
     * externas, el otro solo con datos del paquete recibido.
     * 
     * Se crea dos metodos para evitar un if que tendria que repetirce cada que genere un
     * registro, esto es para hacer mas eficiente el procesamiento del paquete.
     * @param pack paquete de datos
     * @param cols arreglo de columnas por orden
     * @param lenght numero de columnas por registro
     * @return retorna un arreglo de String que contiene el registro
     * @throws SQLBadArgumentsException 
     * @throws SQLNotFoundException 
     * @throws SQLException 
     */
    
    private String[] makeRecordWithKeys(Element pack,int[] cols,int lenght,boolean tipoMov) 
    throws IndexOutOfBoundsException, SQLNotFoundException, SQLBadArgumentsException, SQLException {
        /*
         * La generacion del registro cuando se tiene encuenta las llaves, consta de los siguientes pasos: 
         */
    	
        /* 
         * REGISTROS DE UNA ENTRADA
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = entrada
         * record[5] = valor_ent
         * record[6] = pinventario
         * record[7] = saldo
         * record[8] = valor_saldo
         * 
         * REGISTRO DE UNA SALIDA
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = salida
         * record[5] = valor_sal
         * record[6] = pinventario
         * record[7] = saldo
         * record[8] = valor_saldo
         * 
         * REGISTRO DE UN GASTO PARA RECOSTEAR UN PRODUCTO O UN DESCUENTO
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = valor_ent
         * record[5] = pinventario
         * record[6] = saldo
         * record[7] = valor_saldo

         */
    	
        String[] record;
        
        
        
        /*
         * 1. Se define el tama�o del registro adicionando 4 campos mas que seran para almacenar al
         *    Fecha,PCosto,Saldo y Valor_saldo
         */
        
        if (ENTRADA.equals(tipoMovimiento.trim().toLowerCase())){
        		record= new String[lenght+4];
        } else {
    		record= new String[lenght+5];
        }
        
        /*
         * El primer registro siempre sera la fecha
         */
        record[0] = CacheKeys.getDate();
        
        /*
         * Se inicia la variable del registro en uno, ya que el registro cero fue asignado a la fecha,
         * luego se procede a almacenar los datos correspondientes a las llaves externas.
         */
        int i=1;
        Iterator val = CacheKeys.getKeys();
        for (;val.hasNext();i++) {
            record[i] = (String)val.next();
        }
        
        /*
         *  Una vez almacenadas las llaves externas se procede a sacar la informacion necesaria de los
         *  paquetes para generar otros campos.
         */
        
        List lpack = pack.getChildren();
        for (int j=0;j<cols.length;j++,i++) {
            lpack = pack.getChildren();
            String value = ((Element)lpack.get(cols[j])).getValue();
//            System.out.println("valor obtenido de la columna: "+cols[j]+": "+value);
            record[i] = value;
        }
        
        /*
         * Si se va a anular una salida, entonces se debe consultar en el inventario el precio de costo
         * a la que se efectuo el movimiento.
         */
        double valorEntrada = 0;
        if (ANULARSALIDA.equals(tipoMovimiento.trim().toLowerCase())) {
	        RunQuery RQkey = new RunQuery(bd,"SEL0130",new String[]{record[1],record[2], record[3]});
	        ResultSet RSdatos = RQkey.ejecutarSELECT();
	        while (RSdatos.next()) {
	            valorEntrada=RSdatos.getDouble(1);
	        }
	        record[i] = String.valueOf(valorEntrada);
	        RSdatos.close();
	        RQkey.closeStatement();
	        i++;
        }
        /*
         * Los tres ultimos campos seran para pcosto,saldo y vsaldo, los cuales se calculan dependiendo
         * si es una entrada o una salida.
         */
        double pcosto = 0;
        double saldo = CacheEnlace.getSaldoInventario(bd, record[2], record[3]);
        double vsaldo = CacheEnlace.getVSaldoInventario(bd, record[2], record[3]);
        
        /*
         * Se almacena primer saldo de la transaccion
         */
        
        LNUndoSaldos.setSaldoAntInv(bd, record[2], record[3],saldo);
        
        /*
         * si es una salida entonces solo se calculara el saldo y vsaldo, se obtendra pcosto que sera
         * igual al del registro anterior.
         */
        
        if (tipoMov) {
            double salida = Double.parseDouble(((Element)lpack.get(campoMov)).getValue());
            if (ANULARENTRADA.equals(tipoMovimiento.trim().toLowerCase())) {
	            pcosto = Double.parseDouble(record[5]);
                saldo-= salida;
                vsaldo-=(salida*pcosto);    
            		record[i]   = String.valueOf(pcosto);
	            record[i+1] = String.valueOf(saldo);
	            record[i+2] = String.valueOf(vsaldo);
            }
            else {
                pcosto = CacheEnlace.getPCosto(bd, record[2], record[3]);
                saldo-= salida;
                vsaldo-=(salida*pcosto);    
	            record[i]   = String.valueOf(pcosto);
	            record[i+1]   = String.valueOf(pcosto);
	            record[i+2] = String.valueOf(saldo);
	            record[i+3] = String.valueOf(vsaldo);
            }
        }
        
        /*
         *  si no es porque es una entrada, entonces se debera calcular pcosto, saldo y vsaldo y actualizara
         *  en CacheEnlace a pcosto.
         */
        
        else {
        	System.out.println("Entradas: ");
//        	for (int j=0;j<record.length;j++)
//        		System.out.println("val "+j+" : "+record[j]);
        	
            double entrada = 0;
            double ventrada = 0;
            
            if (GASTOS.equals(tipoMovimiento.trim().toLowerCase()) || DESCUENTOS.equals(tipoMovimiento.trim().toLowerCase())) {
            		if (GASTOS.equals(tipoMovimiento.trim().toLowerCase())) {
            			ventrada = Double.parseDouble(record[4]);
            		}
            		else {
            			/*
            			 * Si es un desucento entonces el valor de la entrada debe ser negativo, para que disminuya
            			 * el valor total y por tanto el costo del producto. 
            			 */
            			ventrada = Double.parseDouble(record[4])*-1;
            			record[4] = String.valueOf(ventrada);
            		}
            		vsaldo+=ventrada;
            }
            else {
                entrada = Double.parseDouble(record[4]);
                ventrada = Double.parseDouble(record[5]);
	            vsaldo+= (entrada*ventrada);
	            saldo+= Double.parseDouble(((Element)lpack.get(campoMov)).getValue());
            }
            
            System.out.println("entrada: "+entrada+" ventrada: "+ventrada+" nuevo saldo "+vsaldo);
            pcosto=(vsaldo/saldo);
            BigDecimal bigDecimal = new BigDecimal(pcosto);
            bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
            pcosto = bigDecimal.doubleValue();
            record[i]   = String.valueOf(pcosto);
            record[i+1] = String.valueOf(saldo);
            record[i+2] = String.valueOf(vsaldo);
            CacheEnlace.setPCosto(bd, record[2], record[3],pcosto);
        }
        
        /*
         *  por ultimo se actualiza saldo y vsaldo en cache de enlace. esto es temporalmente, no esta definido
         *  aun debido a que en caso de ocurrir una excepcion, entonces se deshace los movimientos de la base
         *  de datos y si los saldos no se deshacen entonces se generaria inconcistencias para la generacion
         *  de nuevos registros.
         */
        CacheEnlace.setSaldoInventario(bd, record[2], record[3],saldo);
        CacheEnlace.setVSaldoInventario(bd, record[2], record[3],vsaldo);
        
        return record;
    }
    
    private String[] makeRecordWithOutKeys(Element pack,int[] cols,int lenght,boolean tipoMov) {
        String[] record = new String[lenght];
        for (int j=0;j<cols.length;j++) {
            List lpack = pack.getChildren();
            record[j] = ((Element)lpack.get(cols[j])).getValue();
        }
        return record;
    }
    
    /**
     * Este metodo se encarga de efectuar traslados entre diferentes bodegas, 
     * el paquete recibido puede obtener una lista de productos a mover o un solo 
     * producto. 
     *
     */
    public void traslados(Element pack) {
    	
    }
    
    @SuppressWarnings("unused")
	private String[] salidaBodega() {
         
    	/*
         * REGISTRO DE UNA SALIDA
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = salida
         * record[5] = valor_sal
         * record[6] = pinventario
         * record[7] = saldo
         * record[8] = valor_saldo
         */
    	
        String[] record = new String[8];
        
        /*
         * El primer registro es la fecha;
         */
        record[0] = CacheKeys.getDate();
        record[1] = CacheKeys.getKey("ndocumento");
        
        return record;
    }
    
    @SuppressWarnings("unused")
	private String[] entradaBodega() {
    	/* 
         * REGISTROS DE UNA ENTRADA
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = entrada
         * record[5] = valor_ent
         * record[6] = pinventario
         * record[7] = saldo
         * record[8] = valor_saldo
         */

    	String[] record = new String[8];
    	
        record[0] = CacheKeys.getDate();
        record[1] = CacheKeys.getKey("ndocumento");

        return record;
    }
}
