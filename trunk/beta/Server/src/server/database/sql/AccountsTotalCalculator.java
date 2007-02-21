package server.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.jdom.Element;

public class AccountsTotalCalculator {
	
	private static Statement st;
	private static String date;
	private static String account="";
	
	
	public AccountsTotalCalculator(String nombreDB,Element pack) {
		
	}
	
	public static synchronized void totalizarCuentas(String nombreDB,Element pack) {
		Iterator i = pack.getChildren().iterator();
		while (i.hasNext()) {
			Element e = (Element)i.next();
			if (e.getName().equals("date")) {
				date = e.getValue();
			}
			else if (e.getName().equals("account")) {
				account = e.getValue();
			}
		}
		if (!"".equals(date)) {
			try {
				/*
				 * Se limpia la informacion existente en la tabla temporal
				 */
				st.execute(SQLFormatAgent.getSentencia(nombreDB,"DEL0047"));
				
				/*
				 * Se obtiene el id de la cuenta padre, por defecto es 0 y calculara todo el puc
				 */
				int accountParent = -1;
				if (!"".equals(account)) {
					ResultSet rs = st.executeQuery(SQLFormatAgent.getSentencia(nombreDB,"SCS0047",new String[]{account}));
					while (rs.next()) {
						accountParent = rs.getInt(1);
					}
				}
				else {
					accountParent = 0;
				}
				
				if (accountParent>=0) {
					/*
					 * PENDIENTE EL MANEJO DE CENTRO COSTOS
					 */
					
					/*
					 * Se procede a obtener los saldos de las cuentas auxiliares 
					 * e insertar en la tabla temporal
					 */
					
					st.execute(SQLFormatAgent.getSentencia(nombreDB,"SCI00O9",new String[]{account}));
					
					/*
					 * Se Procede a obtener los saldos de las cuentas auxiliares de 
					 * inventarios e insertar en la tabla temporal
					 */

					st.execute(SQLFormatAgent.getSentencia(nombreDB,"SCI0010",new String[]{account}));

					/*
					 * Se procede a obtener los saldos de las cuentas auxiliares de 
					 * terceros e insertar en la tabla temporal
					 */

					st.execute(SQLFormatAgent.getSentencia(nombreDB,"SCI0011",new String[]{account}));
					
					/*
					 * Ahora se genera una consulta encargada de asignar los totales de las cuentas de mayor
					 * nivel a menor nivel
					 */
					for (int j=10;j>account.length();j-=2) {
						st.execute(SQLFormatAgent.getSentencia(nombreDB,"SCI0012",new String[]{String.valueOf(j)}));
					}
				}
				
				StatementsClosingHandler.close(st);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	
}
