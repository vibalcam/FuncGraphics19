package funcGraphics.dominio;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Parser {
//	private static final Pattern PATTERN = Pattern.compile("\\p{Lower}+");
	private static final Pattern PATTERN = Pattern.compile("\\p{Alpha}+");
	private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");
	
	private Bindings bindings;
	private String parsed;
	private String variable;

	public Parser(String expresion,String variable) {
		bindings = ENGINE.createBindings();
		setFuncion(expresion,variable);
	}

//	public Parser(String expresion) {
//		this(expresion,"");
//	}

	public String getParsed() {
		return parsed;
	}
	
	public void setFuncion(String expresion,String variable) {
//		this.variable = variable.trim().isEmpty() ? "x" : variable;
//		expresion = expresion.trim().isEmpty() ? "0" : expresion.trim();
		String expStrip = expresion.strip();
		expresion = expStrip.isEmpty() ? "0" : expStrip;
		if(this.variable!=null)
			bindings.remove(this.variable);
		this.variable = variable;
		parse(expresion);
	}

	public String getVariable() {
		return variable;
	}

	public double eval(double value) throws ScriptException {
//		if(!variable.isEmpty())
//			engine.put(variable,value);
//		Object res = engine.eval(parsed);
		if(!variable.isBlank())
			bindings.put(variable,value);
		Object res = ENGINE.eval(parsed,bindings);
		if(res==null)
			throw new ScriptException("No es posible evaluar la expresión");
		else if(res instanceof Number)
			return ((Number) res).doubleValue();
		else
			throw new ScriptException("Funcion no definida correctamente");
	}

	private void parse(String expresion) {
//		StringBuilder exp = new StringBuilder(expresion.toLowerCase());
		StringBuilder exp = new StringBuilder(expresion);

		char c;
		int pos = exp.indexOf("^");
		int k;
		int count;
		while(pos!=-1) {
			exp.setCharAt(pos,',');

			k = pos-1;
//			c = exp.charAt(k);
			// System.out.println("k: " + k + "\tChar: " + c);
			count = 0;
//			while(!(isOperador(c) && count<=0) && !(c=='(' && count<=0)) {
//				if(c=='(')
//					count--;
//				else if(c==')')
//					count++;
//
//				// System.out.println("Count: " + count);
//				// System.out.println(!(isOperador(c) && count<=0) && count>=0 && k>0);
//
//				k--;
//				if(k>=0)
//					c = exp.charAt(k);	// comprobar que no se ha excedido el rango antes de obtener el charAt
//				else
//					break;
//				// System.out.println("k: " + k + "\tChar: " + c);
//			}
			while(k>=0 && !(isOperador(c = exp.charAt(k)) && count<=0) && !(c=='(' && count<=0)) {
				if(c=='(')
					count--;
				else if(c==')')
					count++;

				// System.out.println("Count: " + count);
				// System.out.println(!(isOperador(c) && count<=0) && count>=0 && k>0);

				k--;
//				if(k>=0)
//					c = exp.charAt(k);	// comprobar que no se ha excedido el rango antes de obtener el charAt
//				else
//					break;
				// System.out.println("k: " + k + "\tChar: " + c);
			}
			// k = k==0 ? -1 : k;
			exp.insert(k+1,"pow(");
			pos += 4;

			k = pos+1;
//			c = exp.charAt(k);
			count = 0;
			// while(!(isOperador(c) && count<=0) && count>=0 && k<exp.length()) {
//			while(!(isOperador(c) && count<=0) && !(c==')' && count<=0)) {
//				// System.out.println("k: " + k + "\tChar: " + c + "\tCount: " + count);
//				if(c==')')
//					count--;
//				else if(c=='(')
//					count++;
//
//				k++;
//				if(k<exp.length())
//					c = exp.charAt(k);	// comprobar que no se ha excedido el rango antes de obtener el charAt
//				else
//					break;
//			}
			while(k<exp.length() && !(isOperador(c = exp.charAt(k)) && count<=0) && !(c==')' && count<=0)) {
				// System.out.println("k: " + k + "\tChar: " + c + "\tCount: " + count);
				if(c==')')
					count--;
				else if(c=='(')
					count++;

				k++;
//				if(k<exp.length())
//					c = exp.charAt(k);	// comprobar que no se ha excedido el rango antes de obtener el charAt
//				else
//					break;
			}
			// System.out.println("k: " + k);
			exp.insert(k,")");
			pos += 1;

			// System.out.println(exp.toString());
			pos = exp.indexOf("^",pos+1);
		}

		Matcher m = PATTERN.matcher(exp.toString());
		String str;
		// System.out.println("\nMath: " + exp.toString());

		count = 0;
		while(m.find()) {
			// System.out.println(m.start() + "\t" + m.end());
			str = exp.substring(m.start()+count,m.end()+count);
			if(!str.equals(variable)) {
				exp.insert(m.start()+count,"Math.");
				count += 5;
			}
			// System.out.println(exp.toString());
		}

		System.out.println(exp.toString());
		this.parsed = exp.toString();
	}
	
	private boolean isOperador(char c) {
		return (c=='+' || c=='-' || c=='*' || c=='/' || c=='^');
	}
}