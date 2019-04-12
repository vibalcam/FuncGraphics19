package funcGraphics.dominio;
import java.io.Serializable;

import javax.script.ScriptException;

import org.jfree.data.xy.XYSeries;

import funcGraphics.negocio.Grafica;

//public class Funcion implements Comparable<Funcion> {
public class Funcion {
//	final static double SEPARACION_PUNTOS = 0.01;
	private final static int NUM_PUNTOS = 1500;
	
	private FuncionKey key;
	private Parser parser;
	private String expresion;
	private Grafica grafica;
	private XYSeries data;
	private boolean visible;

	public Funcion(Grafica grafica, String expresion, String variable, boolean visible) {
		this.grafica = grafica;
		key = new FuncionKey(this);
		setExpresion(expresion);
		parser = new Parser(getExpresion(),variable);
		data = new XYSeries(key,true,false);
		setVisible(visible);
//		try {
//			updateData();
//		} catch (ScriptException e) {
//			e.printStackTrace();
//		}
	}
	
	public Funcion(Grafica grafica, boolean visible) {
		this(grafica,"","",visible);
	}
	
	public Funcion(Grafica grafica) {
		this(grafica, true);
	}
	
	public String getExpresion() {
		return expresion;
	}
	
	public Parser getParser() {
		return parser;
	}
	
	public FuncionKey getKey() {
		return key;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public XYSeries getData() {
		return data;
	}
	
	private void setExpresion(String expresion) {
//		String expStrip = expresion.strip();
		String expStrip = expresion.replaceAll("\\s+", "");
//		this.expresion = expStrip.isEmpty() ? "0" : expStrip.toLowerCase();
		this.expresion = expStrip.isEmpty() ? "0" : expStrip;
		System.out.println(this.expresion);
	}
	
	public void setFuncion(String expresion,String variable) throws ScriptException {
		setExpresion(expresion);
		parser.setFuncion(this.expresion, variable.replaceAll("\\s+", ""));
		updateData();
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void updateData() throws IllegalArgumentException, ScriptException {
		double leftLimit = grafica.getLeftLimit();
		double rightLimit = grafica.getRightLimit();
		boolean allNan = true;
		data.clear();
		if(leftLimit>rightLimit)
			throw new IllegalArgumentException("Limite izquierdo no puede ser superior al derecho");
		else if(leftLimit<rightLimit) {
			double separacion = (rightLimit-leftLimit)/(NUM_PUNTOS-1);
			
//			XYSeries newData = new XYSeries(this,true,false);
			double x,y;
			for(int k=0; k<NUM_PUNTOS; k++) {
				x = leftLimit+k*separacion;
				y = parser.eval(x);	// si la función es errónea, el Exception se producirá aqui en la primera iteración
				if(!Double.isNaN(y) && !Double.isInfinite(y)) {
					data.add(x,y);
					allNan = false;
				}
//				System.out.println(x + "," + y);
			}
				
//			data = newData;
			if(allNan)
				throw new ScriptException("Variable no definida");
		}
	}

//	/**
//	 * Compara dos funciones mediante la comparación lexicográfica de sus expresiones
//	 * En caso de ser iguales, las compara segun su hashcode, por lo que nunca habrá dos funciones iguales
//	 * 
//	 * @param funcion funcion con la que se compara
//	 * @return devuelve segun String.compareTo, excepto si este es 0, que devuelve segun Double.compare() con los hashCodes
//	 */
//	@Override
//	public int compareTo(Funcion funcion) {
////		System.out.println(this.hashCode());
////		int res = funcion.getExpresion().compareTo(expresion);
////		if(res!=0)
////			return res;
////		else
////			return Double.compare(this.hashCode(), funcion.hashCode());
////			// si su expresion es la misma, se ordenaran por su hashcode, por lo que no habra dos funciones iguales segun el compareTo
////			// esto es necesario para usarlas como key de los XYSeries sin que dos funciones con la misma expresion se interfieran
//		return funcion.getExpresion().compareToIgnoreCase(this.getExpresion());
//	}
//	
//	public boolean equalsExpresion(Funcion funcion) {
////		return funcion.getExpresion().equalsIgnoreCase(this.getExpresion());
//		return this.compareTo(funcion)==0;
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		// Se conserva el equals por default para poder usarlo como key de los XYSeries
//		// sin que dos funciones con la misma expresion se interfieran
//		return super.equals(obj);
//	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Funcion)
			return ((Funcion) obj).getExpresion().equalsIgnoreCase(this.getExpresion());
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return expresion.toLowerCase().hashCode();
	}
	
	@Override
	public String toString() {
		return getExpresion();
	}
	
	public class FuncionKey implements Comparable<FuncionKey> {
		private Funcion funcion;
		
		private FuncionKey(Funcion funcion) {
			this.funcion = funcion;
		}
		
		public Funcion getFuncion() {
			return funcion;
		}

		@Override
		public int compareTo(FuncionKey funcionKey) {
			int res = funcionKey.getFuncion().getExpresion().compareTo(Funcion.this.expresion);
			if(res!=0)
				return res;
			else
				return Double.compare(this.hashCode(), funcionKey.hashCode());
				// si su expresion es la misma, se ordenaran por su hashcode, por lo que no habra dos funciones iguales segun el compareTo
				// esto es necesario para usarlas como key de los XYSeries sin que dos funciones con la misma expresion se interfieran
		}
		
		@Override
		public String toString() {
			return funcion.toString();
		}
	}
}