/**
 * 
 * LNContabilidad.java Creado 3/10/2005 
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
 * Esta clase es la encargada de Procesar la logica de negocios referente 
 * a contabilidad.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

package server.logicanegocios;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import server.basedatos.sql.CacheEnlace;
import server.basedatos.sql.DontHaveKeyException;
import server.basedatos.sql.RunQuery;
import server.basedatos.sql.SQLBadArgumentsException;
import server.basedatos.sql.SQLNotFoundException;

import org.jdom.Element;

public class LNContabilidad {

	//private Hashtable validCols;
	private Vector <String>colData;
	
	private Vector <String>colAccount;
	private int colIdProdServ = -1;
	
	private Vector <String>accountFields;
	private Vector <String>typeRegister;
	
	private int accountData;
	private int accountKey = -1;
	private int accountTh = -1;
	private int accountPS = -1;
	private Boolean naturaleza;
	
    private String bd;
	private String codeAPS;
	private String concepto="SOY UN LAMER QUE NO DEFINI EL CONCEPTO";
	private double base;
	private int colCost = -1;
	private boolean debug;
	
	private final boolean LIBRO_AUX_INV = false;
	private final boolean LIBRO_AUX_TER = true;
	
	
	/**
	 * Constructor utilizado cuando se recibe parametros desde una tabla
	 */
	public LNContabilidad(Element parameters,String bd) {
		this.bd = bd;
//		validCols = new Hashtable();
		colData = new Vector<String>();
		colAccount = new Vector<String>();
		accountFields = new Vector<String>();
		typeRegister = new Vector<String>();
		
        Iterator i = parameters.getChildren().iterator();
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String attribute = e.getAttributeValue("attribute");
            
            if (attribute!=null) {
            		attribute = attribute.toLowerCase();
            }
            
            if ("codeaps".equals(attribute)) {
            		codeAPS=e.getValue();
            } 
            else if ("base".equals(attribute)) {
                
            		/*
                 * Pendientre traer la base de la transaccion
                 */
            	
            		try {
        				base = 0;
                }
                catch(NumberFormatException NFEe) {
                		base = 0;
                }
                
            }
            else if ("concepto".equals(attribute)) {
            		concepto=e.getValue();
            }
            else if ("naturaleza".equals(attribute)) {
	            	if ("d".equals(e.getValue()) || "D".equals(e.getValue())) {
	            		naturaleza= new Boolean("true");
	            	}
	            	else if ("c".equals(e.getValue()) || "C".equals(e.getValue())) {
	            		naturaleza= new Boolean("false");
	            	}
            }
            	else if ("debuging".equals(attribute)) {
            		debug = Boolean.parseBoolean(e.getValue());
            	}
            /*
             * Se trae las columnas validas
             */
            else if ("subarg".equals(e.getName())){
	            	Iterator j = e.getChildren().iterator();
	            	while (j.hasNext()) {
	            		Element f = (Element)j.next();
	            		
	            		
	            		if ("accountCol".equals(f.getAttributeValue("attribute"))) {
	            			try {
		            			StringTokenizer STval = new StringTokenizer(f.getValue(),",");
		            			String account = STval.nextToken();
		            			String col = STval.nextToken();
		            			colData.addElement(col);
		            			colAccount.addElement(account);
	            			}
	            			catch(NoSuchElementException NSEEe) {
	            				NSEEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountCostCol".equals(f.getAttributeValue("attribute"))) {
	            			try {
		            			StringTokenizer STval = new StringTokenizer(f.getValue(),",");
		            			String account = STval.nextToken();
		            			String col = STval.nextToken();
		            			colData.addElement(col);
		            			colAccount.addElement(account);
		            			try {
		            				colCost = Integer.parseInt(col);
		            			}
		            			catch(NumberFormatException NFEe) {
		            				colCost = -1;
		            			}
	            			}
	            			catch(NoSuchElementException NSEEe) {
	            				NSEEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountField".equals(f.getAttributeValue("attribute"))) {
	            			try {
		            			StringTokenizer STval = new StringTokenizer(f.getValue(),",");
		            			String account = STval.nextToken();
		            			String register = STval.nextToken();
		            			accountFields.addElement(account);
		            			typeRegister.addElement(register);
	            			}
	            			catch(NoSuchElementException NSEEe) {
	            				NSEEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountKey".equals(f.getAttributeValue("attribute"))) {
	            			try {
	            				accountKey = Integer.parseInt(f.getValue());
	            			}
	            			catch(NumberFormatException NFEe) {
	            				accountKey=-1;
	            				NFEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountTh".equals(f.getAttributeValue("attribute"))) {
	            			try {
	            				accountTh = Integer.parseInt(f.getValue());
	            			}
	            			catch(NumberFormatException NFEe) {
	            				accountTh=-1;
	            				NFEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountPS".equals(f.getAttributeValue("attribute"))) {
	            			try {
	            				accountPS = Integer.parseInt(f.getValue());
	            			}
	            			catch(NumberFormatException NFEe) {
	            				accountPS=-1;
	            				NFEe.printStackTrace();
	            			}
	            		}
	            		else if ("accountData".equals(f.getAttributeValue("attribute"))) {
	            			try {
	            				accountData = Integer.parseInt(f.getValue());
	            			}
	            			catch(NumberFormatException NFEe) {
	            				accountData=0;
	            				NFEe.printStackTrace();
	            			}
	            		}
	
	            	}
            }
            
        }

	}
	
	/**
	 * Este metodo se encarga de generar parte de un asiento contable de la informacion
	 * obtenida de un <package><field/>1500</field></package>
	 * @param pack
	 * @return
	 * @throws DontHaveKeyException 
	 * @throws SQLException 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 */
	public double fieldData(Element pack) 
	throws DontHaveKeyException, SQLNotFoundException, SQLBadArgumentsException, SQLException {
		
		/*
		 * Se define una variable para constatar partida doble
		 */
		double partidaDoble = 0;
		/*
		 * Se define un iterador para recorrer todos los <fields/> del paquete recibido
		 */
		
		Iterator ipack = pack.getChildren().iterator();
		
		/*
		 * Por cada uno de los fields recibidos debe haber del lado de la parametrizacion una
		 * cuenta a afectar adicionada en el vector fields.
		 * Este ciclo recorrera todos los elementos del vector ValidFields y generara su respectivo
		 * registro en el libro axiliar.
		 */
		for (int i=0;i<accountFields.size();i++) {
			
			String account = accountFields.get(i);
			Element element = (Element)ipack.next();
			String value = element.getValue();
			double valueAccount;
			/*
			 * Se intenta convertir el valor del <field/> a double
			 */
			try {
				valueAccount = Double.parseDouble(value);
			}
			catch(NumberFormatException NFEe) {
				valueAccount = 0;
			}		
			
			/*
			 * Si valueAccount es mayor que 0 se procede a validar la informacion para generar 
			 * el registro
			 */
			
			if (valueAccount>0) {
				
				/*
				 * Se procede a verificar si se cumple con la base
				 */
			
				double baseAccount = CacheEnlace.getPCBase(bd,account.trim());
				/*
				 * Se verifica que el valor de la columna sea mayor a 0 y cumpla la base para
				 * generar el asiento
				 */
				if (baseAccount>=base) {
					
					/*
					 * Verificando si el asiento es debito o credito, esto esta en la asignacion
					 * de cuentas contables para cada producto
					 */
					
					/*
					 * Se valida el tipo de registro a efectuar dependiendo de la parametrizacion
					 */
					boolean debito;
					String type = typeRegister.get(i);
					if (type.equals("D") || type.equals("d")) {
						debito=true;
						
					}
					else {
						debito=false;
					}

					String idCta = CacheEnlace.getPCIdCta(bd,account);	
					
					/*
					 * Una vez verificado esto, se procede a generar el asiento dependiendo de su tipo
					 */
					
					/*
					 * Si el asiento es de una cuenta de terceros entonces ...
					 */
					if (CacheEnlace.isPCTerceros(bd,account)) {
						asientosConTipo(idCta,valueAccount,CacheKeys.getKey("idTercero"),debito,LIBRO_AUX_TER);
					} 
					/*
					 * Si el asiento es de una cuenta de inventarios entonces ...
					 */
					else if (CacheEnlace.isPCInventarios(bd,account)) {
						asientosConTipo(idCta,valueAccount,CacheKeys.getKey("idProdServ"),debito,LIBRO_AUX_INV);
					} 
					/*
					 * Si no es porque el asiento es de una cuenta de detalle, por tanto entonces ...
					 */
					else {
						asientosDetalle(idCta,valueAccount,debito);
					}
					
					/*
					 * Dependiendo del tipo de transaccion, se asigna el valor al vector correspondiente
					 */
					
					if (debito) {
						partidaDoble+=valueAccount;
					}
					else {
						partidaDoble-=valueAccount;
					}
				
				}
			}
			
			
		}
		return partidaDoble;
	}
	
	/**
	 * Este metodo se encarga de anular un documento, este proceso consiste en
	 * debitar los asientos acreditados y acreditar los asientos debitados.
	 * @param pack
	 */
	public void annulDocument(Element pack) {
		
	}
	/**
	 * Ete metodo se encarga de procesar la informaci�n recibida de una tabla,
	 * cuando viene la totalidad de la informacion necesaria para ser almacenada
	 * en los libros auxiliares.
	 * Nota: no se especifica naturaleza.
	 * @throws DontHaveKeyException 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws SQLException 
	 */
	
	public double rowDataAccount(Element pack) 
	throws DontHaveKeyException, SQLException, SQLNotFoundException, SQLBadArgumentsException {
		
		double partidaDoble = 0;
		Iterator i = pack.getChildren().iterator();

		/*
		 * El orden de la informaci�n sera ingresada de la siguiente forma
		 * 
		 * Infomacion obtenida de llaves
		 * 
		 * asiento[0] Numero de documento
		 * asiento[1] Fecha
		 * asiento[2] Concepto
		 * 
		 * Informacion obtenida del Elemento
		 * 
		 * asiento[3] centro costo
		 * asiento[4] cuenta contable
		 * asiento[5] tercero
		 * asiento[6] inventarios
		 * asiento[7] debe
		 * asiento[8] haber
		 * asiento[9] saldo
		 */
		String[] asiento = new String[10];
		asiento[0] = CacheKeys.getKey("ndocumento");
		asiento[1] = CacheKeys.getDate();
		asiento[2] = concepto;

		
		while (i.hasNext()) {
			Element elm = (Element)i.next();
			Iterator j = elm.getChildren().iterator();
			for(int k=3;j.hasNext();k++) {
				Element elm2 = (Element)j.next();
				asiento[k]=elm2.getText().trim();
			}
	
			/*
			 * Se reemplaza el numero de la cuenta contable por su id
			 */
			asiento[4]= CacheEnlace.getPCIdCta(bd,asiento[4]);
				
			double saldo = CacheEnlace.getSaldoLibroAux(bd,asiento[3],asiento[4],asiento[5],asiento[6]);
			double movimiento = 0;
			if (Double.parseDouble(asiento[7])!=0.0) {
				movimiento = Double.parseDouble(asiento[7]);
				double nsaldo = saldo+movimiento;
				asiento[9] = String.valueOf(nsaldo); 
				partidaDoble+= movimiento;
				LNUndoSaldos.setSaldoAntLibroAux(bd,asiento[3],asiento[4],asiento[5],asiento[6],new Double(saldo));
				CacheEnlace.setSaldoLibroAux(bd,asiento[3],asiento[4],asiento[5],asiento[6],new Double(nsaldo));
			}
			else {
				movimiento = Double.parseDouble(asiento[8]);
				double nsaldo = saldo-movimiento;
				asiento[9] = String.valueOf(nsaldo); 
				partidaDoble-= movimiento;
				LNUndoSaldos.setSaldoAntLibroAux(bd,asiento[3],asiento[4],asiento[5],asiento[6],new Double(saldo));
				CacheEnlace.setSaldoLibroAux(bd,asiento[3],asiento[4],asiento[5],asiento[6],new Double(nsaldo));
			}
	        /*
	         * Validando si el centro costo esta vacio, entonces el campo
	         * debe ser nulo
	         */
			if (asiento[3].equals("")) {
	        		asiento[3]="NULL";
	        }
			new RunQuery(bd,"INS0073",asiento).ejecutarSQL();

		}		

		if (debug) {
			System.out.println("partida doble: "+partidaDoble);
		}
		
		return partidaDoble;
	}
	/**
	 * Este metodo se encarga de procesar la informacion recibida de
	 * una tabla, cuando la tabla trae la cuenta contable
	 * @throws DontHaveKeyException 
	 * @throws SQLException 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws LNErrorProcecuteException 
	 */

	public double columDataAccount(Element pack) 
	throws DontHaveKeyException, SQLNotFoundException, SQLBadArgumentsException, SQLException, LNErrorProcecuteException {
	
		/*
		 * Se define una variable para constatar partida doble
		 */
		double partidaDoble = 0;
		
		/*
		 * Recorriendo las filas ....
		 */
		Iterator i = pack.getChildren().iterator();
		String idCta ="";
		String value="";
		double valueAccount = 0;
		
		while (i.hasNext()) {
			Element e = (Element)i.next();
			List lpack = e.getChildren();
			naturaleza.booleanValue();
			
			if (accountKey!=-1) {
				String charCta = ((Element)lpack.get(accountKey)).getValue();
				value = ((Element)lpack.get(accountData)).getValue();
				idCta = CacheEnlace.getPCIdCta(bd,charCta.trim());	
				
				/*
				 * Se intenta convertir el valor de la columna a numero
				 */
				try {
					valueAccount = Double.parseDouble(value);
				}
				catch(NumberFormatException NFEe) {
					valueAccount = 0;
				}
					
				//double baseAccount = CacheEnlace.getPCBase(bd,charCta.trim());

				/*
				 * Si el valor de la columna es mayor a 0 y cumpla la base para
				 * generar el asiento
				 
				if (valueAccount>0 && valueAccount>=baseAccount) {
				*/
				
				if (valueAccount>0) {
					
					/*
					 * Una vez verificado esto, se procede a generar el asiento dependiendo de su tipo
					 */
					
					/*
					 * Si el asiento es de una cuenta de terceros entonces ...
					 */
					if (CacheEnlace.isPCTerceros(bd,charCta.trim())) {
						if (accountTh>=0) {
								asientosConTipo(idCta,valueAccount,((Element)lpack.get(accountTh)).getValue(),naturaleza.booleanValue(),LIBRO_AUX_TER);
						}
						else {
							throw new LNErrorProcecuteException("");
						}
					} 
					/*
					 * Si el asiento es de una cuenta de inventarios entonces ...
					 */
					else if (CacheEnlace.isPCInventarios(bd,charCta.trim())) {
						if(accountPS>=0) {
							asientosConTipo(idCta,valueAccount,((Element)lpack.get(accountPS)).getValue(),naturaleza.booleanValue(),LIBRO_AUX_INV);
						}
						else {
							throw new LNErrorProcecuteException("");
						}
					} 
					/*
					 * Si no es porque el asiento es de una cuenta de detalle, por tanto entonces ...
					 */
					else {
						asientosDetalle(idCta,valueAccount,naturaleza.booleanValue());
					}
					
					/*
					 * Dependiendo del tipo de transaccion, se asigna el valor al vector correspondiente
					 */
					
					if (naturaleza.booleanValue()) {
						partidaDoble += valueAccount;
					}
					else {
						partidaDoble -= valueAccount;
					}
					if (debug) {
						System.out.println("partida doble: "+partidaDoble);
					}
				}
			}
			else {
				throw new LNErrorProcecuteException("");
			}
		
		}

		return partidaDoble;


	}

	/**
	 *  Este metodo se encarga de procesar la informacion recibida en una tabla
	 * @throws SQLException 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws DontHaveKeyException 
	 */
	
	public double columnData(Element pack) 
	throws SQLNotFoundException, SQLBadArgumentsException, SQLException, DontHaveKeyException {
		/*
		 * Cada columna de una fila, puede generar un asiento contable, la definicion de
		 * que asiento genera cada columna se parametriza como argumento en la definicion
		 * de la transaccion.
		 * 
		 * 
		 * Esta informacion se encuentra almacenada en la tabla perfil_cta; por tanto se debe cachear
		 * para agilizar la velocidad de las transacciones.
		 * 
		 * Otra informaci�n que debe estar cacheada son los asientos predefinidos de cada producto.
		 * 
		 */
		
		/*
		 * El proceso que se lleva para la generaci�n de un asiento contable es el siguiente:
		 */
		
		/*
		 * 1. Una tabla esta compuesta por varias filas y columnas, por tanto se hace un recorrido de
		 *    cada una de las columnas de una fila verificando si la columna analizada debe registrar
		 *    un asiento contable.
		 */
		
		Iterator i = pack.getChildren().iterator();
		
		double partidaDoble = 0.0;
		/*
		 * Recorriendo las filas ....
		 */


		while (i.hasNext()) {
			Element e = (Element)i.next();
			List colElement = e.getChildren();
			
			/*
			 * Se recorre las columnas validas para generar
			 * transacciones y se obtiene los datos de las columnas ...
			 */
			for (int k=0;k<colData.size();k++) {
				int col = Integer.parseInt(colData.get(k));
				Element element = (Element)colElement.get(col);

				String value = element.getValue();
				double valueAccount;
				
				/*
				 * Se intenta convertir el valor de la columna a numero
				 */
				try {
					valueAccount = Double.parseDouble(value);
				}
				catch(NumberFormatException NFEe) {
					valueAccount = 0;
				}
				

				String idProdServ="";
				boolean debito = false;
				String attribute = colAccount.get(k);
				/*
				 * Ser verifica si en las columnas viene especificado el
				 * codigo del producto, si es asi entonces se procede a 
				 * buscar cual es la columna para obtener el codigo del
				 * producto.
				 */
				
				if (getColIdProdServ()>-1) {
					System.out.println("Columna de prodServ: "+getColIdProdServ());
					Element eIdProdServ = (Element)colElement.get(getColIdProdServ());
					idProdServ = (String)eIdProdServ.getText();
				}
				else {
					idProdServ="";
				}
				
				/*
				 * Si la columna actual genera asiento y el valor de la columna es mayor a 0 entonces ...
				 */
				if (valueAccount>0 && !"IdProdServ".equals(attribute.trim())) {
					double baseAccount = CacheEnlace.getPCBase(bd,attribute.trim());

					if (naturaleza== null) {
						String code = CacheEnlace.getIdAsientosPr(bd,idProdServ,codeAPS);
						
						/*
						 * Luego con el codigo obtenido se procede a obtener el tipo de asiento
						 * para la cuenta a mover
						 */
						
						debito = CacheEnlace.isAsientoDebito(bd,code,attribute);
					}
					else {
						debito = naturaleza.booleanValue();
					}
					/*
					 * Se verifica que el valor de la columna sea mayor a 0 y cumpla la base para
					 * generar el asiento
					 */
					if (baseAccount>=base) {
						/*
						 * Si el atributo no es idProdServ es porque es un codigo de una cuenta contable,
						 * por tanto se procede a verificar si el tipo de asiento es debito o credito
						 */
						
							
							/*
							 * Verificando si el asiento es debito o credito, esto esta en la asignacion
							 * de cuentas contables para cada producto
							 */
							
							/*
							 * Primero se procede a obtener el codigo del asiento predefinido
							 */
							String idCta = CacheEnlace.getPCIdCta(bd,attribute);	

							
							
							/*
							 * Una vez verificado esto, se procede a generar el asiento dependiendo de su tipo
							 */
							
							/*
							 * Si el asiento es de una cuenta de inventarios entonces ...
							 */
							if (CacheEnlace.isPCInventarios(bd,attribute)) {
								/*
								 * Se verifica si en una de las columnas de la tabla viene especificado 
								 * el codigo del producto.
								 */
								if (colCost==col) {
									valueAccount = valueAccount * CacheEnlace.getPCosto(bd,CacheKeys.getKey("idBodega"),idProdServ);

								}
								
								asientosConTipo(idCta,valueAccount,idProdServ,debito,LIBRO_AUX_INV);
							} 
							else  if (CacheEnlace.isPCTerceros(bd,attribute)) {
								asientosConTipo(idCta,valueAccount,CacheKeys.getKey("idTercero"),debito,LIBRO_AUX_TER);
							}
							
							/*
							 * Si no es porque el asiento es de una cuenta de detalle, por tanto entonces ...
							 */
							else {
								asientosDetalle(idCta,valueAccount,debito);
							}
								
							
							/*
							 * Dependiendo del tipo de transaccion, se asigna el valor al vector correspondiente
							 */
							
							if (debito) {
								partidaDoble += valueAccount;
							}
							else {
								partidaDoble -= valueAccount;
							}
							/*
							 * Debuging
							 */
							if (debug) {
								System.out.println("partida doble:"+partidaDoble);
							}
						
					}
				}
			}
		
		}

		return partidaDoble;
		
	}
	
	private int getColIdProdServ() {
		if (colIdProdServ==-1) {
			for (int i=0;i<colAccount.size();i++) {
				if (colAccount.get(i).equals("IdProdServ")) {
					colIdProdServ=i;
					return colIdProdServ;
				}
			}
			return -1;
		}
		else {
			return colIdProdServ;
		}
	}
	
	/**
	 * Este metodo se encarga de generar asientos con centas de inventario o cuentas de terceros
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws SQLException 
	 */
	
	private void asientosConTipo(String idCta,double value,String idTipo,boolean debito,boolean tipo) 
	throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		/*
		 * Se define un arreglo de 8 campos para generar los argumentos del asiento
		 */
	
	//	String centro = CacheKeys.getKey("centro");
		double saldo;

		if (tipo==LIBRO_AUX_INV) {
			saldo = CacheEnlace.getSaldoLibroAux(bd,"",idCta,"",idTipo);
			LNUndoSaldos.setSaldoAntLibroAux(bd,"",idCta,"",idTipo,new Double(saldo));
		} 
		else {
			saldo = CacheEnlace.getSaldoLibroAux(bd,"",idCta,idTipo,"");
			LNUndoSaldos.setSaldoAntLibroAux(bd,"",idCta,idTipo,"",new Double(saldo));
		}
		
        double nsaldo;
        
        /*
         * Almacenando primer saldo obtenido de la cuenta en la transaccion
         */

        
        
        String[] asiento = new String[8];

		asiento[0] = idCta;
		asiento[1] = idTipo;
		asiento[2] = CacheKeys.getDate();
		asiento[3] = concepto;
		asiento[4] = CacheKeys.getKey("ndocumento");
		
		if (debito) {
			nsaldo=saldo+value;
			asiento[5] = String.valueOf(value);
			asiento[6] = "0";
			asiento[7] = String.valueOf(nsaldo);
		}
		else {
			nsaldo=saldo-value;
			asiento[5] = "0";
			asiento[6] = String.valueOf(value);
			asiento[7] = String.valueOf(nsaldo);
		}
		
		RunQuery RQsalidas;
		
		if (tipo==LIBRO_AUX_INV) {
			CacheEnlace.setSaldoLibroAux(bd,"",idCta,"",idTipo,new Double(nsaldo));
	        RQsalidas = new RunQuery(bd,"INS0043",asiento);
		} 
		else {
			CacheEnlace.setSaldoLibroAux(bd,"",idCta,idTipo,"",new Double(nsaldo));
	        RQsalidas = new RunQuery(bd,"INS0042",asiento);
		}
		
		/*
		 * Depurando ....
		 *
		System.out.println("Datos a almacenar");
		for (int i=0;i<asiento.length;i++) {
			System.out.println("Campo "+i+": "+asiento[i]);
		}
		*/
		RQsalidas.ejecutarSQL();
        
	}
	
	/**
	 * Este metodo se encarga de generar asientos de cuentas de detalle 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws SQLException 
	 */
	
	private void asientosDetalle(String idCta,double value,boolean debito) 
	throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		/*
		 * Se define un arreglo de 7 campos para generar los argumentos del asiento
		 */
	
        double saldo = CacheEnlace.getSaldoLibroAux(bd,"",idCta,"","");
        double nsaldo;
        
        /*
         * Almacenando primer saldo obtenido de la cuenta en la transaccion
         */

        LNUndoSaldos.setSaldoAntLibroAux(bd,"",idCta,"","",new Double(saldo));
        
        String[] asiento = new String[7];

		asiento[0] = idCta;
		asiento[1] = CacheKeys.getDate();
		asiento[2] = concepto;
		asiento[3] = CacheKeys.getKey("ndocumento");
		
		if (debito) {
			nsaldo=saldo+value;
			asiento[4] = String.valueOf(value);
			asiento[5] = "0";
		}
		else {
			nsaldo=saldo-value;
			asiento[4] = "0";
			asiento[5] = String.valueOf(value);
		}
		asiento[6] = String.valueOf(nsaldo);

		CacheEnlace.setSaldoLibroAux(bd,"",idCta,"","",new Double(nsaldo));
		
		/*
		 * Depurando ....
		 *
		System.out.println("Datos a almacenar");
		for (int i=0;i<asiento.length;i++) {
			System.out.println("Campo "+i+": "+asiento[i]);
		}
        */
		RunQuery RQsalidas = new RunQuery(bd,"INS0041",asiento);
        RQsalidas.ejecutarSQL();
		
	}
}
