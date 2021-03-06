package server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

import server.comunications.EmakuServerSocket;
import server.database.sql.LinkingCache;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

public class LNAsientosPredefinidos {

	public LNAsientosPredefinidos(SocketChannel sock, 
								  Document doc, 
								  Element sn_pack,
								  String id_transaction) 
	throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		
		Element elm = doc.getRootElement().getChild("arg");
		String action = elm.getAttributeValue("attribute");
		String code="";
		Iterator source = sn_pack.getChildren().iterator();
		while (source.hasNext()) {
			Element pack = (Element)source.next();
			Iterator fields = pack.getChildren().iterator();
			boolean exit = false;
			while (fields.hasNext()) {
				Element field = (Element)fields.next();
				if ("key".equals(field.getAttributeValue("attribute"))) {
					code=field.getText();
					exit=true;
					break;
				}
			}
			if (exit) {
				break;
			}
		}
		if (action.equals("new")) {
			new LNMultiPackage(sock,
					   new Document((Element)doc.getRootElement().getChild("subarg").clone()),
					   (Element)sn_pack.clone(),
					   id_transaction);
			LinkingCache.reloadCtasAsientos(EmakuServerSocket.getBd(sock),new String[]{code});
		}
		else if (action.equals("edit")) {
			LinkingCache.removeCtasAsientos(EmakuServerSocket.getBd(sock),new String[]{code});
			new LNMultiPackage(sock,
					   new Document((Element)doc.getRootElement().getChild("subarg").clone()),
					   (Element)sn_pack.clone(),
					   id_transaction);
			LinkingCache.reloadCtasAsientos(EmakuServerSocket.getBd(sock),new String[]{code});
		}
		else if (action.equals("delete")) {
			LinkingCache.removeCtasAsientos(EmakuServerSocket.getBd(sock),new String[]{code});
			new LNGenericSQL(sock,
					   new Document((Element)doc.getRootElement().getChild("subarg").clone()),
					   (Element)sn_pack.clone(),
					   id_transaction);
		}
		
	}
}
