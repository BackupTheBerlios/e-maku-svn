package common.misc;

import java.io.*;

import org.jdom.*;
import org.jdom.output.*;

public class XMLUtils {
	
	public static void debugDocument(Document d) {
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		try {
			out.output(d,System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
