package server.businessrules;

import common.misc.language.Language;

public class InvalidDateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 535171761204661149L;

	
	public String getMessage() {
		return Language.getWord("ERR_INVALID_DATE");
	}
	
}
