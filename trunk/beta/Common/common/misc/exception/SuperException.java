package common.misc.exception;

public class SuperException extends Exception {
	
	private static final long serialVersionUID = 2685008709731534977L;

	public static final int PANIC = 1;
	
	public SuperException(int type) {
		
		switch(type) {
		case PANIC:
		     System.exit(1);
		}
	}
	
}
