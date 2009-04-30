package server.businessrules;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

import server.businessrules.LNContabilidad.Monitor;
import server.businessrules.LNContabilidad.RecoverData;
import server.database.connection.ConnectionsPool;
import server.database.sql.LinkingCache;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.misc.language.Language;

/**
 * LNInventarios.java Creado el 25-jul-2005
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de procesar y reorganizar todo lo referente al
 * movimiento de Inventarios. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNInventarios {

	private String bd;

	private String tipoMovimiento;

	private final String ENTRADA = "entrada";

	private final String SALIDA = "salida";

	private final String AJUSTE = "ajuste";

	private final String NOTA = "nota";

	private final String RECOVER = "recover";

	private final String RECOVERDOC = "recoverdocument";

	private final String GASTOS = "gastosydescuentos";

	private final String ANULAR = "anular";
	
	private final String DELETE = "deletedocument";

	private HashMap<Integer,RecoverData> recoverList = new HashMap<Integer,RecoverData>();

	double saldo;

	double vsaldo;

	/**
	 * Este es el constructor de la clase, los parametros recibidos son un
	 * String que almacena el numero de columnas de las cuales tomara los
	 * valores para generar el movimiento y una variable boleana que le
	 * informara si se tendra en cuenta los datos de las llaves externas, o si
	 * no.
	 */

	public LNInventarios(Element parameters, String bd) {

		this.bd = bd;
		if (parameters != null) {
			Iterator i = parameters.getChildren().iterator();
			while (i.hasNext()) {
				Element e = (Element) i.next();
				String attribute = e.getAttributeValue("attribute");
				if (attribute.equals("tipoMovimiento")) {
					tipoMovimiento = e.getValue().trim().toLowerCase();
				}
			}
		}
	}

	/**
	 * Este metodo se llama cuando se va a generar una salida de inventarios
	 * 
	 * @param pack
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 * @throws SQLException
	 * @throws InterruptedException 
	 */

	public void movimientos(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException, InterruptedException {

		/*
		 * Se verifica si el paquete entregado contiene un solo registro
		 * <field/> o varios registros <package/>
		 * 
		 * Si tiene un solo registro entonces
		 */
		QueryRunner RQmovimiento = null;
		//System.out.print("tipo Movimiento: " + tipoMovimiento);
		if (ENTRADA.equals(tipoMovimiento)) {
			RQmovimiento = new QueryRunner(bd, "SCI00O8");
			String record[] = movimientoInventario(pack);
			/*
			 * Se valida que no se genere un asiento de inventario con cantidad
			 * 0 si la cantidad es 0 quiere decir que no hubo movimiento por
			 * tanto el asiento es descartado
			 */
			if (!(record[4].equals("0") || record[4].equals("0.0"))) {
				/*
				 * System.out.print("Registros: "); for (int i=0;i<record.length;i++) {
				 * System.out.println("registro "+i+" : "+record[i]); }
				 */RQmovimiento.ejecutarSQL(record);
				RQmovimiento.closeStatement();
			}
		} else if (SALIDA.equals(tipoMovimiento)) {
			RQmovimiento = new QueryRunner(bd, "SCI00O4");
			String record[] = movimientoInventario(pack);
			if (!(record[4].equals("0") || record[4].equals("0.0"))) {
				/*
				 * System.out.print("Registros: "); for (int i=0;i<record.length;i++) {
				 * System.out.println("registro "+i+" : "+record[i]); }
				 */RQmovimiento.ejecutarSQL(record);
				RQmovimiento.closeStatement();
			}
		} else if (GASTOS.equals(tipoMovimiento)) {
			gastosYdescuentos(pack);
		} else if (AJUSTE.equals(tipoMovimiento)) {
			ajustes(pack);
		} else if (ANULAR.equals(tipoMovimiento)) {
			anular();
		} else if (NOTA.equals(tipoMovimiento)) {
			nota(pack);
		} else if (RECOVER.equals(tipoMovimiento)) {
			recover();
		} else if (RECOVERDOC.equals(tipoMovimiento)) {
			recoverDocument();
		} else if (DELETE.equals(tipoMovimiento)) {
			deleteDocument();
		}
	}

	/**
	 * Este metodo hace un movimiento de inventarios apartir de una nota
	 * contable definiendo si es una entrada o una salida dependiendo si la nota
	 * debita o acredita una cuenta de la 14
	 * 
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 */

	private void nota(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException {

		QueryRunner RQmovimiento = null;
		/*
		 * REGISTROS DE UN MOVIMIENTO
		 * 
		 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
		 * record[3] = id_prod_serv record[4] = entrada | record[4] = salida
		 * record[5] = valor_ent | record[5] = valor_sal record[6] = pinventario
		 * record[7] = saldo record[8] = valor_saldo
		 */
		String record[] = new String[9];
		String cuenta = null;
		record[0] = CacheKeys.getDate();
		record[1] = CacheKeys.getKey("ndocumento");
		Iterator i = pack.getChildren().iterator();
		/*
		 * Este ciclo se encarga de sacar la informacion necesaria para generar
		 * una salida
		 */
		boolean movimiento = false;
		while (i.hasNext()) {
			Element field = (Element) i.next();
			String nameField = field.getAttributeValue("name");
			if (nameField != null) {
				nameField = nameField.toLowerCase();
				if ("cuenta".equals(nameField)) {
					cuenta = field.getValue();
					if (!(cuenta.startsWith("14") && Integer.parseInt(cuenta.substring(4, 6))<90)) {
						return;
					} else {
					}
				}
				if ("bodega".equals(nameField)) {
					record[2] = field.getValue();
				} else if ("idproducto".equals(nameField)) {
					record[3] = field.getValue();
				} else if ("cantidad".equals(nameField)) {
					record[4] = field.getValue();
				} else if ("creditos".equals(nameField)
						&& !field.getValue().startsWith("0")) {
					RQmovimiento = new QueryRunner(bd, "SCI00O4");
					record[5] = field.getValue();
				} else if ("debitos".equals(nameField)
						&& !field.getValue().startsWith("0")) {
					RQmovimiento = new QueryRunner(bd, "SCI00O8");
					record[5] = field.getValue();
					movimiento = true;
				}
			}
		}
		/*
		 * Se captura los saldos antes del movimiento
		 */
		saldo = LinkingCache.getSaldoInventario(bd, record[2], record[3]);
		vsaldo = LinkingCache.getVSaldoInventario(bd, record[2], record[3]);

		/*
		 * Se almacena primer saldo de la transaccion
		 */

		LNUndoSaldos.setSaldoAntInv(bd, record[2], record[3], saldo);
		String[] ponderado = null;
		double cantidad = Double.parseDouble(record[4]);
		double entrada = cantidad > 0 ? Double.parseDouble(record[5])
				/ cantidad : 0;
		BigDecimal bigDecimal = new BigDecimal(entrada);
		bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		record[5] = String.valueOf(bigDecimal.doubleValue());
		if (movimiento) {
			ponderado = ponderar(record[2], record[3], cantidad, bigDecimal
					.doubleValue());
		} else {
			ponderado = ponderar(record[2], record[3], cantidad * -1,
					bigDecimal.doubleValue());

		}
		record[6] = ponderado[0];
		record[7] = ponderado[1];
		record[8] = ponderado[2];

		if (!(record[4].equals("0") || record[4].equals("0.0"))) {
			RQmovimiento.ejecutarSQL(record);
			RQmovimiento.closeStatement();
		}
	}

	/**
	 * Este metodo genera ajustes automaticos apartir de la cantidad,
	 * dependiendo si el valor es positivo o negativo decide si el movimiento de
	 * ajuste sera una entrada o una salida
	 * 
	 * @param pack
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 * @throws SQLException
	 */
	private void ajustes(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException {
		String record[] = movimientoInventario(pack);
		QueryRunner RQmovimiento = null;
		if (SALIDA.equals(tipoMovimiento)) {
			RQmovimiento = new QueryRunner(bd, "SCI00O4");
		} else if (ENTRADA.equals(tipoMovimiento)) {
			RQmovimiento = new QueryRunner(bd, "SCI00O8");
		}

		if (!(record[4].equals("0") || record[4].equals("0.0"))) {
			RQmovimiento.ejecutarSQL(record);
			RQmovimiento.closeStatement();
		}
	}

	/**
	 * Este metodo se encarga de efectuar traslados entre diferentes bodegas.
	 */

	public void traslados(Element pack) throws LNErrorProcecuteException,
			SQLBadArgumentsException, SQLBadArgumentsException,
			SQLNotFoundException, SQLException {
		QueryRunner RQentrada = null;
		QueryRunner RQsalida = null;
		try {
			tipoMovimiento = SALIDA;
			String[] records = movimientoInventario(pack);
			RQsalida = new QueryRunner(bd, "SCI00O4");
			RQsalida.ejecutarSQL(records);
			CacheKeys.setKey("valorEntrada", records[6]);
			tipoMovimiento = ENTRADA;
			String[] ventradas = movimientoInventario(pack);
			RQentrada = new QueryRunner(bd, "SCI00O8");
			RQentrada.ejecutarSQL(ventradas);
			RQentrada.closeStatement();
			RQsalida.closeStatement();
		} catch (NumberFormatException NFEe) {
			RQentrada.closeStatement();
			RQsalida.closeStatement();
			throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
		} catch (IndexOutOfBoundsException IOOBEe) {
			RQentrada.closeStatement();
			RQsalida.closeStatement();
			IOOBEe.printStackTrace();
			throw new LNErrorProcecuteException(Language.getWord("ERR_ARGS"));
		} catch (NullPointerException NPEe) {
			RQsalida.closeStatement();
			throw new LNErrorProcecuteException(Language.getWord("ERR_DATA"));
		}
	}

	/**
	 * Este metodo analiza la informaci�n del paquete pack y aplica la logica de
	 * negocios correspondiente para generar una entrada de inventarios
	 * 
	 * @param pack
	 *            Este objeto contiene la informaci�n necesaria para generar el
	 *            movimiento
	 * @return Retorna un objeto String listo para ser almacenado en la base de
	 *         datos.
	 */

	private synchronized String[] movimientoInventario(Element pack) {// ,String
		// movimiento)
		// {

		/*
		 * REGISTROS DE UN MOVIMIENTO
		 * 
		 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
		 * record[3] = id_prod_serv record[4] = entrada | record[4] = salida
		 * record[5] = valor_ent | record[5] = valor_sal record[6] = pinventario
		 * record[7] = saldo record[8] = valor_saldo
		 */

		String[] record = new String[9];
		int conversion = 1;
		/*
		 * Se carga los datos iniciales
		 */
		if (tipoMovimiento.equals(ENTRADA)) {
			record = infoMovimiento(pack, "bodegaEntrante");
		} else if (tipoMovimiento.equals(SALIDA)) {
			record = infoMovimiento(pack, "bodegaSaliente");
			conversion = -1;
		} else if (tipoMovimiento.equals(AJUSTE)) {
			record = infoMovimiento(pack, "bodegaAjuste");
		}

		/*
		 * Si el registro 4 del arreglo es 0 quiere decir que no tiene cantidad,
		 * por tanto se procesa el codigo restante del metodo y se retorna el
		 * arreglo.
		 */
		if (record[4].equals("0") || record[4].equals("0.0")) {
			return record;
		}

		if (tipoMovimiento.equals(AJUSTE)) {
			double _cantidad = Double.parseDouble(record[4]);
			if (_cantidad > 0) {
				tipoMovimiento = SALIDA;
				record[5] = record[5]==null || record[5]=="0" || record[5]=="0.0"?String.valueOf(LinkingCache.getPCosto(bd,	record[2], record[3])):record[5];
				conversion = -1;
			} else {
				tipoMovimiento = ENTRADA;
				record[4] = String.valueOf(_cantidad * -1);
			}
		}

		/*
		 * Se verifica si record[5] tiene valor, en caso de que no lo tenga es
		 * porque se esta generando un traslado, por tanto este tubo que haber
		 * sido almacenado con anterioridad en el cache de llaves
		 */

		if (tipoMovimiento.equals(ENTRADA)) {
			if (record[5] == null) {
				record[5] = CacheKeys.getKey("valorEntrada") == null ? "0.0"
						: CacheKeys.getKey("valorEntrada");
			}
		} else {
			if (record[5] == null) {
				record[5] = String.valueOf(LinkingCache.getPCosto(bd,
						record[2], record[3]));
			}
		}

		double cantidad = Double.parseDouble(record[4]);
		double valor = Double.parseDouble(record[5]);

		String[] ponderado = ponderar(record[2], record[3], cantidad
				* conversion, valor);

		record[6] = ponderado[0];
		record[7] = ponderado[1];
		record[8] = ponderado[2];

		return record;
	}

	private void gastosYdescuentos(Element pack) throws SQLException,
			SQLNotFoundException, SQLBadArgumentsException {
		/*
		 * REGISTRO DE UN GASTO O UN DESCUENTO
		 * 
		 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
		 * record[3] = id_prod_serv record[4] = valor_ent record[5] =
		 * pinventario record[6] = saldo record[7] = valor_saldo
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
			Element field = (Element) i.next();
			String nameField = field.getAttributeValue("name");
			if (nameField != null) {
				nameField = nameField.toLowerCase();
				if ("bodegaentrante".equals(nameField)) {
					record[2] = field.getValue();
				} else if ("idproducto".equals(nameField)) {
					record[3] = field.getValue();
				} else if ("gastos".equals(nameField)
						|| "descuentos".equals(nameField)) {
					try {
						double gd = Double.parseDouble(field.getValue());
						if (gd > 0) {
							if ("gastos".equals(nameField)) {
								gastos.addElement(new Double(gd));

							} else {
								double descuento = gd * -1;
								gastos.addElement(new Double(descuento));
							}
						}
					} catch (NumberFormatException NFEe) {
					}
				}
			}
		}
		QueryRunner RQmovimiento = new QueryRunner(bd, "SCI0013");

		if (gastos.size() > 0) {
			for (int j = 0; j < gastos.size(); j++) {
				/*
				 * Se captura los saldos antes del movimiento
				 */
				saldo = LinkingCache.getSaldoInventario(bd, record[2],
						record[3]);
				vsaldo = LinkingCache.getVSaldoInventario(bd, record[2],
						record[3]);

				/*
				 * Se carga los datos iniciales
				 */

				double ventrada = gastos.get(j).doubleValue();
				vsaldo += ventrada;
				double pcosto = saldo > 0 ? (vsaldo / saldo) : 0;
				BigDecimal bigDecimal = new BigDecimal(pcosto);
				bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
				pcosto = bigDecimal.doubleValue();

				record[4] = String.valueOf(ventrada);
				record[5] = String.valueOf(pcosto);
				record[6] = String.valueOf(saldo);
				record[7] = String.valueOf(vsaldo);
				RQmovimiento.ejecutarSQL(record);
				LinkingCache.setPCosto(bd, record[2], record[3], pcosto);
				actualizarSaldos(record);
			}
			RQmovimiento.closeStatement();
		}
	}

	/**
	 * Este metodo retorna los datos iniciales de un movimiento de inventario
	 * 
	 * @return retorna un arreglo con los datos iniciales
	 */
	private String[] infoMovimiento(Element pack, String bodega) {
		/*
		 * Todo movimiento de inventarios tiene unos datos iniciales que son:
		 * 
		 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
		 * record[3] = id_prod_serv record[4] = entrada || salida record[5] =
		 * pcosto
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
			Element field = (Element) i.next();
			String nameField = field.getAttributeValue("name");
			if (nameField != null) {
				nameField = nameField.toLowerCase();
				if (bodega != null && bodega.toLowerCase().equals(nameField)) {
					record[2] = field.getValue();
					CacheKeys.setKey("idBodega", field.getValue());
				} else if ("idproducto".equals(nameField)) {
					record[3] = field.getValue();
				} else if ("cantidad".equals(nameField)) {
					record[4] = field.getValue();
				} else if ("pcosto".equals(nameField)) {
					record[5] = field.getValue();
				}
			}
		}

		/*
		 * Se captura los saldos antes del movimiento
		 */
		saldo = LinkingCache.getSaldoInventario(bd, record[2], record[3]);
		vsaldo = LinkingCache.getVSaldoInventario(bd, record[2], record[3]);

		/*
		 * Se almacena primer saldo de la transaccion
		 */

		LNUndoSaldos.setSaldoAntInv(bd, record[2], record[3], saldo);

		return record;
	}

	/**
	 * Este metodo se encarga de deshacer un movimiento apartir del codigo del
	 * documento, su funcion es sacar del inventario los productos que entraron
	 * y meter los productos que salieron, a sus correspondientes precios de
	 * costo con el que se genero el movimiento.
	 * 
	 * @throws SQLException
	 * @throws SQLBadArgumentsException
	 * @throws SQLNotFoundException
	 */

	private void anular() throws SQLException, SQLNotFoundException,
			SQLBadArgumentsException {
		QueryRunner RQanular = new QueryRunner(bd, "SCU0006",new String[]{CacheKeys.getKey("ndocumento")});
		RQanular.ejecutarSQL();
		recoverAux("SCS0051",new String[]{CacheKeys.getKey("ndocumento")},true);
	}

	private String[] ponderar(String idBodega, String idProdServ,
			double cantidad, double valor) {
		double saldoAnt = saldo;
		saldo += cantidad;
		if ((cantidad<0 && saldoAnt==Math.abs(cantidad)) || (valor<0 && cantidad==saldoAnt)) {
			vsaldo=0;
		}
		else {
			vsaldo+=(cantidad * valor);
		}
		double pcosto = (vsaldo / saldo);
		try {
			BigDecimal bigDecimal = new BigDecimal(pcosto);
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			pcosto = bigDecimal.doubleValue();
		} catch (NumberFormatException NFEe) {
			pcosto = 0;
		}

		String[] ponderados = new String[] { String.valueOf(pcosto),
				String.valueOf(saldo), String.valueOf(vsaldo) };

		LinkingCache.setPCosto(bd, idBodega, idProdServ, pcosto);
		LinkingCache.setSaldoInventario(bd, idBodega, idProdServ, saldo);
		LinkingCache.setVSaldoInventario(bd, idBodega, idProdServ, vsaldo);

		return ponderados;
	}

	/**
	 * Este metodo se encarga de actualizar los Saldos de la cache de saldos,
	 * una vez el arreglo de registros para el movimiento fue generado.
	 * 
	 * @param record
	 *            contiene el arrego de registros, necesario para sacar la
	 *            bodega y el codigo del producto.
	 */

	private void actualizarSaldos(Object[] record) {
		LinkingCache.setSaldoInventario(bd, (String) record[2],
				(String) record[3], saldo);
		LinkingCache.setVSaldoInventario(bd, (String) record[2],
				(String) record[3], vsaldo);
	}

	public void recoverDocument() throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		
		recoverAux("SCS0082",new String[]{CacheKeys.getKey("ndocumento")},true);
		recoverAux("SCS0085",new String[]{CacheKeys.getKey("ndocumento")},true);
		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0003",new String[]{});
		RQdpDocument.ejecutarSQL();
		RQdpDocument.closeStatement();
	}

	public void recover() throws SQLNotFoundException, SQLBadArgumentsException, SQLException, InterruptedException {
		
		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0003",new String[]{});
		recoverAux("SCS0090",null,false);
		RQdpDocument.ejecutarSQL();
		RQdpDocument.closeStatement();
		
	}

	private void recoverAux(String sql,String args[],boolean transaction) {
		Calendar calendar = Calendar.getInstance();
		long init = calendar.getTimeInMillis();

		Monitor m = new Monitor();
		m.setPriority(Thread.MIN_PRIORITY);
		m.start();
		try {
			Connection conn = null;
			if (transaction) {
				conn = ConnectionsPool.getConnection(bd);
			}
			else {
				conn = ConnectionsPool.getMultiConnection(bd);
			}
			
			QueryRunner RQdocument = null;
			if (args==null) {
				RQdocument =new QueryRunner(bd,sql);
			}
			else {
				RQdocument =new QueryRunner(bd,sql,args);
			}
			ResultSet RSdocument = RQdocument.ejecutarMTSELECT(conn);
			int i=0;
			for (;RSdocument.next();i++) {
				m.setI(i);
				runThread(i,RSdocument.getString(1),RSdocument.getString(2),RSdocument.getString(3),RSdocument.getDouble(4),RSdocument.getDouble(5),transaction);
			}

			calendar = Calendar.getInstance();
			long end = calendar.getTimeInMillis();

			synchronized(recoverList) {
				while (recoverList.size()>0) {
					try {
						recoverList.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
			System.out.println("Inventario recosteado en " + (end-init)/1000 + " segundos ");
			
			m.interrupt();
			RQdocument.closeStatement();
			RSdocument.close();
			if (!transaction) {
				ConnectionsPool.freeMultiConnection(bd, conn);
			}
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

	private void runThread(int i,String fecha,String bodega,String idProducto,double saldo,double valorSaldo,boolean transaction) throws InterruptedException {
		synchronized(recoverList) {
			if (recoverList.size()>=20) {
				try {
					recoverList.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			recoverList.put(i,new RecoverData(recoverList,
					  i,
					  fecha,
					  bodega,
					  idProducto,
					  saldo,
					  valorSaldo,
					  transaction));
				//recoverList.get(i).setPriority(Thread.MAX_PRIORITY);
				recoverList.get(i).start();
		}
	
	}

	private void recoverData(String fecha,String idBodega, String idProducto,double saldoAnt,double valorSaldoAnt,boolean transaction) {
		String orden       ="";
		String rfDocumento ="";
		String tipoDocumento ="";
		boolean estado = true;
		double pinventario =0;
		double hpinventario=0;
		double entrada     =0;
		double valorEntrada=0;
		double salida      =0;
		double valorSalida =0;
		boolean ponderar = false;
		Hashtable<String,DataInventory> historyInv = new Hashtable<String,DataInventory>();

		QueryRunner RQdata = null;
		Connection conn = null;
		if (transaction) {
			conn = ConnectionsPool.getConnection(bd);
		}
		else {
			conn = ConnectionsPool.getMultiConnection(bd);
		}
		/*
		 * Consulta de saldo anterior a partir de la fecha inicial y los
		 * registros a actualizar a partir de la fecha inicial.
		 */
		try {
			if (fecha != null) {
				RQdata = new QueryRunner(bd, "SCS0077", new String[] { fecha,idBodega, idProducto });
				/*System.out.println("-----------------------------------------------------------");
				System.out.println("Consultando saldos de: "+fecha+"-"+idBodega+"-"+idProducto);
				System.out.println("pinventario: "+pinventario);
				System.out.println("Saldo anterior: "+saldoAnt);
				System.out.println("Valor Sdo Ant: "+valorSaldoAnt);
				*/
			}
			else if (LNDocuments.getActionDocument().equals(LNDocuments.EDIT_DOCUMENT) ||
					LNDocuments.getActionDocument().equals(LNDocuments.DELETE_DOCUMENT)) {
						fecha = CacheKeys.getMinDate();
			}
			/*
			 * Si no existe fecha inicial, entonces se consultan todos los
			 * registros del producto y bodega que se van a a actualizar.
			 */
			else {
				//System.out.println("Argumentos: "+idBodega+","+idProducto);
				RQdata = new QueryRunner(bd, "SCS0071", new String[] { idBodega,
						idProducto });
			}

			ResultSet RSdata = RQdata.ejecutarMTSELECT(conn);
			QueryRunner RQupdate = new QueryRunner(bd,"SCU0002");
			/*
			 * Se recorre el producto a actualizar
			 */
			while (RSdata.next()) {
				ponderar=true;
				/*
				 * Obteniendo informacion del producto para generar nuevos saldos y pinventario
				 */
				
				orden = RSdata.getString(1);
				rfDocumento = RSdata.getString(2);
				tipoDocumento = RSdata.getString(3);
				hpinventario = RSdata.getDouble(4);
				estado = RSdata.getBoolean(5);
				entrada = RSdata.getDouble(6);
				valorEntrada = RSdata.getDouble(7);
				salida = RSdata.getDouble(8);
				valorSalida=RSdata.getDouble(9);

				/*
				 * Si es salida entonces ....
				 */
				if (salida!=0) {
					//System.out.println("Sacando ..");
					/*
					 * Si esta anulada
					 */
					if (!estado) {
						salida = 0;
						valorSalida = 0;
					}
					/*
					 * Si no esta anulada y es una devolucion en compras entonces debe salir al valor al que se 
					 * compro, esto lo sabemos por el rfdocumento siempre y cuando la devolucion se cruce con la
					 * compra, si no el valor de salida debe ser el ponderado.
					 */
					else if (tipoDocumento.equals("DC")) {
						//System.out.println("Salida.. devolucion en compras");
						/*
						 * Verificamos si el valor de la compra esta registrado en el historial y tomamos como valor
						 * de la salida el mismo valor del ingreso
						 */
						if (rfDocumento==null) {
							valorSalida = pinventario;
						}
						else if (historyInv.containsKey(rfDocumento)) {
							valorSalida = historyInv.get(rfDocumento).getValorEntrada();
							//System.out.println("lo encontro  "+valorSalida);
						}
						/*
						 * si no esta en el historial, toca consultar el valor de salida en la base de datos.
						 */
						else {
							valorSalida = getDBValue(conn,"SCS0078",rfDocumento,idProducto);
							//System.out.println("no encontro nada "+valorSalida+" rf: "+rfDocumento+" idProducto "+idProducto);
						}
					}
					else if (tipoDocumento.equals("CA") || tipoDocumento.equals("FA") || tipoDocumento.equals("FC") || tipoDocumento.equals("FM") || tipoDocumento.equals("IJ")){
						valorSalida=pinventario;
						ponderar=false;
					}
				}
				/*
				 *  Si es una entrada entonces ..
				 */
				else if (entrada!=0){
					//System.out.println("Metiendo...");
					/*
					 * Si esta anulada ...
					 */
					if (!estado) {
						//System.out.println("Entrada.. documento anulado");
						entrada = 0;
						valorEntrada = 0;
					}
					/*
					 * Si no esta anulada y es una devolucion en compras entonces debe salir al valor al que se 
					 * compro, esto lo sabemos por el rfdocumento.
					 */
					else if (tipoDocumento.equals("DV")) {

						//System.out.println("Entrada.. devolucion en ventas");
						/*
						 * Verificamos si el valor de la venta esta registrado en el historial y tomamos como valor
						 * de la entrada el mismo valor de la venta, siempre y cuando este se cruce con el valor de la venta
						 */
						if (rfDocumento==null) {
							valorEntrada = pinventario;
						}
						else if (historyInv.containsKey(rfDocumento)) {
							valorEntrada = historyInv.get(rfDocumento).getValorSalida();
						}
						/*
						 * Si el documento referencia no se encuentra en el historico se debe buscar en la base de datos
						 */
						else {
							valorEntrada = getDBValue(conn,"SCS0079",orden,idProducto);
						}
					}
					else if (tipoDocumento.equals("IJ")) {// && pinventario!=0){
							valorEntrada=pinventario;
							ponderar=false;
					}
				}
				
				/*
				 * Recalculando informacion
				 */
				double nuevoValorEntrada = valorEntrada<0 && entrada==Math.abs(saldoAnt) && valorEntrada==pinventario?valorSaldoAnt*-1:entrada*valorEntrada;
				/*
				 * Si se va a sacar la totalidad del inventario, esta debe salir al valor del saldo,
				 * no a la multiplicacion del pinventario * numero de unidades, ya que esto presenta 
				 * un desface por la perdida de decimales y genera saldos con cantidad 0 y valor diferene
				 * de 0.
				 */
				
				double nuevoValorSalida = saldoAnt==salida && valorSalida==pinventario?valorSaldoAnt:salida*valorSalida;
				
				saldoAnt = saldoAnt + entrada - salida;
				valorSaldoAnt = saldoAnt==0?0:roundValue(valorSaldoAnt + nuevoValorEntrada - nuevoValorSalida);

				/*
				 * Si ponderar es verdadero entonces se pondera el valor del inventario teniendo en cuenta
				 * que las las cantidades del saldo sean diferentes de 0.
				 */
				//System.out.println("producto: "+idProducto+" estado: "+estado+" valor: "+pinventario);
				if (ponderar && estado) {

					/*
					 * Se verifica que el saldo o el valor del saldo no sean 0, si es asi entonces el 
					 * valor del precio del inventario debe ser igual al valor del movimiento a generar
					 * sea una entrada o una salida.
					 */
					
					if (saldoAnt!=0) {
						pinventario = roundValue(valorSaldoAnt/saldoAnt);
					}
					else {
						if (entrada!=0) {
							pinventario=valorEntrada;
						}
						else {
							pinventario=valorSalida;
						}
					}
					//System.out.println("producto ponderado : "+idProducto+" estado: "+estado+" valor: "+pinventario);

				}
				
				/* 
				 * Actualizando en la base de datos
				 */
				RQupdate.ejecutarSQL(conn,new String[] {
										String.valueOf(pinventario),
										String.valueOf(valorEntrada),
										String.valueOf(valorSalida),
										String.valueOf(saldoAnt),
										String.valueOf(valorSaldoAnt),
										orden});
				
				/*
				 * Si el documento registrado no es un documento anulado entonces se ingresa en el historial.
				 */
				if (estado) {
					historyInv.put(orden,new DataInventory(pinventario,entrada,valorEntrada,salida,valorSalida));
				}

			}
			
			LinkingCache.setPCosto(bd, idBodega, idProducto, pinventario);
			LinkingCache.setSaldoInventario(bd, idBodega, idProducto, saldoAnt);
			LinkingCache.setVSaldoInventario(bd, idBodega, idProducto, valorSaldoAnt);

			/*
			 * Cerrando estamentos
			 */
			RQupdate.closeStatement();
			RQdata.closeStatement();
			RSdata.close();
			if (!transaction) {
				ConnectionsPool.freeMultiConnection(bd, conn);
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
		}

	}

	private double getDBValue(Connection conn,String sql,String orden,String idProducto) throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		QueryRunner RQventrada = new QueryRunner(bd,sql,new String[] { orden,idProducto });
		ResultSet RSventrada = RQventrada.ejecutarMTSELECT(conn);
		double valor = 0;
		if (RSventrada.next()) {
			valor = RSventrada.getDouble(1);
		}
		RQventrada.closeStatement();
		RSventrada.close();
		return valor;
	}
	
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
	private void deleteDocument() throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0003",new String[]{});
		QueryRunner RQdropDocument = new QueryRunner(bd,"SCS0083",new String[] { CacheKeys.getKey("ndocumento") });
		QueryRunner RQdeleteDocument = new QueryRunner(bd,"SCD0002",new String[] { CacheKeys.getKey("ndocumento") });
		RQdpDocument.ejecutarSQL();
		RQdropDocument.ejecutarSQL(); 
		RQdeleteDocument.ejecutarSQL();
		RQdpDocument.closeStatement();
		RQdropDocument.closeStatement();
		RQdeleteDocument.closeStatement();
	}

	class DataInventory {
		double pinventario 	= 0;
		double entrada 		= 0;
		double valorEntrada = 0;
		double salida 		= 0;
		double valorSalida 	= 0;
		
		public DataInventory(double pinventario,double entrada,double valorEntrada,double salida,double valorSalida) {
			this.pinventario=pinventario;
			this.entrada=entrada;
			this.valorEntrada=valorEntrada;
			this.salida=salida;
			this.valorSalida=valorSalida;
		}
		
		public double getEntrada() {
			return entrada;
		}
		public void setEntrada(double entrada) {
			this.entrada = entrada;
		}
		public double getPinventario() {
			return pinventario;
		}
		public void setPinventario(double pinventario) {
			this.pinventario = pinventario;
		}
		public double getSalida() {
			return salida;
		}
		public void setSalida(double salida) {
			this.salida = salida;
		}
		public double getValorEntrada() {
			return valorEntrada;
		}
		public void setValorEntrada(double valorEntrada) {
			this.valorEntrada = valorEntrada;
		}
		public double getValorSalida() {
			return valorSalida;
		}
		public void setValorSalida(double valorSalida) {
			this.valorSalida = valorSalida;
		}
	}
	
	class RecoverData extends Thread {

		String _fecha;
		String _bodega;
		String _producto;
		HashMap<Integer,RecoverData> recoverList;
		Integer index;
		double _saldo;
		double _valorSaldo;
		boolean _transaction;
		
		RecoverData(HashMap<Integer,RecoverData> recoverList,Integer index,String fecha,String bodega,String producto,double saldo,double valorSaldo,boolean transaction) {
			this.recoverList=recoverList;
			this.index=index;
			this._fecha=fecha;
			this._bodega=bodega;
			this._producto=producto;
			this._saldo=saldo;
			this._valorSaldo=valorSaldo;
			this._transaction=transaction;
		}
		
		public void run() {
			recoverData(_fecha,_bodega,_producto,_saldo,_valorSaldo,_transaction);
			synchronized(recoverList) {
				recoverList.remove(index);
				recoverList.notify();
			}
		}
	}
	
	class Monitor extends Thread {
		int i;
		public void setI(int i) {
			this.i=i;
		}
		public void run() {
			int j = 0;
			while (true) {
				if (i!=j) {
					System.out.println("Hilo Generado "+i);
					j=i;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					break;
				}
			}
		}
	}

}