/**
 * Created on 29 de enero de 2004, 10:31
 */
package common.misc.text;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class NumberToLetterConversor {

	private static final String[ ][ ] numerals = {
			{" UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO","NUEVE"},
			{" DIECI", " VEINTI", " TREINTA", " CUARENTA", " CINCUENTA"," SESENTA", " SETENTA", " OCHENTA", " NOVENTA"},
			{"CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ","QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ","NOVECIENTOS "}};
    
	private static String notation = "";

	public synchronized static String letters(String numberValue, String _notation) {
        notation = _notation!=null? _notation : " PESOS M/L";
		StringTokenizer stk = new StringTokenizer(process(numberValue));
		String lettersValue = "";
		String tmp = "";
		while (true) {
			try {
				tmp = stk.nextToken().trim();
				lettersValue = lettersValue + tmp + " ";
			}
			catch (NoSuchElementException NSEEe) {
				break;
			}
		}
		return lettersValue;
        
	}

	private synchronized static String process(String numberValue) {
		try {
            int length = numberValue.length();
			if (length > 6 && numberValue.substring(length - 6, length).equals("000000")) { 
				notation = " DE" + notation;
			}
			tens(numberValue.substring(length - 2, length));
			if (numberValue.substring(length - 3, length - 2).equals("1") && 
				numberValue.substring(length - 2, length).equals("00")) {
				notation = "CIEN " + notation;
			}
			else { 
				units(numberValue.substring(length - 3,length - 2), 2, false);
			}
			if (length > 3) {
				try {
					if ((length < 6 || !(numberValue.substring(length - 6,length - 3).equals("000")))) {
						notation = " MIL "+ notation;
					}
					if (length == 4 && numberValue.substring(0, 1).equals("1")) { 
						return notation;
					}

					tens(numberValue.substring(length - 5,length - 3));
					if (length > 5) {
						if (numberValue.substring(length - 6,length - 5).equals("1") &&
							numberValue.substring(length - 5,length - 3).equals("00")) {
							notation = "CIEN "+ notation;
						}
						else {
							units(numberValue.substring(length - 6,length - 5),2,false);
						}
						if (length > 6) {
							try {
								if (length == 7 && numberValue.substring(0, 1).equals("1")) {
                                    notation = " MILLON " + notation;
                                }
								else {
                                    notation = " MILLONES " + notation;
                                }
								tens(numberValue.substring(length - 8,length - 6));
								if (length > 8) {
									if (numberValue.substring(length - 9,length - 8).equals("1") && 
                                        numberValue.substring(length - 8,length - 6).equals("00")) {
                                        notation = "CIEN " + notation;
                                    }
									else {
                                        units(numberValue.substring(length - 9,length - 8),2,false);
                                    }
									if (length > 9) {
										try {
											notation = " MIL " + notation;
											if (length == 10 && numberValue.substring(0, 1).equals("1")){
                                                return notation;
                                            }
											tens(numberValue.substring(length - 11,length - 9));
										}
										catch (StringIndexOutOfBoundsException SIOOBEe) {
											units(numberValue.substring(length - 10,length - 9),0,false);
										}
									}
								}
							}
							catch (StringIndexOutOfBoundsException SIOOBEe) {
								units(numberValue.substring(length - 7,length - 6),0,false);
							}
						}
					}
				}
				catch (StringIndexOutOfBoundsException SIOOBEe) {
					units(numberValue.substring(length - 4,length - 3), 0, false);
				}
			}
		}
		catch (StringIndexOutOfBoundsException SIOOBEe) {
			units(numberValue, 0, false);
		}
		return notation;
	}

	private static String units(String valor, int numeral, boolean includeAnd) {
		try {
            int index = Integer.parseInt(valor) - 1;
			if (includeAnd) {
                notation = " Y " + numerals[numeral][index] + notation;
            }
			else {
				notation = numerals[numeral][index] + notation;
			}
		}
		catch (ArrayIndexOutOfBoundsException AIOOBEe) {}
		return notation;
	}

	private static String tens(String numberValue) {
		if (numberValue.equals("10")) {
			notation = "DIEZ" + notation;
			return notation;
		}
		if (numberValue.equals("11")) {
			notation = "ONCE" + notation;
			return notation;
		}
		if (numberValue.equals("12")) {
			notation = "DOCE" + notation;
			return notation;
		}
		if (numberValue.equals("13")) {
			notation = "TRECE" + notation;
			return notation;
		}
		if (numberValue.equals("14")) {
			notation = "CATORCE" + notation;
			return notation;
		}
		if (numberValue.equals("15")) {
			notation = "QUINCE" + notation;
			return notation;
		}
		if (numberValue.equals("20")) {
			notation = "VEINTE" + notation;
			return notation;
		}

		if (!numberValue.substring(1, 2).equals("0")) {
            if (Integer.parseInt(numberValue.substring(0,1)) > 2) {
                units(numberValue.substring(1,2),0,true);
            }
    		else {
                units(numberValue.substring(1, 2), 0, false);
            }
        }
		units(numberValue.substring(0, 1), 1, false);
		return notation;
	}
}
