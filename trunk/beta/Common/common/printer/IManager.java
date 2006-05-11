package common.printer;
import java.io.ByteArrayInputStream;

import org.jdom.Document;

public abstract class IManager {
	
	protected Document document_template;
	protected Document document_transaction;
	protected int width;
	protected int height;
	protected ByteArrayInputStream in;
	
	public ByteArrayInputStream getStream(){
		return in;
	}
}
