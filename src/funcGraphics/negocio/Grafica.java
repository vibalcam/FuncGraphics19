package funcGraphics.negocio;

import javax.script.ScriptException;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import funcGraphics.dominio.Funcion;
import funcGraphics.dominio.Funcion.FuncionKey;

public class Grafica {
	public static final double DEFAULT_LIMIT = 5;

	private XYSeriesCollection dataCollection;
	private double leftLimit;
	private double rightLimit;
	private boolean guardado = false;

	/**
	 * Construye una gráfica con los límites predeterminados
	 */
	public Grafica() {
		this(-1 * DEFAULT_LIMIT, DEFAULT_LIMIT);
	}

	/**
	 * Constructor a partir de los límites de la gráfica
	 * 
	 * @param leftLimit  límite izquierdo
	 * @param rightLimit límite derecho
	 */
	public Grafica(double leftLimit, double rightLimit) {
		dataCollection = new XYSeriesCollection();
		this.setLimites(leftLimit, rightLimit);
//		this.leftLimit = leftLimit;
//		this.rightLimit = rightLimit;
	}

	/**
	 * Devuelve el límite izquierdo
	 * 
	 * @return límite izquierdo
	 */
	public double getRightLimit() {
		return rightLimit;
	}

	/**
	 * Devuelve el límite derecho
	 * 
	 * @return límite derecho
	 */
	public double getLeftLimit() {
		return leftLimit;
	}

	/**
	 * Devuelve el estado de guardado de la gráfica
	 * 
	 * @return true si la gráfica está guardada y actualizada, false en caso
	 *         contrario
	 */
	public boolean isGuardado() {
		return guardado;
	}

	/**
	 * Devuelve los XYSeriesCollection que tienen los datos de la gráfica
	 * 
	 * @return XYSeriesCollection con los datos
	 */
	public XYSeriesCollection getDataCollection() {
		return dataCollection;
	}

	/**
	 * Establece el estado de guardado de la gráfica
	 * 
	 * @param guardado nuevo estado de guardado
	 */
	public void setGuardado(boolean guardado) {
		this.guardado = guardado;
	}

	/**
	 * Cambia el límite derecho y actualiza los datos de la gráfica. En el caso de
	 * ser el nuevo límite derecho menor que el antiguo límite izquierdo, modifica
	 * el límite izquierdo manteniendo la anchura del intervalo.
	 * 
	 * @param rightLimit nuevo valor del límite derecho
	 */
	public void setRightLimit(double rightLimit) {
		if (this.rightLimit != rightLimit) {
			if (this.leftLimit > rightLimit)
				this.leftLimit = rightLimit - (this.rightLimit - this.leftLimit);
			this.rightLimit = rightLimit;
			updateData();
		}
	}

	/**
	 * Cambia el límite izquierdo y actualiza los datos de la gráfica. En el caso de
	 * ser el nuevo límite izquierdo mayor que el antiguo límite derecho, modifica
	 * el límite derecho manteniendo la anchura del intervalo.
	 * 
	 * @param leftLimit nuevo valor del límite izquierdo
	 */
	public void setLeftLimit(double leftLimit) {
		if (this.leftLimit != leftLimit) {
			if (leftLimit > this.rightLimit)
				this.rightLimit = leftLimit + (this.rightLimit - this.leftLimit);
			this.leftLimit = leftLimit;
			updateData();
		}
	}

//	/**
//	 * Cambia los límites de la gráfica. En el caso de que el límite izquierdo sea mayor que el derecho,
//	 * se establece ambos límites iguales al límite derecho
//	 * @param leftLimit límite izquierdo
//	 * @param rightLimit límite derecho
//	 * @return true si y solo si ha sido el lñimite izquierdo es menor que el derecho
//	 */
//	public boolean setLimites(double leftLimit, double rightLimit) {
//		boolean exito;
//		if(this.leftLimit==leftLimit && this.rightLimit==rightLimit) {
//			exito = true;
//		} else {
//			this.rightLimit = rightLimit;
//			if(leftLimit>rightLimit) {
//				this.leftLimit = rightLimit;
//				exito = false;
//			} else {
//				this.leftLimit = leftLimit;
//				exito = true;
//			}
//		
//			updateData();
//		}
//		
//		return exito;
//	}

	/**
	 * Cambia los límites de la gráfica
	 * 
	 * @param leftLimit  límite izquierdo
	 * @param rightLimit límite derecho
	 * @throws IllegalArgumentException si el límite izquierdo es superior al
	 *                                  derecho
	 */
	public void setLimites(double leftLimit, double rightLimit) throws IllegalArgumentException {
		if (leftLimit > rightLimit)
			throw new IllegalArgumentException("Límite izquierdo no puede ser superior al derecho");
		else if (this.leftLimit != leftLimit || this.rightLimit != rightLimit) { // si son iguales evitamos tener que
																					// actualizar los datos
			this.leftLimit = leftLimit;
			this.rightLimit = rightLimit;
			updateData();
		}
	}

	/**
	 * Actualiza los datos de todas las funciones asociadas al gráfico
	 */
	public void updateData() {
		for (Object serie : dataCollection.getSeries()) {
			try {
				(((FuncionKey) ((XYSeries) serie).getKey())).getFuncion().updateData();
			} catch (ScriptException e) {
//				e.printStackTrace();
			}
		}
	}

	/**
	 * Elimina una función de la gráfica
	 * 
	 * @param funcion función a eliminar
	 */
	public void remove(Funcion funcion) {
		dataCollection.removeSeries(funcion.getData());
	}

	/**
	 * Elimina todas las funciones de la gráfica
	 */
	public void clear() {
		dataCollection.removeAllSeries();
	}

	/**
	 * Añade una función a la gráfica. Si dicha función ya se encuentra en la
	 * gráfica, no será añadida.
	 * 
	 * @param funcion función a añadir a la gráfica
	 * @return true si y solo si la gráfica no existía en la gráfica
	 */
	public boolean add(Funcion funcion) {
		try {
			dataCollection.addSeries(funcion.getData());
			return true;
		} catch (IllegalArgumentException e) { // devuelto si la serie ya se encuentra en dataCollection
//			e.printStackTrace();
			return false;
		}
	}
}