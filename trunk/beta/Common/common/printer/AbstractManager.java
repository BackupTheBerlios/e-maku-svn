package common.printer;
import java.io.ByteArrayInputStream;

public abstract class AbstractManager {
	
	protected int width;
	protected int height;
	protected ByteArrayInputStream in;
	protected boolean sussceful;
	
	public ByteArrayInputStream getStream(){
		return in;
	}

	public boolean isSusseful() {
		return sussceful;
	}
}
