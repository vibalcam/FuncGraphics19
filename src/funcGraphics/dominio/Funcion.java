package funcGraphics.dominio;

import javax.script.ScriptException;

import org.jfree.data.xy.XYSeries;

import funcGraphics.negocio.Grafica;

/**
 * Objeto que representa a una función de una variable
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
	 * Crea una función con una expresión y una variable dadas y una visibilidad inicial
	 * @param grafica gráfica que contiene la función
	 * @param expresion expresión de la función
	 * @param variable variable usada en la función
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
	 * Crea una función con expresión y variable vacías
	 * @param grafica gráfica que contiene la función
	 * @param visible visibilidad inicial
	 */
	public Funcion(Grafica grafica, boolean visible) {
		this(grafica, "", "", visible);
	}

	/**
	 * Crea una función con los parámetros predeterminados
	 * @param grafica gráfica que contiene la función
	 */
	public Funcion(Grafica grafica) {
		this(grafica, true);
	}

	/**
	 * Devuelve la expresión de la función
	 * @return expresión de la función
	 */
	public String getExpresion() {
		return expresion;
	}

	/**
	 * Devuelve el parseador que usa la función
	 * @return parseador de la función
	 */
	public Parser getParser() {
		return parser;
	}

	/**
	 * Devuelve la key asociada a la función
	 * @return key asociada a la función
	 */
	public FuncionKey getKey() {
		return key;
	}

	/**
	 * Devuelve la visibilidad de la función
	 * @return true si la función esta visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Devuelve los puntos de la función en el interválo de la gráfica
	 * @return XYSeries con los puntos
	 */
	public XYSeries getData() {
		return data;
	}

	/**
	 * Establece la expresión de la función
	 * @param expresion nueva expresión
	 */
	private void setExpresion(String expresion) {
		String expStrip = expresion.replaceAll("\\s+", "");
		this.expresion = expStrip.isEmpty() ? "0" : expStrip;
//		System.out.println(this.expresion);
	}

	/**
	 * Establece la nueva función representada y actualiza los puntos
	 * @param expresion expresión de la función
	 * @param variable variable de la función
	 * @throws ScriptException cuando se produce un error en el cálculo de los puntos
	 */
	public void setFuncion(String expresion, String variable) throws ScriptException {
		setExpresion(expresion);
		parser.setFuncion(this.expresion, variable.replaceAll("\\s+", ""));
		updateData();
	}

	/**
	 * Establece la visibilidad de la función
	 * @param visible nueva visibilidad
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Actualiza los puntos de la función
	 * @throws IllegalArgumentException si el límite izquierdo de la gráfica es mayor que el derecho
	 * @throws ScriptException cuando se produce un error en el cálculo de los puntos
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
				y = parser.eval(x); // si la función es errónea, el Exception se producirá aqui en la primera
									// iteración
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
	 * Compara esta función al objeto introducido
	 * @return true si el objeto es una función con la misma expresion ignorando mayúsculas
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Funcion)
			return ((Funcion) obj).getExpresion().equalsIgnoreCase(this.getExpresion());
		else
			return false;
	}

	/**
	 * Devuelve un hash code para esta función.
	 * El hashcode es calculado a partir de su expresión en minúscula usando el hashcode de String
	 */
	@Override
	public int hashCode() {
		return expresion.toLowerCase().hashCode();
	}

	/**
	 * Devuelve una representación de la función
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
		 * Si su expresión es la misma, se ordenaran por el hashcode de FuncionKey, por lo que
		 * no habrá dos keys iguales.
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
		 * Dicha representación es la misma que la de su Funcion asociada.
		 */
		@Override
		public String toString() {
			return funcion.toString();
		}
	}
}