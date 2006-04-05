package server.logicanegocios;

import common.miscelanea.idiom.Language;

public class AnnulDocumentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -40599760339722809L;

	private String tipoDocumento;
	private String numero;

    public AnnulDocumentException(String tipoDocumento,String numero) {
        this.tipoDocumento=tipoDocumento;
        this.numero=numero;
    }

    public String getMessage() {
        return Language.getWord("ERR_ANNUL_DOC0") + 
        		this.tipoDocumento+" "+
        		this.numero+
        		Language.getWord("ERR_ANNUL_DOC1");
    }

}
