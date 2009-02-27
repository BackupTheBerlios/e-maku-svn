package common.printer;
import java.io.ByteArrayInputStream;

import org.jdom.Element;

import common.printer.PrintingManager.ImpresionType;

public interface AbstractManager {
	public ByteArrayInputStream getStream();
	public boolean isSuccessful();
	public ImpresionType getImpresionType();
	public void processPDF(Element template, Element packages);
	public void processPostScript(Element template, Element packages);
}
