package funcGraphics.dominio;

import javax.script.ScriptException;

import org.jfree.data.xy.XYSeries;

import funcGraphics.negocio.Grafica;

/**
 * Objeto que representa a una funci�n de una variable
 * @author vibal
 */
public class Funcion {
	private final static int NUM_PUNTOS = 1500;

	private FuncionKey key;
	private Parser parser;
	private String expresion;
	private Grafica grafica;
	private XYSeries data;
	private boolean visible;

	/**
	 * Crea una funci�n con una expresi�n y una variable dadas y una visibilidad inicial
	 * @param grafica gr�fica que contiene la funci�n
	 * @param expresion expresi�n de la funci�n
	 * @param variable variable usada en la funci�n
	 * @param visible visibilidad inicial
	 */
	public Funcion(Grafica grafica, String expresion, String variable, boolean visible) {
		this.grafica = grafica;
		key = new FuncionKey(this);
		setExpresion(expresion);
		parser = new Parser(getExpresion(), variable);
		data = new XYSeries(key, true, false);
		setVisible(visible);
	}

	/**
	 * Crea una funci�n con expresi�n y variable vac�as
	 * @param grafica gr�fica que contiene la funci�n
	 * @param visible visibilidad inicial
	 */
	public Funcion(Grafica grafica, boolean visible) {
		this(grafica, "", "", visible);
	}

	/**
	 * Crea una funci�n con los par�metros predeterminados
	 * @param grafica gr�fica que contiene la funci�n
	 */
	public Funcion(Grafica grafica) {
		this(grafica, true);
	}

	/**
	 * Devuelve la expresi�n de la funci�n
	 * @return expresi�n de la funci�n
	 */
	public String getExpresion() {
		return expresion;
	}

	/**
	 * Devuelve el parseador que usa la funci�n
	 * @return parseador de la funci�n
	 */
	public Parser getParser() {
		return parser;
	}

	/**
	 * Devuelve la key asociada a la funci�n
	 * @return key asociada a la funci�n
	 */
	public FuncionKey getKey() {
		return key;
	}

	/**
	 * Devuelve la visibilidad de la funci�n
	 * @return true si la funci�n esta visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Devuelve los puntos de la funci�n en el interv�lo de la gr�fica
	 * @return XYSeries con los puntos
	 */
	public XYSeries getData() {
		return data;
	}

	/**
	 * Establece la expresi�n de la funci�n
	 * @param expresion nueva expresi�n
	 */
	private void setExpresion(String expresion) {
		String expStrip = expresion.replaceAll("\\s+", "");
		this.expresion = expStrip.isEmpty() ? "0" : expStrip;
//		System.out.println(this.expresion);
	}

	/**
	 * Establece la nueva funci�n representada y actualiza los puntos
	 * @param expresion expresi�n de la funci�n
	 * @param variable variable de la funci�n
	 * @throws ScriptException cuando se produce un error en el c�lculo de los puntos
	 */
	public void setFuncion(String expresion, String variable) throws ScriptException {
		setExpresion(expresion);
		parser.setFuncion(this.expresion, variable.replaceAll("\\s+", ""));
		updateData();
	}

	/**
	 * Establece la visibilidad de la funci�n
	 * @param visible nueva visibilidad
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Actualiza los puntos de la funci�n
	 * @throws IllegalArgumentException si el l�mite izquierdo de la gr�fica es mayor que el derecho
	 * @throws ScriptException cuando se produce un error en el c�lculo de los puntos
	 */
	public void updateData() throws IllegalArgumentException, ScriptException {
		double leftLimit = grafica.getLeftLimit();
		double rightLimit = grafica.getRightLimit();
		boolean allNan = true;
		data.clear();
		if (leftLimit > rightLimit)
			throw new IllegalArgumentException("Limite izquierdo no puede ser superior al derecho");
		else if (leftLimit < rightLimit) {
			double separacion = (rightLimit - leftLimit) / (NUM_PUNTOS - 1);

			double x, y;
			for (int k = 0; k < NUM_PUNTOS; k++) {
				x = leftLimit + k * separacion;
				y = parser.eval(x); // si la funci�n es err�nea, el Exception se producir� aqui en la primera
									// iteraci�n
				if (!Double.isNaN(y) && !Double.isInfinite(y)) {
					data.add(x, y);
					allNan = false;
				}
			}

			if (allNan)
				throw new ScriptException("Variable no definida en dicho intervalo");
		}
	}

	/**
	 * Compara esta funci�n al objeto introducido
	 * @return true si el objeto es una funci�n con la misma expresion ignorando may�sculas
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Funcion)
			return ((Funcion) obj).getExpresion().equalsIgnoreCase(this.getExpresion());
		else
			return false;
	}

	/**
	 * Devuelve un hash code para esta funci�n.
	 * El hashcode es calculado a partir de su expresi�n en min�scula usando el hashcode de String
	 */
	@Override
	public int hashCode() {
		return expresion.toLowerCase().hashCode();
	}

	/**
	 * Devuelve una representaci�n de la funci�n
	 */
	@Override
	public String toString() {
		return getExpresion();
	}

	/**
	 * Key asociada a una Funcion
	 * @author vibal
	 */
	public class FuncionKey implements Comparable<FuncionKey> {
		private Funcion funcion;

		/**
		 * Crea una FuncionKey a partir de su Funcion asociada
		 * @param funcion Funcion asociada
		 */
		private FuncionKey(Funcion funcion) {
			this.funcion = funcion;
		}

		/**
		 * Devuelve la Funcion asociada a este FuncionKey
		 * @return Funcion asociada
		 */
		public Funcion getFuncion() {
			return funcion;
		}

		/**
		 * Compara dos FuncionKey usando el compareTo de String con las expresiones de sus Funcion asociadas.
		 * Si su expresi�n es la misma, se ordenaran por el hashcode de FuncionKey, por lo que
		 * no habr� dos keys iguales.
		 */
		@Override
		public int compareTo(FuncionKey funcionKey) {
			int res = funcionKey.getFuncion().getExpresion().compareTo(Funcion.this.expresion);
			if (res != 0)
				return res;
			else
				return Double.compare(this.hashCode(), funcionKey.hashCode());
			// si su expresion es la misma, se ordenaran por su hashcode, por lo que no
			// habra dos funciones iguales segun el compareTo
			// esto es necesario para usarlas como key de los XYSeries sin que dos funciones
			// con la misma expresion se interfieran
		}

		/**
		 * Devuelve una String representando la FuncionKey.
		 * Dicha representaci�n es la misma que la de su Funcion asociada.
		 */
		@Override
		public String toString() {
			return funcion.toString();
		}
	}
}