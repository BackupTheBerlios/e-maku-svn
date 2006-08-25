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
			b = (Boolean)shellScript.eval(condition);
		} catch (EvalError e) {
			return false;
		}
		return b.booleanValue();
	}
}
