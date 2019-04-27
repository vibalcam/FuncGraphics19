package funcGraphics.dominio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Parser {
	private static final Pattern PATTERN = Pattern.compile("\\p{Alpha}+");
	private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

	private Bindings bindings;
	private String parsed;
	private String variable;

	public Parser(String expresion, String variable) {
		bindings = ENGINE.createBindings();
		setFuncion(expresion, variable);
	}

	public Parser(String expresion) {
		this(expresion, "");
	}

	public String getParsed() {
		return parsed;
	}

	public void setFuncion(String expresion, String variable) {
		String expStrip = expresion.strip();
		expresion = expStrip.isEmpty() ? "0" : expStrip;
		if(this.variable != null)
			bindings.remove(this.variable);
		this.variable = variable;
		parse(expresion);
	}

	public String getVariable() {
		return variable;
	}

	public double eval(double value) throws ScriptException {
		if (variable!=null && !variable.isBlank())
			bindings.put(variable, value);
		Object res = ENGINE.eval(parsed, bindings);
		if (res == null)
			throw new ScriptException("No es posible evaluar la expresión");
		else if (res instanceof Number)
			return ((Number) res).doubleValue();
		else
			throw new ScriptException("Funcion no definida correctamente");
	}

	private void parse(String expresion) {
		StringBuilder exp = new StringBuilder(expresion);

		char c;
//		int pos = exp.indexOf("^");
		int pos = -1;
		int k;
		int count;
//		while(pos!=-1) {
		while ((pos = exp.indexOf("^", pos + 1)) != -1) {
			exp.setCharAt(pos, ',');

			k = pos - 1;
			count = 0;
			while (k >= 0 && !(isOperador(c = exp.charAt(k)) && count <= 0) && !(c == '(' && count <= 0)) {
				if (c == '(')
					count--;
				else if (c == ')')
					count++;
				k--;
			}
			exp.insert(k + 1, "pow(");
			pos += 4;

			k = pos + 1;
			count = 0;
			while (k < exp.length() && !(isOperador(c = exp.charAt(k)) && count <= 0) && !(c == ')' && count <= 0)) {
				if (c == ')')
					count--;
				else if (c == '(')
					count++;

				k++;
			}
			exp.insert(k, ")");
			pos += 1;

//			pos = exp.indexOf("^",pos+1);
		}

		Matcher m = PATTERN.matcher(exp.toString());
		String str;

		count = 0;
		while (m.find()) {
			str = exp.substring(m.start() + count, m.end() + count);
			if (!str.equals(variable)) {
				exp.insert(m.start() + count, "Math.");
				count += 5;
			}
		}

//		System.out.println(exp.toString());
		this.parsed = exp.toString();
	}

	private boolean isOperador(char c) {
		return (c == '+' || c == '-' || c == '*' || c == '/' || c == '^');
	}
}