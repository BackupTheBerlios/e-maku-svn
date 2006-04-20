package client.gui.components;

public class Formula {
	
	public static final int SIMPLE = 0;
	public static final int BEANSHELL = 1;
	public static final int SUPER = 2;
	public static final int SIMPLENQ = 3;
	public static final int BEANSHELLNQ = 4;
	public static final int SUPERNQ = 5;
	public static final int SUPERBEANNQ = 6;
	private int type = SIMPLE;
	private String formula;
	
	public Formula(String formula, int type) {
		this.formula = formula;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public String getFormula() {
		return formula;
	}
	public String toString() {
		return formula;
	}
}
