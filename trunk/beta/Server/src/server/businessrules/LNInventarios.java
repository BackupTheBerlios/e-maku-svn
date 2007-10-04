package server.businessrules;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

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
	 */

	public void movimientos(Element pack) throws SQLNotFoundException,
			SQLBadArgumentsException, SQLException {

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
			recover(pack);
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
					if (!(cuenta.startsWith("14") && !cuenta.substring(4, 6)
							.equals("99"))) {
						return;
					} else {
					}
				}
				if ("bodega".equals(nameField)) {
					record[2] = field.getValue();
				} else if ("idproducto".equals(nameField)) {
					record[3] = field.getValue();
				} else if ("cantidad".equals(nameField)) {
					System.out.println("Asignando cantidad... "
							+ field.getValue());
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
		System.out.println("tipo de movimiento " + tipoMovimiento);
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
				record[5] = String.valueOf(LinkingCache.getPCosto(bd,
						record[2], record[3]));
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
		String idDocumento = CacheKeys.getKey("ndocumento");
		QueryRunner RQdocumento = new QueryRunner(bd, "SCS0051",
				new String[] { idDocumento });
		ResultSet RSdatos = RQdocumento.ejecutarSELECT();
		/*
		 * La anterior consulta nos retorna las siguientes columnas 1.
		 * id_prod_serv 2. id_bodega 3. pinventario 4. entrada 5. valor_ent 6.
		 * salida 7. valor_sal
		 * 
		 * del anterior registro se analiza los campos entrada y salida; con
		 * ellos se sabe el tipo de movimiento a anular.
		 */

		while (RSdatos.next()) {
			Vector<String> record = new Vector<String>();
			String[] ponderado = null;
			QueryRunner RQanular = null;

			record.addElement(CacheKeys.getDate());
			record.addElement(idDocumento);
			record.addElement(RSdatos.getString(2));
			record.addElement(RSdatos.getString(1));
			/*
			 * Se captura los saldos antes del movimiento
			 */
			saldo = LinkingCache.getSaldoInventario(bd, RSdatos.getString(2),
					RSdatos.getString(1));
			vsaldo = LinkingCache.getVSaldoInventario(bd, RSdatos.getString(2),
					RSdatos.getString(1));

			/*
			 * Se almacena primer saldo de la transaccion
			 */

			LNUndoSaldos.setSaldoAntInv(bd, RSdatos.getString(2), RSdatos
					.getString(1), saldo);

			/*
			 * REGISTROS DE UNA ENTRADA
			 * 
			 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
			 * record[3] = id_prod_serv record[4] = entrada record[5] =
			 * valor_ent record[6] = pinventario record[7] = saldo record[8] =
			 * valor_saldo
			 * 
			 * REGISTRO DE UNA SALIDA
			 * 
			 * record[0] = fecha record[1] = ndocumento record[2] = id_bodega
			 * record[3] = id_prod_serv record[4] = salida record[5] = valor_sal
			 * record[6] = pinventario record[7] = saldo record[8] = valor_saldo
			 */

			/*
			 * Si la columna 4 retorna un valor diferente de 0 entonces el
			 * movimiento a anular es una entrada
			 */
			if (RSdatos.getDouble(4) != 0) {
				RQanular = new QueryRunner(bd, "SCI00O4");

				record.addElement(RSdatos.getString(4));
				record.addElement(RSdatos.getString(5));

				/*
				 * Se multiplica el 3 registro por -1 para indicar que el
				 * movimiento generado sea una salida.
				 */

				ponderado = ponderar(RSdatos.getString(2),
						RSdatos.getString(1), RSdatos.getDouble(4) * -1,
						RSdatos.getDouble(5));
			}
			/*
			 * si no, si la columna 6 retorna un valor diferente de 0 entonces
			 * el movimiento a anular es una salida
			 */
			else if (RSdatos.getDouble(6) != 0) {
				RQanular = new QueryRunner(bd, "SCI00O8");
				record.addElement(RSdatos.getString(6));
				record.addElement(RSdatos.getString(7));
				ponderado = ponderar(RSdatos.getString(2),
						RSdatos.getString(1), RSdatos.getDouble(6), RSdatos
								.getDouble(7));
			}
			/*
			 * si no, es por que el movimiento a anular es gasto o un descuento
			 */
			else {
				record.addElement(RSdatos.getString(4));
			}

			if (ponderado != null) {
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

	private String[] ponderar(String idBodega, String idProdServ,
			double cantidad, double valor) {

		saldo += cantidad;
		vsaldo += (cantidad * valor);
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
		QueryRunner RQdocument = new QueryRunner(bd,"SCS0082",new String[]{CacheKeys.getKey("ndocumento")});
		ResultSet RSdocument = RQdocument.ejecutarSELECT();
		System.out.println("Recalculando editados");
		//int i=0;
		while (RSdocument.next()) {
			//System.out.println("registro "+(i++));
			recoverData(RSdocument.getString(1),
						RSdocument.getString(2),
						RSdocument.getString(3));	
		}
		
		QueryRunner RQdropDocument = new QueryRunner(bd,"SCS0085",new String[] {CacheKeys.getKey("ndocumento")});
		ResultSet RSdropDocument = RQdropDocument.ejecutarSELECT();
		System.out.println("Recalculando los elimiandos");
		//i=0;
		while (RSdropDocument.next()) {
			//System.out.println("registro "+(i++));
			recoverData(RSdropDocument.getString(1),
						RSdropDocument.getString(2),
						RSdropDocument.getString(3));	
		}
		QueryRunner RQdpDocument = new QueryRunner(bd,"SCD0003",new String[]{});
		RQdpDocument.ejecutarSQL();


		RQdocument.closeStatement();
		RSdocument.close();
		RQdropDocument.closeStatement();
		RSdropDocument.close();
		RQdpDocument.closeStatement();
	}

	public void recover(Element pack) {
		Iterator i = pack.getChildren().iterator();
		
		String fecha = null;
		String idBodega = null;
		String idProducto = null;
		
		/*
		 * Recoleccion de argumentos
		 */

		while (i.hasNext()) {
			Element field = (Element) i.next();
			String nameField = field.getAttributeValue("name");
			nameField = nameField.toLowerCase();
			if ("fecha".equals(nameField)) {
				fecha = field.getText();
			} else if ("idbodega".equals(nameField)) {
				idBodega = field.getText();
			} else if ("idproducto".equals(nameField)) {
				idProducto = field.getText();
			}
		}
		
		recoverData(fecha,idBodega,idProducto);
	}
	
	private void recoverData(String fecha,String idBodega, String idProducto) {
		double saldoAnt = 0;
		double valorSaldoAnt =0;
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
		/*
		 * Consulta de saldo anterior a partir de la fecha inicial y los
		 * registros a actualizar a partir de la fecha inicial.
		 */
		try {
			if (fecha != null) {
				QueryRunner RQsaldo = new QueryRunner(bd, "SCS0076",new String[] { fecha,idBodega,idProducto });
				RQdata = new QueryRunner(bd, "SCS0077", new String[] { fecha,idBodega, idProducto });
				ResultSet RSsaldo = RQsaldo.ejecutarSELECT();
				if (RSsaldo.next()) {
					pinventario = RSsaldo.getDouble(1);
					saldoAnt = RSsaldo.getDouble(2);
					valorSaldoAnt = RSsaldo.getDouble(3);
				}
				/*System.out.println("-----------------------------------------------------------");
				System.out.println("Consultando saldos de: "+fecha+"-"+idBodega+"-"+idProducto);
				System.out.println("pinventario: "+pinventario);
				System.out.println("Saldo anterior: "+saldoAnt);
				System.out.println("Valor Sdo Ant: "+valorSaldoAnt);
				*/
				RQsaldo.closeStatement();
				RSsaldo.close();
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
			ResultSet RSdata = RQdata.ejecutarSELECT();
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
						//System.out.println("Salida.. documento anulado");
						/*
						 * Debemos verificar a que precio entro y a ese precio se debe registrar la salida,
						 * se verifica en el historico del inventario, en caso de que no este
						 * debemos consultarlo en la base de datos. La razon de porque no podria estar es
						 * porque se tomo un rango de fechas posterior a la elaboracion del documento que se
						 * esta anulando.
						 */
						if (historyInv.containsKey(orden)) {
							valorSalida = historyInv.get(orden).getValorEntrada();
						}
						else {
							double valor = getDBValue( "SCS0078",orden,idProducto);
							if (valor!=0) {
								valorSalida = valor;
							}
						}
					}
					/*
					 * Si no esta anulada y es una devolucion en compras entonces debe salir al valor al que se 
					 * compro, esto lo sabemos por el rfdocumento.
					 */
					else if (tipoDocumento.equals("DC")) {
						//System.out.println("Salida.. devolucion en compras");
						/*
						 * Verificamos si el valor de la compra esta registrado en el historial y tomamos como valor
						 * de la salida el mismo valor del ingreso
						 */
						if (historyInv.containsKey(rfDocumento)) {
							valorSalida = historyInv.get(rfDocumento).getValorEntrada();
						}
						/*
						 * si no esta en el historial, toca consultar el valor de salida en la base de datos.
						 */
						else {
							valorSalida = getDBValue( "SCS0078",rfDocumento,idProducto);
						}
					}
					else if (tipoDocumento.equals("FA") || tipoDocumento.equals("FC") || tipoDocumento.equals("FM")){
						//if (idProducto.equals("1474"))
							//System.out.println("valor salida:"+valorSalida+"valor Inventario: "+pinventario);
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
						/* 
						 * Igual que si fuera una salida.
						 */
						if (historyInv.containsKey(orden)) {
							valorEntrada = historyInv.get(orden).getValorSalida();
						}
						/*
						 * Se busca el saldo en la base de datos
						 */
						else {
							double valor = getDBValue( "SCS0079",orden,idProducto);
							if (valor!=0) {
								valorEntrada =  valor;
							}
							else {
								RSdata.getDouble(7);
							}
						}
					}
					/*
					 * Si no esta anulada y es una devolucion en compras entonces debe salir al valor al que se 
					 * compro, esto lo sabemos por el rfdocumento.
					 */
					else if (tipoDocumento.equals("DV")) {
						//System.out.println("Entrada.. devolucion en ventas");
						/*
						 * Verificamos si el valor de la venta esta registrado en el historial y tomamos como valor
						 * de la entrada el mismo valor de la venta
						 */
						if (historyInv.containsKey(rfDocumento)) {
							valorEntrada = historyInv.get(rfDocumento).getValorSalida();
						}
						/*
						 * Si el documento referencia no se encuentra en el historico se debe buscar en la base de datos
						 */
						else {
							valorEntrada = getDBValue( "SCS0079",orden,idProducto);
						}
					}
					else {
						//System.out.println("Entrada simple");
						valorEntrada = RSdata.getDouble(7);
					}
				}
				
				/*
				 * Recalculando informacion
				 */
				
				saldoAnt = saldoAnt + entrada - salida;
				valorSaldoAnt =roundValue(valorSaldoAnt + (entrada*valorEntrada) - (salida*valorSalida));

				/*
				 * Si ponderar es verdadero entonces se pondera el valor del inventario teniendo en cuenta
				 * que las las cantidades del saldo sean diferentes de 0.
				 */
				
				if (ponderar) {
					//System.out.println("Ponderando");

					/*
					 * Se verifica que el saldo o el valor del saldo no sean 0, si es asi entonces el 
					 * valor del precio del inventario debe ser igual al valor del movimiento a generar
					 * sea una entrada o una salida.
					 */
					
					if (saldoAnt!=0) {
						//System.out.println("saldo anterior diferente de 0, se pudo ponderar");
						pinventario = roundValue(valorSaldoAnt/saldoAnt);
					}
					else {
						//System.out.println("saldo anterior diferente de 0, no se pudo ponderar");
						if (entrada!=0) {
							pinventario=valorEntrada;
						}
						else {
							pinventario=valorSalida;
						}
					}
				}
				
				if (idProducto.equals("1474"))
					System.out.println("Actualizando pinventario: "+pinventario+" saldoAnt: "+saldoAnt+" valorSaldo: "+valorSaldoAnt);
				/*System.out.println("Entrada: "+entrada+" valor Entrada: "+valorEntrada+" salida: "+salida+" valor salida "+valorSalida);
				System.out.println("Actualizando pinventario: "+pinventario+" saldoAnt: "+saldoAnt+" valorSaldo: "+valorSaldoAnt);
				*/
				/* 
				 * Actualizando en la base de datos
				 */
				RQupdate.ejecutarSQL(new String[] {
										String.valueOf(pinventario),
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

	private double getDBValue(String sql,String orden,String idProducto) throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
		QueryRunner RQventrada = new QueryRunner(bd,sql,new String[] { orden,idProducto });
		ResultSet RSventrada = RQventrada.ejecutarSELECT();
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
}