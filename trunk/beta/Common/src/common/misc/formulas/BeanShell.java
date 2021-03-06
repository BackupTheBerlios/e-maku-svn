package common.misc.formulas;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShell {
	
	public static Interpreter shellScript;
	public BeanShell() {
		shellScript = new Interpreter();
	}
	
	public static synchronized boolean eval(String condition) {
		Boolean b;
		try {
			if (shellScript==null ) {
				shellScript = new Interpreter();
			}
			b = (Boolean)shellScript.eval(condition);
		} catch (EvalError e) {
			e.printStackTrace();
			return false;
		}
		return b.booleanValue();
	}
}
