 package server.businessrules;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

import server.database.sql.CacheEnlace;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.misc.language.Language;

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

    private String bd;
    private String tipoMovimiento;
    private final String ENTRADA = "entrada";
    private final String SALIDA = "salida";
    private final String GASTOS = "gastosydescuentos";
    private final String ANULAR = "anular";
    double saldo;
    double vsaldo;
    
    /**
     * Este es el constructor de la clase, los parametros recibidos
     * son un String que almacena el numero de columnas de las cuales
     * tomara los valores para generar el movimiento y una variable
     * boleana que le informara si se tendra en cuenta los datos de
     * las llaves externas, o si no.
     */
    
    public LNInventarios(Element parameters,String bd) {
        
        this.bd=bd;
        if (parameters!=null) {
	        Iterator i = parameters.getChildren().iterator();
	        while (i.hasNext()) {
	            Element e = (Element)i.next();
	            String attribute = e.getAttributeValue("attribute");
	            if (attribute.equals("tipoMovimiento")) {
	            	tipoMovimiento = e.getValue();
	            }
	        }
        }
    }

    /**
     * Este metodo se llama cuando se va a generar una salida de inventarios
     * @param pack
     * @throws SQLNotFoundException
     * @throws SQLBadArgumentsException
     * @throws SQLException 
     */
    
    public void movimientos(Element pack) 
    throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        
    	/*
         * Se verifica si el paquete entregado contiene un solo registro <field/>
         * o varios registros <package/>
         * 
         * Si tiene un solo registro entonces
         */
    	RunQuery RQmovimiento = null;
    	System.out.print("tipo Movimiento: "+tipoMovimiento);
    	if (ENTRADA.equals(tipoMovimiento.trim().toLowerCase())) {
        	RQmovimiento = new RunQuery(bd,"INS0052");
        	String record[] = movimientoInventario(pack,ENTRADA);
        	/*
        	 * Se valida que no se genere un asiento de inventario con cantidad 0
        	 * si la cantidad es 0 quiere decir que no hubo movimiento por tanto
        	 * el asiento es descartado
        	 */
        	if (!(record[4].equals("0") || record[4].equals("0.0"))) {
/*        		System.out.print("Registros: ");
        		for (int i=0;i<record.length;i++) {
        			System.out.println("registro "+i+" : "+record[i]);
        		}
*/        		RQmovimiento.ejecutarSQL(record);
				RQmovimiento.closeStatement();
        	}
    	}
    	else if (SALIDA.equals(tipoMovimiento.trim().toLowerCase())) {
        	RQmovimiento = new RunQuery(bd,"INS0038");
        	String record[] = movimientoInventario(pack,SALIDA);
        	if (!(record[4].equals("0") || record[4].equals("0.0"))) {
/*        		System.out.print("Registros: ");
        		for (int i=0;i<record.length;i++) {
        			System.out.println("registro "+i+" : "+record[i]);
        		}
 */       		RQmovimiento.ejecutarSQL(record);
 				RQmovimiento.closeStatement();
        	}
    	}
    	else if (GASTOS.equals(tipoMovimiento.trim().toLowerCase())) {
    		gastosYdescuentos(pack);
    	}
    	else if (ANULAR.endsWith(tipoMovimiento.trim().toLowerCase())) {
    		anular(); 
    	}
    }
    /**
     * Este metodo se encarga de efectuar traslados entre diferentes bodegas.
     */
    
    public void traslados(Element pack) throws LNErrorProcecuteException, 
    SQLBadArgumentsException,SQLBadArgumentsException, SQLNotFoundException, SQLException {
    	RunQuery RQentrada = null;
    	RunQuery RQsalida = null;
        try {
        	String[] records = movimientoInventario(pack,SALIDA);
        	RQsalida = new RunQuery(bd,"INS0038");
        	RQsalida.ejecutarSQL(records);
        	CacheKeys.setKey("valorEntrada",records[6]);
        	String[] ventradas = movimientoInventario(pack,ENTRADA);
        	RQentrada = new RunQuery(bd,"INS0052");
        	RQentrada.ejecutarSQL(ventradas);
        	RQentrada.closeStatement();
        	RQsalida.closeStatement();
        }
        catch(NumberFormatException NFEe) {
        	RQentrada.closeStatement();
       	 	RQsalida.closeStatement();
            throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
        }
        catch(IndexOutOfBoundsException IOOBEe) {
        	RQentrada.closeStatement();
       	 	RQsalida.closeStatement();
       	 	IOOBEe.printStackTrace();
            throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
        }
    }

    /**
     * Este metodo analiza la información del paquete pack y aplica la logica
     * de negocios correspondiente para generar una entrada de inventarios
     * @param pack Este objeto contiene la información necesaria para generar
     * 		  el movimiento
     * @return Retorna un objeto String listo para ser almacenado en la base 
     * 		   de datos.
     */

    private String[] movimientoInventario(Element pack,String movimiento) {
		
		/* 
         * REGISTROS DE UN MOVIMIENTO
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = entrada			| record[4] = salida
         * record[5] = valor_ent		| record[5] = valor_sal
         * record[6] = pinventario
         * record[7] = saldo
         * record[8] = valor_saldo
         */

    	String[] record = new String[9];
    	int conversion = 1;
    	/*
    	 * Se carga los datos iniciales
    	 */
    	if (movimiento.equals(ENTRADA)) {
    		record = infoMovimiento(pack,"bodegaEntrante");
    	}
    	else {
    		record = infoMovimiento(pack,"bodegaSaliente");
    		conversion = -1;
    	}
    	
    	/*
    	 * Si el registro 4 del arreglo es 0 quiere decir que
    	 * no tiene cantidad, por tanto se procesa el codigo
    	 * restante del metodo y se retorna el arreglo.
    	 */
    	if (record[4].equals("0") || record[4].equals("0.0")) {
    		return record;
    	}
        
    	/*
    	 * Se verifica si record[5] tiene valor, en caso
    	 * de que no lo tenga es porque se esta generando
    	 * un traslado, por tanto este tubo que haber sido
    	 * almacenado con anterioridad en el cache de llaves
    	 */
    	
    	if (movimiento.equals(ENTRADA)) {
	    	if (record[5]==null) {
	    		record[5] = CacheKeys.getKey("valorEntrada");
	    	}
    	}
    	else {
            if (record[5]==null) {
            	record[5] = String.valueOf(CacheEnlace.getPCosto(bd, record[2], record[3]));
            }
    	}
    	
        double cantidad = Double.parseDouble(record[4]);
    	double valor = Double.parseDouble(record[5]);

    	String[] ponderado = ponderar(record[2],
				  record[3],
				  cantidad*conversion,
				  valor);

		record[6] = ponderado[0];
		record[7] = ponderado[1];
		record[8] = ponderado[2];
        return record;
    }

    private void gastosYdescuentos(Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
    	/*
         * REGISTRO DE UN GASTO O UN DESCUENTO
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
    	
    	String record[] = new String[8];
    	Vector<Double> gastos = new Vector<Double>();
    	
        record[0] = CacheKeys.getDate();
        record[1] = CacheKeys.getKey("ndocumento");
        record[2] = CacheKeys.getKey("bodegaEntrante");
        
        Iterator i = pack.getChildren().iterator();
        /*
         * Este ciclo se encarga de sacar la informacion necesaria para generar
         * una salida
         */
        while (i.hasNext()) {
        	Element field = (Element)i.next();
        	String nameField = field.getAttributeValue("name");
        	if (nameField!=null) {
        		nameField = nameField.toLowerCase();
	        	if ("bodegaentrante".equals(nameField)) {
	        		record[2] = field.getValue();
	        	}
	        	else if ("idproducto".equals(nameField)) {
	        		record[3] = field.getValue();
	        	}
	        	else if ("gastos".equals(nameField) || "descuentos".equals(nameField)) {
	        		try {
	        			double gd = Double.parseDouble(field.getValue());
	        			if (gd>0) {
		        			if ("gastos".equals(nameField)) {
		    					gastos.addElement(new Double(gd));
		        				
		        			}
		        			else {
		        				double descuento = gd*-1;
		        				gastos.addElement(new Double(descuento));
		        			}
	        			}
	        		}
	        		catch(NumberFormatException NFEe) {}
	        	}
        	}
        }
		RunQuery RQmovimiento = new RunQuery(bd,"INS0069");
		
		for (int j=0;j<gastos.size();j++) {
	        /*
	         * Se captura los saldos antes del movimiento
	         */
	        saldo = CacheEnlace.getSaldoInventario(bd, record[2], record[3]);
	        vsaldo = CacheEnlace.getVSaldoInventario(bd, record[2], record[3]);

	        /*
	    	 * Se carga los datos iniciales
	    	 */
	            	
	        double ventrada = gastos.get(j).doubleValue();
	        vsaldo+=ventrada;
	        double pcosto=(vsaldo/saldo);
	        BigDecimal bigDecimal = new BigDecimal(pcosto);
	        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
	        pcosto = bigDecimal.doubleValue();
	        
	        record[4] = String.valueOf(ventrada);
	        record[5] = String.valueOf(pcosto);
	        record[6] = String.valueOf(saldo);
	        record[7] = String.valueOf(vsaldo);
	    	RQmovimiento.ejecutarSQL(record);
	        CacheEnlace.setPCosto(bd, record[2], record[3],pcosto);
	        actualizarSaldos(record);
		}
		
		RQmovimiento.closeStatement();
    }
    
    /**
	 * Este metodo retorna los datos iniciales de un movimiento de inventario
	 * @return retorna un arreglo con los datos iniciales
	 */
	private String[] infoMovimiento(Element pack,String bodega) {
		/* 
         * Todo movimiento de inventarios tiene unos datos iniciales que son:
         * 
         * record[0] = fecha
         * record[1] = ndocumento
         * record[2] = id_bodega
         * record[3] = id_prod_serv
         * record[4] = entrada || salida      
         * record[5] = pcosto                 
         */
		

    	String[] record = new String[9];
    	
        record[0] = CacheKeys.getDate();
        record[1] = CacheKeys.getKey("ndocumento");
        record[2] = CacheKeys.getKey(bodega);
        
        Iterator i = pack.getChildren().iterator();
        /*
         * Este ciclo se encarga de sacar la informacion necesaria para generar
         * una salida
         */
        while (i.hasNext()) {
        	Element field = (Element)i.next();
        	String nameField = field.getAttributeValue("name");
        	if (nameField!=null) {
        		nameField = nameField.toLowerCase();
	        	if (bodega!= null && bodega.toLowerCase().equals(nameField)) {
	        		record[2] = field.getValue();
	        	}
	        	else if ("idproducto".equals(nameField)) {
	        		record[3] = field.getValue();
	        	}
	        	else if ("cantidad".equals(nameField)) {
	        			System.out.println("Asignando cantidad... "+field.getValue());
	        			record[4] = field.getValue();
	        	}
	        	else if ("pcosto".equals(nameField)) {
        			record[5] = field.getValue();
	        	}
        	}
        }

        /*
         * Se captura los saldos antes del movimiento
         */
        saldo = CacheEnlace.getSaldoInventario(bd, record[2], record[3]);
        vsaldo = CacheEnlace.getVSaldoInventario(bd, record[2], record[3]);

        /*
         * Se almacena primer saldo de la transaccion
         */

        LNUndoSaldos.setSaldoAntInv(bd, record[2], record[3],saldo);

        return record;
	}

	/**
	 * Este metodo se encarga de deshacer un movimiento apartir del codigo del
	 * documento, su funcion es sacar del inventario los productos que entraron
	 * y meter los productos que salieron, a sus correspondientes precios de
	 * costo con el que se genero el movimiento.
	 * @throws SQLException 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 */
	
    private void anular() 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
    	String idDocumento = CacheKeys.getKey("ndocumento");
        RunQuery RQdocumento = new RunQuery(bd,"SEL0238",new String[]{idDocumento});
        ResultSet RSdatos = RQdocumento.ejecutarSELECT();
        /*
         * La anterior consulta nos retorna las siguientes columnas
         * 1. id_prod_serv
         * 2. id_bodega
         * 3. pinventario
         * 4. entrada
         * 5. valor_ent
         * 6. salida
         * 7. valor_sal
         * 
         * del anterior registro se analiza los campos entrada y salida;
         * con ellos se sabe el tipo de movimiento a anular.
         */

        while (RSdatos.next()) {
        	Vector<String> record = new Vector<String>();
        	String[] ponderado = null;
        	RunQuery RQanular = null;
        	
        	record.addElement(CacheKeys.getDate());
        	record.addElement(idDocumento);
        	record.addElement(RSdatos.getString(2));
        	record.addElement(RSdatos.getString(1));
            /*
             * Se captura los saldos antes del movimiento
             */
            saldo = CacheEnlace.getSaldoInventario(bd, RSdatos.getString(2),RSdatos.getString(1));
            vsaldo = CacheEnlace.getVSaldoInventario(bd, RSdatos.getString(2),RSdatos.getString(1));

            /*
             * Se almacena primer saldo de la transaccion
             */

            LNUndoSaldos.setSaldoAntInv(bd, RSdatos.getString(2),RSdatos.getString(1),saldo);
        	
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
             */
        	
        	/*
        	 * Si la columna 4 retorna un valor diferente de 0 entonces el movimiento a anular
        	 * es una entrada
        	 */
        	if (RSdatos.getDouble(4)!= 0) {
            	RQanular = new RunQuery(bd,"INS0038");

            	record.addElement(RSdatos.getString(4));
            	record.addElement(RSdatos.getString(5));

            	/*
            	 * Se multiplica el 3 registro por -1 para indicar que el movimiento generado
            	 * sea una salida.
            	 */
            	
            	ponderado = ponderar(RSdatos.getString(2),
											  RSdatos.getString(1),
											  RSdatos.getDouble(4)*-1,
											  RSdatos.getDouble(5));
        	}
        	/*
        	 * si no, si la columna 6 retorna un valor diferente de 0 entonces el movimiento a 
        	 * anular es una salida 
        	 */
        	else if (RSdatos.getDouble(6)!=0) {
            	RQanular = new RunQuery(bd,"INS0052");
            	record.addElement(RSdatos.getString(6));
            	record.addElement(RSdatos.getString(7));
            	ponderado = ponderar(RSdatos.getString(2),
            								  RSdatos.getString(1),
            								  RSdatos.getDouble(6),
            								  RSdatos.getDouble(7));
        	}
        	/*
        	 * si no, es por que el movimiento a anular es gasto o un descuento
        	 */
        	else {
            	record.addElement(RSdatos.getString(4));
        	}
        	
        	if (ponderado!=null) {
	    	    record.addElement(ponderado[0]);
	    	    record.addElement(ponderado[1]);
	    	    record.addElement(ponderado[2]);
	        	RQanular.ejecutarSQL(record.toArray(new String[record.size()]));
	        	RQanular.closeStatement();
        	}
        }
        RSdatos.close();
        RQdocumento.closeStatement();
    }

    private String[] ponderar(String idBodega,String idProdServ,double cantidad,double valor) {
        
    	saldo+= cantidad;
    	vsaldo+= (cantidad*valor);
	    double pcosto=(vsaldo/saldo);
	    try {
		    BigDecimal bigDecimal = new BigDecimal(pcosto);
		    bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
		    pcosto = bigDecimal.doubleValue();
	    }
	    catch(NumberFormatException NFEe) {
	    	pcosto = 0;
	    }
	    
	    String[] ponderados = new String[]{String.valueOf(pcosto),
	    								   String.valueOf(saldo),
	    								   String.valueOf(vsaldo)};

	    CacheEnlace.setPCosto(bd,idBodega,idProdServ,pcosto);
        CacheEnlace.setSaldoInventario(bd, idBodega,idProdServ,saldo);
        CacheEnlace.setVSaldoInventario(bd, idBodega,idProdServ,vsaldo);

	    return ponderados;
    }
    /**
     * Este metodo se encarga de actualizar los Saldos de la cache de saldos, una vez
     * el arreglo de registros para el movimiento fue generado.
     * @param record contiene el arrego de registros, necesario para sacar la bodega y
     * el codigo del producto.
     */
    
    private void actualizarSaldos(Object[] record) {
        CacheEnlace.setSaldoInventario(bd, (String)record[2], (String)record[3],saldo);
        CacheEnlace.setVSaldoInventario(bd, (String)record[2], (String)record[3],vsaldo);
    }
}