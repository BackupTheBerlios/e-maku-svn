package common.gui.components;

public class MalformedProfileException extends Exception {

	private static final long serialVersionUID = -4771871293842164050L;

	public MalformedProfileException() {
		super();
	}

	public MalformedProfileException(String message) {
		super(message);
	}

	public MalformedProfileException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedProfileException(Throwable cause) {
		super(cause);
	}

}
