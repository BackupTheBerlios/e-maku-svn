package com.kazak.smi.server.database.sql;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;

public class CacheLoader {

	private static Hashtable <String,String>HInstructions;
   
   public CacheLoader() {
	   HInstructions = new Hashtable<String,String>();
	   LogWriter.write(
			   Language.getWord("LOADING_CACHE") + " "+
			   ConfigFile.getMainDataBase());
	   URL url = this.getClass().getResource("/sqlSentences.xml");
	   SAXBuilder sax = new SAXBuilder(false);
	   try {
		   Document doc = sax.build(url);
		   Element root = doc.getRootElement();
		   Iterator sentences = root.getChildren().iterator();
		   while (sentences.hasNext()) {
			   Element sentence = (Element)sentences.next();
			   String code = sentence.getChildText("code").trim();
			   String sql = sentence.getChildText("sql").trim();
			   HInstructions.put("K-"+code,sql);
		   }
	   } catch (JDOMException e) {
		   e.printStackTrace();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }

   }

    /**
     * Este metodo retorna una sentencia SQL
     * @param key Codigo de la sentencia SQL
     * @return retorna la sentencia SQL
     */
    public static String getSQLSentence(String key) {
        return HInstructions.get(key);
    }    
}