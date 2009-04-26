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

package server.businessrules;

import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Element;

import server.businessrules.LNInventarios.RecoverData;
import server.database.connection.ConnectionsPool;
import server.database.sql.LinkingCache;
import server.database.sql.DontHaveKeyException;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

public class LNContabilidad {

	private Vector<String> colData;

	private Vector<String> colAccount;

	private int colIdProdServ = -1;

	private Vector<String> accountFields;

	private Vector<String> typeRegister;

	private int accountData;

	private int accountKey = -1;

	private int accountTh = -1;

	private int accountPS = -1;

	private Boolean naturaleza;

	private String bd;

	private String codeAPS;

	private String concepto = "SOY UN LAMER QUE NO DEFINI EL CONCEPTO";

	private String tercero;

	private String inventario;

	private String centroCosto;

	private String constantTh;
	
	private double base;

	private int colCost = -1;

	private boolean debug;
	
	private static Hashtable<String,Boolean> cacheNat = new Hashtable<String,Boolean>();
	private HashMap<Integer,RecoverData> recoverList = new HashMap<Integer,RecoverData>();

	private final boolean LIBRO_AUX_INV = false;

	private final boolean LIBRO_AUX_TER = true;

	public LNContabilidad (String bd) {
		this.bd=bd;
	}
	/**
	 * Constructor utilizado cuando se recibe parametros desde una tabla
	 */
	public LNContabilidad(Element parameters, String bd) {
		this.bd = bd;
		// validCols = new Hashtable();
		colData = new Vector<String>();
		colAccount = new Vector<String>();
		accountFields = new Vector<String>();
		typeRegister = new Vector<String>();

		Iterator i = parameters.getChildren().iterator();
		while (i.hasNext()) {
			Element e = (Element) i.next();
			String attribute = e.getAttributeValue("attribute");

			if (attribute != null) {
				attribute = attribute.toLowerCase();
			}

			if ("codeaps".equals(attribute)) {
				codeAPS = e.getValue();
			} else if ("base".equals(attribute)) {

				/*
				 * Pendientre traer la base de la transaccion
				 */

				try {
					base = 0;
				} catch (NumberFormatException NFEe) {
					base = 0;
				}

			} else if ("concepto".equals(attribute)) {
				concepto = e.getValue().trim();
			} else if ("naturaleza".equals(attribute)) {
				if ("d".equals(e.getValue()) || "D".equals(e.getValue())) {
					naturaleza = new Boolean("true");
				} else if ("c".equals(e.getValue()) || "C".equals(e.getValue())) {
					naturaleza = new Boolean("false");
				}
			} else if ("debuging".equals(attribute)) {
				debug = Boolean.parseBoolean(e.getValue());
			} else if ("tercero".equals(attribute)) {
				tercero = e.getValue().toLowerCase().trim();
			} else if ("inventario".equals(attribute)) {
				inventario = e.getValue().toLowerCase().trim();
			} else if ("centrocosto".equals(attribute)) {
				centroCosto = e.getValue().toLowerCase().trim();
			} else if ("constantth".equals(attribute)) {
				constantTh = e.getValue().toLowerCase().trim();
			}

			/*
			 * Se trae las columnas validas
			 */
			else if ("subarg".equals(e.getName())) {
				Iterator j = e.getChildren().iterator();
				while (j.hasNext()) {
					Element f = (Element) j.next();

					if ("accountCol".equals(f.getAttributeValue("attribute"))) {
						try {
							StringTokenizer STval = new StringTokenizer(f
									.getValue(), ",");
							String account = STval.nextToken();
							String col = STval.nextToken();
							colData.addElement(col);
							System.out.println("Account: "+col);
							if (account.equals("getName")) {
								System.out.println("Desde getName "+CacheKeys.getKey("account"));
								colAccount.addElement(CacheKeys.getKey("account"));
							}
							else {
								System.out.println("Su valor...");
								colAccount.addElement(account);
							}
						} catch (NoSuchElementException NSEEe) {
							NSEEe.printStackTrace();
						}
					} else if ("accountCostCol".equals(f
							.getAttributeValue("attribute"))) {
						try {
							StringTokenizer STval = new StringTokenizer(f
									.getValue(), ",");
							String account = STval.nextToken();
							String col = STval.nextToken();
							colData.addElement(col);
							colAccount.addElement(account);
							try {
								colCost = Integer.parseInt(col);
							} catch (NumberFormatException NFEe) {
								colCost = -1;
							}
						} catch (NoSuchElementException NSEEe) {
							NSEEe.printStackTrace();
						}
					} else if ("accountField".equals(f
							.getAttributeValue("attribute"))) {
						try {
							StringTokenizer STval = new StringTokenizer(f
									.getValue(), ",");
							String account = STval.nextToken();
							String register = STval.nextToken();
							accountFields.addElement(account);
							typeRegister.addElement(register);
						} catch (NoSuchElementException NSEEe) {
							NSEEe.printStackTrace();
						}
					} else if ("accountKey".equals(f
							.getAttributeValue("attribute"))) {
						try {
							accountKey = Integer.parseInt(f.getValue());
						} catch (NumberFormatException NFEe) {
							accountKey = -1;
							NFEe.printStackTrace();
						}
					} else if ("accountTh".equals(f
							.getAttributeValue("attribute"))) {
						try {
							accountTh = Integer.parseInt(f.getValue());
						} catch (NumberFormatException NFEe) {
							accountTh = -1;
							NFEe.printStackTrace();
						}
					} else if ("accountPS".equals(f
							.getAttributeValue("attribute"))) {
						try {
							accountPS = Integer.parseInt(f.getValue());
						} catch (NumberFormatException NFEe) {
							accountPS = -1;
							NFEe.printStackTrace();
						}
					} else if ("accountData".equals(f
							.getAttributeValue("attribute"))) {
						try {
							accountData = Integer.parseInt(f.getValue());
						} catch (NumberFormatException NFEe) {
							accountData = 0;
							NFEe.printStackTrace();
						}
					}

				}
			}

		}

	}

	/**
	 * Este metodo se encarga de generar parte de un asiento contable de la
	 * informacion obtenida de un <package><field/>1500</field></package>
	 * 
	 * @param pack
	 * @return
	 * @throws DontHaveKeyException
	 * @throws SQLException
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 */
	public double fieldData(Element pack) throws DontHaveKeyException,
			SQLNotFoundException, SQLBadArgumentsException, SQLException {
		
		

		/*
		 * Se define una variable para constatar partida doble
		 */
		double partidaDoble = 0;
		/*
		 * Se define un iterador para recorrer todos los <fields/> del paquete
		 * recibido
		 */

		Iterator ipack = pack.getChildren().iterator();

		/*
		 * Por cada uno de los fields recibidos debe haber del lado de la
		 * parametrizacion una cuenta a afectar adicionada en el vector fields.
		 * Este ciclo recorrera todos los elementos del vector ValidFields y
		 * generara su respectivo registro en el libro axiliar.
		 */

		for (int i = 0; i < accountFields.size(); i++) {

			String account = accountFields.get(i);
			Element element = (Element) ipack.next();
			String value = element.getValue();
			double valueAccount;
			/*
			 * Se intenta convertir el valor del <field/> a double
			 */
			try {
				valueAccount = Double.parseDouble(value);
			} catch (NumberFormatException NFEe) {
				valueAccount = 0;
			}

			/*
			 * Si valueAccount es mayor que 0 se procede a validar la
			 * informacion para generar el registro
			 */

			/*
			 * Se modifica la validacion valueAccount>0 por valueAccount!=0
			 * para despues verificar si el valor ingresado es negativo entonces
			 * cambiar el tipo de movimiento
			 * 
			 * Popayan 2007-09-09                                    pipelx
			 */
			if (valueAccount != 0) {

				/*
				 * Se procede a verificar si se cumple con la base
				 */

				double baseAccount = LinkingCache.getPCBase(bd, account.trim());
				/*
				 * Se verifica que el valor de la columna sea mayor a 0 y cumpla
				 * la base para generar el asiento
				 */
				if (baseAccount >= base) {

					/*
					 * Verificando si el asiento es debito o credito, esto esta
					 * en la asignacion de cuentas contables para cada producto
					 */

					/*
					 * Se valida el tipo de registro a efectuar dependiendo de
					 * la parametrizacion
					 */
					boolean debito;
					String type = typeRegister.get(i);
					if (type.equals("D") || type.equals("d")) {
						debito = true;

					} else {
						debito = false;
					}

					if (valueAccount<0) {
						valueAccount = Math.abs(valueAccount);
						debito=!debito;
					}
					
					String idCta = LinkingCache.getPCIdCta(bd, account);

					/*
					 * Una vez verificado esto, se procede a generar el asiento
					 * dependiendo de su tipo
					 */

					/*
					 * Si el asiento es de una cuenta de terceros entonces ...
					 */
					if (LinkingCache.isPCTerceros(bd, account)) {
						String th;
						if (constantTh!=null) {
							th=constantTh;
						}
						else {
							th = CacheKeys.getKey("idTercero");
						}
						System.out.println("tercero: "+th);
						asientosConTipo(idCta, 
										valueAccount, 
										th, 
										debito, LIBRO_AUX_TER,
										LinkingCache.isPCNaturaleza(bd,account));
					}
					/*
					 * Si el asiento es de una cuenta de inventarios entonces
					 * ...
					 */
					else if (LinkingCache.isPCInventarios(bd, account)) {
						asientosConTipo(idCta, 
										valueAccount, 
										CacheKeys.getKey("idProdServ"), 
										debito, 
										LIBRO_AUX_INV,
										LinkingCache.isPCNaturaleza(bd,account));
					}
					/*
					 * Si no es porque el asiento es de una cuenta de detalle,
					 * por tanto entonces ...
					 */
					else {
						asientosDetalle(idCta, 
										valueAccount, 
										debito,
										LinkingCache.isPCNaturaleza(bd,account));
					}

					/*
					 * Dependiendo del tipo de transaccion, se asigna el valor
					 * al vector correspondiente
					 */

					if (debito) {
						partidaDoble += valueAccount;
					} else {
						partidaDoble -= valueAccount;
					}

				}
			}

		}

		try {
			BigDecimal bigDecimal = new BigDecimal(partidaDoble);
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			partidaDoble = bigDecimal.doubleValue();
		} catch (NumberFormatException NFEe) {
		}

		return partidaDoble;
	}


	/**
	 * Ete metodo se encarga de procesar la informaci�n recibida de una tabla,
	 * cuando viene la totalidad de la informacion necesaria para ser almacenada
	 * en los libros auxiliares. Nota: no se especifica naturaleza.
	 * 
	 * @throws DontHaveKeyException
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 */

	public double rowDataAccount(Element pack) throws DontHaveKeyException,
			SQLException, SQLNotFoundException, SQLBadArgumentsException {

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
		 * 
		 * Información calculada
		 * 
		 * asiento[9] saldo
		 */

		String[] asiento = new String[10];
		asiento[0] = CacheKeys.getKey("ndocumento");
		asiento[1] = CacheKeys.getDate();
		asiento[2] = concepto;

		for (int k = 3; i.hasNext() && k<asiento.length; k++) {
			if (k == 3 && centroCosto != null) {
				if (centroCosto.equals("getname") && CacheKeys.getKey("centrocosto")!=null) {
					asiento[k] = CacheKeys.getKey("centrocosto");
				} else if (centroCosto.equals("notdata")) {
					asiento[k] = "NULL";
				} else {
					asiento[k] = "NULL";
				}
			} 
			/*
			 * Verificacion del tercero
			 */
			else if(k==5) {
				Element elm2 = (Element) i.next();
				asiento[k] = "NULL";
				if(LinkingCache.isPCTerceros(bd, asiento[4])) {
					/*
					 * Si el tercero viene en el paquete entonces se toma ese tercero
					 */
					if (!"".equals(elm2.getText())) {
						asiento[k] = elm2.getText().trim();
					}
					/*
					 * Si no entonces se toma el tercero de las llaves
					 */
					else if ("getname".equals(tercero) && CacheKeys.getKey("idTercero")!=null){
						asiento[k] = CacheKeys.getKey("idTercero");
					} 
					/*
					 * si en las llaves no viene tercero entonces el asientos es NULL
					 */
					else if (tercero.equals("notdata")) {
						asiento[k] = "NULL";
					} 
				}
			} else if (k == 6 && inventario != null) {
				if (inventario.equals("getname") && 
				    CacheKeys.getKey("inventario")!=null &&
				    LinkingCache.isPCInventarios(bd, asiento[4])) { 
					asiento[k] = CacheKeys.getKey("inventario");
				} else if (inventario.equals("notdata")) {
					asiento[k] = "NULL";
				} else {
					asiento[k] = "NULL";
				}
			} 
			else {
				Element elm2 = (Element) i.next();
				asiento[k] = elm2.getText().trim();
				/*
				System.out.println("indice "+k+" valor "+asiento[k]);
				*/
			}
			if (debug) {
				System.out.println("asiento[" + k + "] = " + asiento[k]);
			}
		}
		
		double debito 	= Double.parseDouble(asiento[7]);
		double credito 	= Double.parseDouble(asiento[8]);	

		if (debito<0) {
			asiento[7]="0";
			asiento[8]=String.valueOf(roundValue(Math.abs(debito)));
		} 
		else if (credito<0) {
			asiento[7]=String.valueOf(roundValue(Math.abs(credito)));
			asiento[8]="0";
		} 
		
		if (debug) {
			System.out.println("debito: [" + asiento[7] + "]  credito: ["+asiento[8]+"]");
		}
		double valor = Double.parseDouble(asiento[7]) + Double.parseDouble(asiento[8]);
		if (valor>0) {
			
			if (debug) {
				System.out.println("generando ...");
			}
			/*
			 * Se reemplaza el numero de la cuenta contable por su id
			 */
			asiento[4] = LinkingCache.getPCIdCta(bd, asiento[4]);
			double saldo = LinkingCache.getSaldoLibroAux(bd, 
														asiento[3].equals("NULL")?"":asiento[3],
														asiento[4],
														asiento[5].equals("NULL")?"":asiento[5], 
														asiento[6].equals("NULL")?"":asiento[6]);
			double movimiento = 0;
			if (Double.parseDouble(asiento[7]) != 0.0) {
				movimiento = Double.parseDouble(asiento[7]);
				double nsaldo = saldo + movimiento;
				asiento[9] = String.valueOf(nsaldo);
				partidaDoble += movimiento;
				LNUndoSaldos.setSaldoAntLibroAux(bd, 														
												asiento[3].equals("NULL")?"":asiento[3],
												asiento[4],
												asiento[5].equals("NULL")?"":asiento[5], 
												asiento[6].equals("NULL")?"":asiento[6],
												new Double(saldo));
				
				LinkingCache.setSaldoLibroAux(bd,
								asiento[3].equals("NULL")?"":asiento[3],
								asiento[4],
								asiento[5].equals("NULL")?"":asiento[5], 
								asiento[6].equals("NULL")?"":asiento[6],
						        new Double(nsaldo));
			} else {
				movimiento = Double.parseDouble(asiento[8]);
				double nsaldo = saldo - movimiento;
				asiento[9] = String.valueOf(nsaldo);
				partidaDoble -= movimiento;
				try {
					BigDecimal bigDecimal = new BigDecimal(partidaDoble);
					bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
					partidaDoble = bigDecimal.doubleValue();
				} catch (NumberFormatException NFEe) {
				}
				if (debug) {
					System.out.println("Movimiento: " + movimiento
							+ " Partida doble rowDataAccount: " + partidaDoble);
				}
				LNUndoSaldos.setSaldoAntLibroAux(bd,														
											asiento[3].equals("NULL")?"":asiento[3],
											asiento[4],
											asiento[5].equals("NULL")?"":asiento[5], 
											asiento[6].equals("NULL")?"":asiento[6],
											new Double(saldo));
											
				LinkingCache.setSaldoLibroAux(bd,														
											asiento[3].equals("NULL")?"":asiento[3],
											asiento[4],
											asiento[5].equals("NULL")?"":asiento[5], 
											asiento[6].equals("NULL")?"":asiento[6],
											new Double(nsaldo));
			}
		
			new QueryRunner(bd, "SCI0014", asiento).ejecutarSQL();
		}

		return partidaDoble;
	}

	/**
	 * Este metodo se encarga de procesar la informacion recibida de una tabla,
	 * cuando la tabla trae la cuenta contable
	 * 
	 * @throws DontHaveKeyException
	 * @throws SQLException
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws LNErrorProcecuteException
	 */

	public double columDataAccount(Element pack) throws DontHaveKeyException,
			SQLNotFoundException, SQLBadArgumentsException, SQLException,
			LNErrorProcecuteException {

		/*
		 * Se define una variable para constatar partida doble
		 */
		double partidaDoble = 0;

		String idCta = "";
		String value = "";
		double valueAccount = 0;

		List lpack = pack.getChildren();
		naturaleza.booleanValue();

		if (accountKey != -1) {
			String charCta = ((Element) lpack.get(accountKey)).getValue();
			value = ((Element) lpack.get(accountData)).getValue();
			idCta = LinkingCache.getPCIdCta(bd, charCta.trim());

			/*
			 * Se intenta convertir el valor de la columna a numero
			 */
			try {
				valueAccount = Double.parseDouble(value);
			} catch (NumberFormatException NFEe) {
				valueAccount = 0;
			}

			// double baseAccount = CacheEnlace.getPCBase(bd,charCta.trim());

			/*
			 * Si el valor de la columna es mayor a 0 y cumpla la base para
			 * generar el asiento
			 * 
			 * if (valueAccount>0 && valueAccount>=baseAccount) {
			 */

			/*
			 * Se cambia valueAccount>0 por valueAccount!=0 para luego validar 
			 * si el valor recibido es un negativo entonces se cambia el tipo
			 * de movimiento del asiento.
			 * 
			 * Popayan 2007-09-08                          pipelx
			 */
			
			if (valueAccount != 0) {

				/*
				 * Se verifica que el valor sea positivo, si no entonces se 
				 * invierte el tipo de movimiento y se pone positivo el valor
				 * del asiento.
				 */
				
				if (valueAccount<0) {
					valueAccount = Math.abs(valueAccount);
					naturaleza = !naturaleza;
				}
				/*
				 * Una vez verificado esto, se procede a generar el asiento
				 * dependiendo de su tipo
				 */

				/*
				 * Si el asiento es de una cuenta de terceros entonces ...
				 */
				if (LinkingCache.isPCTerceros(bd, charCta.trim())) {
					if (accountTh >= 0) {
						asientosConTipo(idCta, 
								valueAccount, 
								((Element) lpack.get(accountTh)).getValue(), 
								naturaleza.booleanValue(), 
								LIBRO_AUX_TER,
								LinkingCache.isPCNaturaleza(bd,charCta.trim()));
					}
					else if ("getname".equals(tercero) && CacheKeys.getKey("idTercero")!=null) {
						asientosConTipo(idCta, 
										valueAccount, 
										CacheKeys.getKey("idTercero"), 
										naturaleza.booleanValue(), 
										LIBRO_AUX_TER,
										LinkingCache.isPCNaturaleza(bd,charCta.trim()));
					} else {
						throw new LNErrorProcecuteException("");
					}
				}
				/*
				 * Si el asiento es de una cuenta de inventarios entonces ...
				 */
				else if (LinkingCache.isPCInventarios(bd, charCta.trim())) {
					if (accountPS >= 0) {
						asientosConTipo(idCta, 
										valueAccount, 
										((Element) lpack.get(accountPS)).getValue(), 
										naturaleza.booleanValue(), 
										LIBRO_AUX_INV,
										LinkingCache.isPCNaturaleza(bd,charCta.trim()));
					} else {
						throw new LNErrorProcecuteException("");
					}
				}
				/*
				 * Si no es porque el asiento es de una cuenta de detalle, por
				 * tanto entonces ...
				 */
				else {
					asientosDetalle(idCta, 
									valueAccount, 
									naturaleza.booleanValue(),
									LinkingCache.isPCNaturaleza(bd,charCta.trim()));
				}

				/*
				 * Dependiendo del tipo de transaccion, se asigna el valor al
				 * vector correspondiente
				 */

				if (naturaleza.booleanValue()) {
					partidaDoble += valueAccount;
				} else {
					partidaDoble -= valueAccount;
				}
				if (debug) {
					System.out.println("partida doble columnDataAccount: "
							+ partidaDoble);
				}
			}
		} else {
			throw new LNErrorProcecuteException("");
		}

		try {
			BigDecimal bigDecimal = new BigDecimal(partidaDoble);
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			partidaDoble = bigDecimal.doubleValue();
		} catch (NumberFormatException NFEe) {
		}

		return partidaDoble;

	}

	/**
	 * Este metodo se encarga de procesar la informacion recibida en una tabla
	 * 
	 * @throws SQLException
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws DontHaveKeyException
	 */

	public double columnData(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException, DontHaveKeyException {

		/*
		 * Cada columna de una fila, puede generar un asiento contable, la
		 * definicion de que asiento genera cada columna se parametriza como
		 * argumento en la definicion de la transaccion.
		 * 
		 * 
		 * Esta informacion se encuentra almacenada en la tabla perfil_cta; por
		 * tanto se debe cachear para agilizar la velocidad de las
		 * transacciones.
		 * 
		 * Otra informaci�n que debe estar cacheada son los asientos
		 * predefinidos de cada producto.
		 * 
		 */

		/*
		 * El proceso que se lleva para la generaci�n de un asiento contable es
		 * el siguiente:
		 */

		double partidaDoble = 0.0;
		List colElement = pack.getChildren();
		/*
		 * Se recorre las columnas validas para generar transacciones y se
		 * obtiene los datos de las columnas ...
		 */
		for (int k = 0; k < colData.size(); k++) {
			int col = Integer.parseInt(colData.get(k));
			Element element = (Element) colElement.get(col);

			String value = element.getValue();
			double valueAccount;

			/*
			 * Se intenta convertir el valor de la columna a numero
			 */
			try {
				valueAccount = Double.parseDouble(value);
			} catch (NumberFormatException NFEe) {
				valueAccount = 0;
			}

			String idProdServ = "";
			boolean debito = false;
			String account = colAccount.get(k);
			
			System.out.println("cuenta: "+account);

			/*
			 * Ser verifica si en las columnas viene especificado el codigo del
			 * producto, si es asi entonces se procede a buscar cual es la
			 * columna para obtener el codigo del producto.
			 */

			if (getColIdProdServ() > -1) {
				Element eIdProdServ = (Element) colElement
						.get(getColIdProdServ());
				idProdServ = (String) eIdProdServ.getText();
			} else {
				idProdServ = "";
			}

			boolean cuentaregistrada = true;
			/*
			 * Verificando si el asiento es debito o credito, esto esta
			 * en la asignacion de cuentas contables para cada producto
			 */

			if (naturaleza == null) {

				/*
				 * Se obtiene el codigo del asiento predefinido 
				 */
				String code = LinkingCache.getIdAsientosPr(bd, idProdServ,codeAPS);

				/*
				 * Luego con el codigo obtenido se procede a obtener el tipo
				 * de asiento para la cuenta a mover
				 */

				try {
					debito = LinkingCache.isAsientoDebito(bd, code, account);
				}
				catch (DontHaveKeyException DHKEe) {
					cuentaregistrada=false;
				}
			} else {
				debito = naturaleza.booleanValue();
			}

			if (debug) {
				System.out.println("La cuenta a procesar es: "+account);
				System.out.println("La cuenta esta registrada: "+cuentaregistrada);
				System.out.println("Valor del movimiento: "+valueAccount);
			}
			/*
			 * Si la columna actual genera asiento y el valor de la columna es
			 * mayor a 0 entonces ...
			 */
			
			/*
			 * Se modifica la condicion valueAccount>0 por valueAccount!=0 y luego se
			 * valida si el valor recibido es negativo, si es asi, entonces se cambia la naturaleza
			 * de la cuenta y el valor se pasa a positivo
			 * 
			 * Popayan, 2007-09-08                                      pipelx
			 */
			
			if (valueAccount != 0 && !"IdProdServ".equals(account.trim()) && cuentaregistrada) {
				double baseAccount = LinkingCache
						.getPCBase(bd, account.trim());

				/*
				 * Si el valor recibido es negativo, entonces lo pasamos a positivo y cambiamos la 
				 * naturaleza de la cuenta
				 */
				
				if (valueAccount<0) {
					valueAccount=Math.abs(valueAccount);
					debito = !debito;
				}
				/*
				 * Se verifica que el valor de la columna sea mayor a 0 y cumpla
				 * la base para generar el asiento
				 */
				if (baseAccount >= base) {
					/*
					 * Si el atributo no es idProdServ es porque es un codigo de
					 * una cuenta contable, por tanto se procede a verificar si
					 * el tipo de asiento es debito o credito
					 */

					/*
					 * Primero se procede a obtener el codigo del asiento
					 * predefinido
					 */
					String idCta = LinkingCache.getPCIdCta(bd, account);

					/*
					 * Una vez verificado esto, se procede a generar el asiento
					 * dependiendo de su tipo
					 */

					/*
					 * Si el asiento es de una cuenta de inventarios entonces
					 * ...
					 */
					if (LinkingCache.isPCInventarios(bd, account)) {
						/*
						 * Se verifica si en una de las columnas de la tabla
						 * viene especificado el codigo del producto.
						 */
						if (colCost == col) {
							String bodega ="";
							if (CacheKeys.getKey("bodegaSaliente")!=null) {
								bodega = CacheKeys.getKey("bodegaSaliente");
							}
							else { 
								bodega = CacheKeys.getKey("bodegaEntrante");
							}
							
							if (valueAccount==LinkingCache.getSaldoInventario(bd, bodega, idProdServ)) {
								valueAccount = LinkingCache.getVSaldoInventario(bd, bodega, idProdServ);
							}
							else {
								valueAccount = valueAccount	* LinkingCache.getPCosto(bd,bodega, idProdServ);
							}
							try {
								BigDecimal bigDecimal = new BigDecimal(
										valueAccount);
								bigDecimal = bigDecimal.setScale(2,
										BigDecimal.ROUND_HALF_UP);
								valueAccount = bigDecimal.doubleValue();
							} catch (NumberFormatException NFEe) {
							}

						}

						asientosConTipo(idCta, 
										valueAccount, 
										idProdServ,
										debito, 
										LIBRO_AUX_INV,
										LinkingCache.isPCNaturaleza(bd,account));
					} else if (LinkingCache.isPCTerceros(bd, account)) {
						String idTercero = CacheKeys.getKey("idTercero");
						if (idTercero== null) {
							idTercero = tercero;
						}
						asientosConTipo(idCta, 
										valueAccount, 
										idTercero, 
										debito, 
										LIBRO_AUX_TER,
										LinkingCache.isPCNaturaleza(bd,account));
					}

					/*
					 * Si no es porque el asiento es de una cuenta de detalle,
					 * por tanto entonces ...
					 */
					else {
						asientosDetalle(idCta, 
										valueAccount, 
										debito,
										LinkingCache.isPCNaturaleza(bd,account));
					}

					/*
					 * Dependiendo del tipo de transaccion, se asigna el valor
					 * al vector correspondiente
					 */

					if (debito) {
						partidaDoble += valueAccount;
					} else {
						partidaDoble -= valueAccount;
					}

					try {
						BigDecimal bigDecimal = new BigDecimal(partidaDoble);
						bigDecimal = bigDecimal.setScale(2,
								BigDecimal.ROUND_HALF_UP);
						partidaDoble = bigDecimal.doubleValue();
					} catch (NumberFormatException NFEe) {
					}

					/*
					 * Debuging
					 */
					if (debug) {
						System.out.println("partida doble columnData:"
								+ partidaDoble);
					}

				}
			}
		}

		return partidaDoble;

	}

	private int getColIdProdServ() {
		if (colIdProdServ == -1) {
			for (int i = 0; i < colAccount.size(); i++) {
				if (colAccount.get(i).equals("IdProdServ")) {
					colIdProdServ = i;
					return colIdProdServ;
				}
			}
			return -1;
		} else {
			return colIdProdServ;
		}
	}

	/**
	 * Este metodo se encarga de generar asientos con centas de inventario o
	 * cuentas de terceros
	 * 
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 * @throws DontHaveKeyException 
	 */

	private void asientosConTipo(String idCta, double value, String idTipo,
			boolean debito, boolean tipo,boolean natCta) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException, DontHaveKeyException {
		/*
		 * Se define un arreglo de 8 campos para generar los argumentos del
		 * asiento
		 */

		// String centro = CacheKeys.getKey("centro");
		double saldo;

		if (tipo == LIBRO_AUX_INV) {
			saldo = LinkingCache.getSaldoLibroAux(bd, "", idCta, "", idTipo);
			LNUndoSaldos.setSaldoAntLibroAux(bd, "", idCta, "", idTipo,
					new Double(saldo));
		} else {
			saldo = LinkingCache.getSaldoLibroAux(bd, "", idCta, idTipo, "");
			LNUndoSaldos.setSaldoAntLibroAux(bd, "", idCta, idTipo, "",
					new Double(saldo));
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
			asiento[5] = String.valueOf(value);
			asiento[6] = "0";
			if (natCta) {
				nsaldo = saldo + value;
			}
			else {
				nsaldo = saldo - value;
			}
		} else {
			asiento[5] = "0";
			asiento[6] = String.valueOf(value);
			if (natCta) {
				nsaldo = saldo - value;
			}
			else {
				nsaldo = saldo + value;
			}
		}
		

		try {
			BigDecimal bigDecimal = new BigDecimal(nsaldo);
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			nsaldo = bigDecimal.doubleValue();
		} catch (NumberFormatException NFEe) {
		}
		asiento[7] = String.valueOf(nsaldo);

		if (debug) {
			System.out.println("Datos a almacenar"); 
			for (int i=0;i<asiento.length;i++) {
			System.out.println("Campo "+i+": "+asiento[i]); }
		}
	
		QueryRunner RQsalidas;

		if (tipo == LIBRO_AUX_INV) {
			LinkingCache.setSaldoLibroAux(bd, "", idCta, "", idTipo, new Double(
					nsaldo));
			RQsalidas = new QueryRunner(bd, "SCI00O7", asiento);
		} else {
			LinkingCache.setSaldoLibroAux(bd, "", idCta, idTipo, "", new Double(
					nsaldo));
			RQsalidas = new QueryRunner(bd, "SCI00O6", asiento);
		}

		RQsalidas.ejecutarSQL();

	}

	/**
	 * Este metodo se encarga de generar asientos de cuentas de detalle
	 * 
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 * @throws DontHaveKeyException 
	 */

	private void asientosDetalle(String idCta, double value, boolean debito,boolean natCta)
			throws SQLNotFoundException, SQLBadArgumentsException, SQLException, DontHaveKeyException {
		/*
		 * Se define un arreglo de 7 campos para generar los argumentos del
		 * asiento
		 */

		double saldo = LinkingCache.getSaldoLibroAux(bd, "", idCta, "", "");
		double nsaldo;

		/*
		 * Almacenando primer saldo obtenido de la cuenta en la transaccion
		 */

		LNUndoSaldos.setSaldoAntLibroAux(bd, "", idCta, "", "", new Double(
				saldo));

		String[] asiento = new String[7];

		asiento[0] = idCta;
		asiento[1] = CacheKeys.getDate();
		asiento[2] = concepto;
		asiento[3] = CacheKeys.getKey("ndocumento");

		if (debito) {
			asiento[4] = String.valueOf(value);
			asiento[5] = "0";
			if (natCta) {
				nsaldo = saldo + value;
			}
			else {
				nsaldo = saldo - value;
			}
		} else {
			asiento[4] = "0";
			asiento[5] = String.valueOf(value);
			if (natCta) {
				nsaldo = saldo - value;
			}
			else {
				nsaldo = saldo + value;
			}
		}
		

		try {
			BigDecimal bigDecimal = new BigDecimal(nsaldo);
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			nsaldo = bigDecimal.doubleValue();
		} catch (NumberFormatException NFEe) {
		}

		asiento[6] = String.valueOf(nsaldo);

		LinkingCache.setSaldoLibroAux(bd, "", idCta, "", "", new Double(nsaldo));

		/*
		 * Depurando ....
		 * 
		 * System.out.println("Datos a almacenar"); for (int i=0;i<asiento.length;i++) {
		 * System.out.println("Campo "+i+": "+asiento[i]); }
		 */
		QueryRunner RQsalidas = new QueryRunner(bd, "SCI00O5", asiento);
		RQsalidas.ejecutarSQL();

	}

	/**
	 * Este metodo se encarga de anular un asiento contable de un documento
	 * 
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 * 
	 */

	public void anular() throws SQLNotFoundException, SQLBadArgumentsException,
			SQLException {

		String idDocumento = CacheKeys.getKey("ndocumento");
		QueryRunner RQdocumento = new QueryRunner(bd, "SCS0052",
				new String[] { idDocumento });
		ResultSet RSdatos = RQdocumento.ejecutarSELECT();
		/*
		 * La siguiente consulta trae los siguientes campos id_cta centro
		 * id_tercero id_prod_serv debe haber
		 */

		QueryRunner RQanular = new QueryRunner(bd, "SCU0005",new String[]{idDocumento});
		RQanular.ejecutarSQL();
		HashMap<Integer,RecoverData> recoverList = new HashMap<Integer,RecoverData>();
		
		for (int i=0;RSdatos.next();i++) {
			String idTercero = RSdatos.getString(3)==null?"-1":RSdatos.getString(3);
			String idProducto = RSdatos.getString(4)==null?"-1":RSdatos.getString(4);
			recoverList.put(i,new RecoverData(i,
					  RSdatos.getString(1),
					  RSdatos.getString(2),
					  idTercero,
					  idProducto,
					  RSdatos.getDouble(3)));
				recoverList.get(i).start();
		}
		
		while (recoverList.size()>0) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {}
		}

		RSdatos.close();
		RQdocumento.closeStatement();
	}

	public void recoverDocument() throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Calendar calendar = Calendar.getInstance();
		long init = calendar.getTimeInMillis();

		QueryRunner RQdocument = new QueryRunner(bd,"SCS0081",new String[]{CacheKeys.getKey("ndocumento")});
		ResultSet RSdocument = RQdocument.ejecutarSELECT();
		System.out.println("Recalculando editados contabilidad");
		HashMap<Integer,RecoverData> recoverList = new HashMap<Integer,RecoverData>();
		int i=0;
		for (;RSdocument.next();i++) {
			String idTercero = RSdocument.getString(3)==null?"-1":RSdocument.getString(3);
			String idProducto = RSdocument.getString(4)==null?"-1":RSdocument.getString(4);
			recoverList.put(i,new RecoverData(i,
					  RSdocument.getString(1),
					  RSdocument.getString(2),
					  idTercero,
					  idProducto,
					  RSdocument.getDouble(3)));
				recoverList.get(i).start();
		}

		while (recoverList.size()>0) {
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {}
		}

		QueryRunner RQdropDocument = new QueryRunner(bd,"SCS0086",new String[]{CacheKeys.getKey("ndocumento")});
		ResultSet RSdropDocument = RQdropDocument.ejecutarSELECT();
		System.out.println("Recalculando los elimiandos contabilidad");
		for (;RSdropDocument.next();i++) {
			String idTercero = RSdropDocument.getString(3)==null?"-1":RSdropDocument.getString(3);
			String idProducto = RSdropDocument.getString(4)==null?"-1":RSdropDocument.getString(4);
			recoverList.put(i,new RecoverData(i,
					  RSdocument.getString(1),
					  RSdocument.getString(2),
					  idTercero,
					  idProducto,
					  RSdocument.getDouble(3)));
				recoverList.get(i).start();

		}
	
		while (recoverList.size()>0) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {}
		}

		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0004",new String[]{});
		RQdpDocument.ejecutarSQL();
		
		calendar = Calendar.getInstance();
		long end = calendar.getTimeInMillis();
		System.out.println("Registros recalculados en " + (end-init)/1000 + " segundos ");

		RQdocument.closeStatement();
		RSdocument.close();
		RQdropDocument.closeStatement();
		RSdropDocument.close();
		RQdpDocument.closeStatement();
	}
	
	/**
	 * Metodo encargado de recalcular las cuentas de costos
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 * @throws SQLException
	 */
	
	public void recoverCost(Element pack) throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		QueryRunner RQdocument = new QueryRunner(bd,"SCS0087");
		ResultSet RSdocument = RQdocument.ejecutarSELECT();
		System.out.println("Recalculando costeo");
		while (RSdocument.next()) {
			//recoverData(null,RSdocument.getString(1),RSdocument.getString(2),RSdocument.getString(3),RSdocument.getDouble(4));
		}
		RSdocument.close();
		RQdocument.closeStatement();
	}
	
	/**
	 * Este metodo recalcula los saldos de las cuentas recibidas en el elemento, este elemento
	 * debe traer los siguientes argumentos
	 * fecha: 		Desde donde se empezara la recuperacion.
	 * id_cta: 		Cuenta a recuperar
	 * id_tercero: 	Si la cuenta es de tercero, entonces se tiene en cuenta este argumento.
	 * id_prod_serv:Si la cuenta es de productos, entonces se tiene en cuenta este argumento.
	 * @param pack Este elemento contiene los argumentos anteriores.
	 * @throws InterruptedException 
	 */
	
	private void runThread(final int i,String fecha,String idTercero,String idProducto) throws InterruptedException {
		class monitor extends Thread {
			public void run() {
				while (true) {
					System.out.println("Hilo Generado "+i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}
		}
		monitor m = new monitor();
		m.setPriority(Thread.MIN_PRIORITY);
		m.start();
		synchronized(recoverList) {
			if (recoverList.size()>=4) {
				try {
					recoverList.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			recoverList.put(i,new RecoverData(i,
					  null,
					  fecha,
					  idTercero,
					  idProducto,
					  0));
				//recoverList.get(i).setPriority(Thread.MAX_PRIORITY);
				recoverList.get(i).start();
		}
	
	}
	public void recover(Element pack) {
		try {
			Connection conn = ConnectionsPool.getMultiConnection(bd);

			QueryRunner RQdocument = new QueryRunner(bd,"SCS0094");
			ResultSet RSdocument = RQdocument.ejecutarMTSELECT(conn);
			int i=0;
				for (;RSdocument.next();i++) {
					String fecha = RSdocument.getString(1);
					String idTercero = RSdocument.getString(2)==null?"-1":RSdocument.getString(2);
					String idProducto = RSdocument.getString(3)==null?"-1":RSdocument.getString(3);
					runThread(i,fecha,idTercero,idProducto);
						//recoverList.notify();
				}
			while (recoverList.size()>0) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {}
			}
	
			RQdocument.closeStatement();
			RSdocument.close();
			ConnectionsPool.freeMultiConnection(bd, conn);
		}
		catch(SQLNotFoundException e) {
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recoverData(String fecha,String idCta,String idTercero,String idProducto,double saldoAnt,int hilo) {
		
		/*
		 * Si la fecha no es nula entonces se recuperara el auxiliar desde la fecha obtenida,
		 * si no entonces la recuperacion se hara desde el inicio del auxiliar.
		 */
		QueryRunner RQdata 		= null;
		ResultSet RSdata 		= null;
		QueryRunner RQnaturaleza= null;
		ResultSet RSnaturaleza  = null;
		Connection conn = null;
		try {
			if (LNDocuments.getActionDocument().equals(LNDocuments.EDIT_DOCUMENT) ||
					LNDocuments.getActionDocument().equals(LNDocuments.DELETE_DOCUMENT)) {
					fecha = CacheKeys.getMinDate();
			}
			
			if (fecha != null) {
				RQdata = new QueryRunner(bd, "SCS0075", new String[] { fecha,idCta,idTercero,idProducto });
			}
			
			/*
			 * Si no existe fecha inicial, entonces se consultan todos los
			 * registros del producto y bodega que se van a a actualizar.
			 */
			else {
				RQdata = new QueryRunner(bd, "SCS0072", new String[] { idCta,idTercero,idProducto });
			}
			
			boolean naturaleza = false;
			if (cacheNat.containsKey(idCta)) {
				naturaleza=cacheNat.get(idCta);
			}
			else {
				RQnaturaleza = new QueryRunner(bd,"SCS0073",new String[]{idCta});
				RSnaturaleza = RQnaturaleza.ejecutarSELECT();
				RSnaturaleza.next();
				naturaleza = RSnaturaleza.getBoolean(1);
				//System.out.println("Naturaleza consultada "+idCta);
				RQnaturaleza.closeStatement();
				RSnaturaleza.close();
				cacheNat.put(idCta, naturaleza);
			}
			//System.out.println("consultando conexiones");
			conn = ConnectionsPool.getMultiConnection(bd);
			//System.out.println("corriendo RSdata con "+conn);
			RSdata=RQdata.ejecutarMTSELECT(conn);
			double saldo=0;
			//System.out.println("Recalculando informacion de: Cta : "+idCta+" Tercero: "+idTercero+" Producto "+idProducto);
			if (naturaleza) {
				try {
					saldo=roundValue(recoverDebit(conn,hilo,saldoAnt,RSdata));
					LinkingCache.setSaldoLibroAux(bd,"",idCta,
							idTercero.equals("-1")?"":idTercero,
							idProducto.equals("-1")?"":idProducto,saldo);
				}
				catch(SQLException e) {
					System.out.println("ups "+hilo);
					if (conn!=null) {
						ConnectionsPool.freeMultiConnection(bd, conn);
				        conn=null;
					}
					recoverData(fecha,idCta,idTercero,idProducto,saldoAnt,hilo);
				}
			}
			else {
				try {
					saldo=roundValue(recoverCredit(conn,hilo,saldoAnt,RSdata));
					LinkingCache.setSaldoLibroAux(bd,"",idCta,
							idTercero.equals("-1")?"":idTercero,
							idProducto.equals("-1")?"":idProducto,saldo);
				}
				catch(SQLException e) {
					System.out.println("ups "+hilo);
					ConnectionsPool.freeMultiConnection(bd, conn);
			        conn=null;
					recoverData(fecha,idCta,idTercero,idProducto,saldoAnt,hilo);
				}
			}


			
		} catch (SQLNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				RQnaturaleza.closeStatement();
				RSnaturaleza.close();
				RQdata.closeStatement();
				RSdata.close();
			}
			catch(NullPointerException NPEe) {} 
			catch (SQLException e) {}
			try {
				RQnaturaleza.closeStatement();
				RSnaturaleza.close();
				RQdata.closeStatement();
				RSdata.close();
			}
			catch(NullPointerException NPEe) {}
			catch (SQLException e) {}
		}
		//System.out.println("finalizando..");
	}
	
	/**
	 * 
	 *  Metodo encargado de recuperar una cuenta de un libro auxiliar con 
	 *  naturaleza debito.
	 * @param saldoAnt Contiene el saldo inicial desde donde se debe tener en cuenta
	 * recuperacion.
	 * @param RSdata Contiene la informacion de la cuenta a recuperar.
	 * @param RQupdate Objeto encargado de hacer la actualizacion 
	 * @throws SQLException 
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 */
	private double recoverDebit(Connection conn,int hilo,double saldoAnt,ResultSet RSdata) 
	throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		//System.out.println("inciando up");
    	QueryRunner RQupdate = new QueryRunner(bd,"SCU0003");
		//System.out.print(" OK");

		String orden = null;
		double debe  = 0;
		double haber = 0;
		double saldo = saldoAnt;
		while (RSdata.next()) {
			//System.out.print("*"+hilo);
			orden = RSdata.getString(1);
			debe  = RSdata.getDouble(2);
			haber = RSdata.getDouble(3);
			saldo = roundValue(saldo + debe - haber);
			RQupdate.ejecutarSQL(conn,new String[] {
					String.valueOf(saldo),
					orden
				});
		}
		System.out.println("");
		RQupdate.closeStatement();
		ConnectionsPool.freeMultiConnection(bd, conn);
        conn=null;
    	return saldo;
	}
	
	/**
	 * Metodo encargado de recuperar una cuenta de un libro auxiliar con 
	 * naturaleza credito
	 * @param saldoAnt Contiene el saldo inicial desde donde se debe tener en cuenta
	 * recuperacion.
	 * @param RSdata Contiene la informacion de la cuenta a recuperar.
	 * @param RQupdate Objeto encargado de hacer la actualizacion 
	 * @throws SQLException
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 */
	private double recoverCredit(Connection conn,int hilo,double saldoAnt,ResultSet RSdata) 
	throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		//System.out.println("inciando up");
    	QueryRunner RQupdate = new QueryRunner(bd,"SCU0003");
		//System.out.print(" OK");
		String orden = null;
		double debe  = 0;
		double haber = 0;
		double saldo = saldoAnt;

    	while (RSdata.next()) {
			orden = RSdata.getString(1);
			debe  = RSdata.getDouble(2);
			haber = RSdata.getDouble(3);
			saldo = roundValue(saldo + haber - debe);
			//System.out.print("+"+hilo);
			RQupdate.ejecutarSQL(conn,new String[] {
					String.valueOf(saldo),
					orden
					});
		}
		System.out.println("");
    	RQupdate.closeStatement();
		ConnectionsPool.freeMultiConnection(bd, conn);
        conn=null;
    	
		return saldo;
	}
	
	/**
	 * Este metodo retorna el valor redondeado de un double
	 * @param value valor a retornar
	 * @return valor redondeado
	 */
	private double roundValue(Double value) {
		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}
	
	/**
	 * Este metodo se encarga de eliminar todos los registros de un documento del libro auxiliar.
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 * @throws SQLException
	 */

	public void deleteDocument() throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0004",new String[]{});
		QueryRunner RQdropDocument = new QueryRunner(bd,"SCS0084",new String[] { CacheKeys.getKey("ndocumento") });
		QueryRunner RQdeleteDocument = new QueryRunner(bd,"SCD0001",new String[] { CacheKeys.getKey("ndocumento") });
		//System.out.println("Eliminando auxiliar documento: "+CacheKeys.getKey("ndocumento"));
		RQdpDocument.ejecutarSQL();
		RQdropDocument.ejecutarSQL();
		RQdeleteDocument.ejecutarSQL();
		RQdpDocument.closeStatement();
		RQdropDocument.closeStatement();
		RQdeleteDocument.closeStatement();
	}
	
	class RecoverData extends Thread {

		String _fecha;
		String _cuenta;
		String _tercero;
		String _producto;
		//HashMap<Integer,RecoverData> recoverList;
		Integer index;
		double _saldoAnt;
		
		RecoverData(Integer index,String fecha,String cuenta,String tercero,String producto,double saldoAnt) {
			//this.recoverList=recoverList;
			this.index=index;
			this._saldoAnt=saldoAnt;
			this._fecha=fecha;
			this._cuenta=cuenta;
			this._tercero=tercero;
			this._producto=producto;
		}
		
		public void run() {
			//System.out.println("--------------Iniciando hilo "+index);
			recoverData(_fecha,_cuenta,_tercero,_producto,_saldoAnt,index);
			synchronized(recoverList) {
				recoverList.remove(index);
				recoverList.notify();
				//System.out.println("--------------Hilos pendientes: "+recoverList.size());
			}
		}
		
	}

}
