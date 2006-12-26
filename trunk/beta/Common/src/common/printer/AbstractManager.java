package common.printer;
import java.io.ByteArrayInputStream;

import org.jdom.Element;

import common.printer.PrintManager.ImpresionType;

public interface AbstractManager {
	public ByteArrayInputStream getStream();
	public boolean isSusseful();
	public ImpresionType getImpresionType();
	public void process(Element template, Element packages);
}
