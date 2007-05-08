package client.gui.components;

import common.misc.language.Language;

public class MaxRecordsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1554002383861573318L;

	public String getMessage() {
        return  Language.getWord("MAX_ERROR_EXCEPTION");
    }

}
