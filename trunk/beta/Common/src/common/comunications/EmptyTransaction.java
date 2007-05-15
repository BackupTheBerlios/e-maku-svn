package common.comunications;

import org.jdom.Document;
import org.jdom.Element;

public class EmptyTransaction extends Document {
	private static final long serialVersionUID = 1L;
	private int cols = 1;
	private int rows = 1;
	
	public EmptyTransaction() {
		generate();
	}
	
	public EmptyTransaction(int rows,int cols) {
		this.cols = cols;
		this.rows = rows;
		generate();
	}
	
	private void generate() {
		Element rootNode = new Element("ANSWER");
		for (int j = 0 ; j  < rows ; j++) {
			Element row = new Element("row");
			for (int i = 0 ; i < cols ; i++) {
				Element col = new Element("col");
				row.addContent(col);	
			}
			rootNode.addContent(row);
		}
		setRootElement(rootNode);
	}
}