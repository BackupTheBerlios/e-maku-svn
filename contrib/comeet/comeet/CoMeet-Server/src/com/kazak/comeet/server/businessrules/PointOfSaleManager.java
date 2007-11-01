package com.kazak.comeet.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import com.kazak.comeet.server.comunications.SocketWriter;
import com.kazak.comeet.server.database.sql.QueryRunner;
import com.kazak.comeet.server.database.sql.SQLBadArgumentsException;
import com.kazak.comeet.server.database.sql.SQLNotFoundException;
import com.kazak.comeet.server.misc.LogWriter;

public class PointOfSaleManager {

	private Iterator argsIterator;
	private ArrayList<QueryRunner> queries;
	
	public PointOfSaleManager(SocketChannel sock, Element args, Element packet, String id) {
		this.argsIterator = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean result = false;
		String message = "";
		queries = new ArrayList<QueryRunner>();
		try {
			if ("add".equals(type) || "edit".equals(type) || "remove".equals(type) ) {
				result = processPointOfSale(packet);
			} else {
				message="EL paquete xml esta incompleto.";
			}
		} catch (SQLNotFoundException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Punto de Venta [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
		} catch (SQLBadArgumentsException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Punto de Venta [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
		} catch (SQLException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Punto de Venta [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
		}
		if (result) {
			for (QueryRunner queryRunner :queries) {
				queryRunner.commit();
			}
			Element reload = new Element("RELOADTREE");
			try {
				SocketWriter.write(sock,new Document(reload));
			} catch (IOException e) {
				e.printStackTrace();
			}
			message = "Los datos fueron almacenados satisfactoriamente";
			TransactionRunner.notifyMessageReception(sock,id,message,"Transaccion sobre POS");
		}
		else {
			for (QueryRunner qRunner :queries) {
				qRunner.rollback();
			}
			if ("remove".equals(type)) {
				message =
					"El grupo debe estar vacio\n" +
					"para poder ser eliminado\n";
			}
			TransactionRunner.notifyErrorMessage(sock,id,
							"ERROR: No se pudo procesar la transaccion.\nCausa:\n" + message + " ");
		}
	}
	
	private boolean processPointOfSale(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {		
		Iterator iterator = transaction.getChildren("package").iterator();
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			String sqlCode = ((Element)argsIterator.next()).getText();
			QueryRunner queryRunner = new QueryRunner(sqlCode,packArgs(element));
			queries.add(queryRunner);
			queryRunner.setAutoCommit(false);
			queryRunner.executeSQL();
		}
		return true;
	}
	
	
	public String[] packArgs(Element pack) {
		List list = pack.getChildren("field");
		Iterator listIterator = list.iterator();
		String[] argsArray = new String[list.size()];
		int index = 0;
		while(listIterator.hasNext()) {
			Element element = (Element) listIterator.next();
			argsArray[index] = element.getValue();
			index++;
		}
		return argsArray;
	}
}