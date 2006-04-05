package server.comunicaciones;

import org.jdom.Document;
import org.jdom.Element;

public class AcpFailure extends Document {

	private static final long serialVersionUID = -2227873601621081462L;
	
	public AcpFailure(String mess) {
		this.setRootElement(new Element("ACPFAILURE"));
		
		Element	message 	= new Element("message");
		message.setText(mess);
		
		this.getRootElement().addContent(message);
	}

}
