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
	 * Construye una gr�fica con los l�mites predeterminados
	 */
	public Grafica() {
		this(-1 * DEFAULT_LIMIT, DEFAULT_LIMIT);
	}

	/**
	 * Constructor a partir de los l�mites de la gr�fica
	 * 
	 * @param leftLimit  l�mite izquierdo
	 * @param rightLimit l�mite derecho
	 */
	public Grafica(double leftLimit, double rightLimit) {
		dataCollection = new XYSeriesCollection();
		this.setLimites(leftLimit, rightLimit);
//		this.leftLimit = leftLimit;
//		this.rightLimit = rightLimit;
	}

	/**
	 * Devuelve el l�mite izquierdo
	 * 
	 * @return l�mite izquierdo
	 */
	public double getRightLimit() {
		return rightLimit;
	}

	/**
	 * Devuelve el l�mite derecho
	 * 
	 * @return l�mite derecho
	 */
	public double getLeftLimit() {
		return leftLimit;
	}

	/**
	 * Devuelve el estado de guardado de la gr�fica
	 * 
	 * @return true si la gr�fica est� guardada y actualizada, false en caso
	 *         contrario
	 */
	public boolean isGuardado() {
		return guardado;
	}

	/**
	 * Devuelve los XYSeriesCollection que tienen los datos de la gr�fica
	 * 
	 * @return XYSeriesCollection con los datos
	 */
	public XYSeriesCollection getDataCollection() {
		return dataCollection;
	}

	/**
	 * Establece el estado de guardado de la gr�fica
	 * 
	 * @param guardado nuevo estado de guardado
	 */
	public void setGuardado(boolean guardado) {
		this.guardado = guardado;
	}

	/**
	 * Cambia el l�mite derecho y actualiza los datos de la gr�fica. En el caso de
	 * ser el nuevo l�mite derecho menor que el antiguo l�mite izquierdo, modifica
	 * el l�mite izquierdo manteniendo la anchura del intervalo.
	 * 
	 * @param rightLimit nuevo valor del l�mite derecho
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
	 * Cambia el l�mite izquierdo y actualiza los datos de la gr�fica. En el caso de
	 * ser el nuevo l�mite izquierdo mayor que el antiguo l�mite derecho, modifica
	 * el l�mite derecho manteniendo la anchura del intervalo.
	 * 
	 * @param leftLimit nuevo valor del l�mite izquierdo
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
//	 * Cambia los l�mites de la gr�fica. En el caso de que el l�mite izquierdo sea mayor que el derecho,
//	 * se establece ambos l�mites iguales al l�mite derecho
//	 * @param leftLimit l�mite izquierdo
//	 * @param rightLimit l�mite derecho
//	 * @return true si y solo si ha sido el l�imite izquierdo es menor que el derecho
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
	 * Cambia los l�mites de la gr�fica
	 * 
	 * @param leftLimit  l�mite izquierdo
	 * @param rightLimit l�mite derecho
	 * @throws IllegalArgumentException si el l�mite izquierdo es superior al
	 *                                  derecho
	 */
	public void setLimites(double leftLimit, double rightLimit) throws IllegalArgumentException {
		if (leftLimit > rightLimit)
			throw new IllegalArgumentException("L�mite izquierdo no puede ser superior al derecho");
		else if (this.leftLimit != leftLimit || this.rightLimit != rightLimit) { // si son iguales evitamos tener que
																					// actualizar los datos
			this.leftLimit = leftLimit;
			this.rightLimit = rightLimit;
			updateData();
		}
	}

	/**
	 * Actualiza los datos de todas las funciones asociadas al gr�fico
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
	 * Elimina una funci�n de la gr�fica
	 * 
	 * @param funcion funci�n a eliminar
	 */
	public void remove(Funcion funcion) {
		dataCollection.removeSeries(funcion.getData());
	}

	/**
	 * Elimina todas las funciones de la gr�fica
	 */
	public void clear() {
		dataCollection.removeAllSeries();
	}

	/**
	 * A�ade una funci�n a la gr�fica. Si dicha funci�n ya se encuentra en la
	 * gr�fica, no ser� a�adida.
	 * 
	 * @param funcion funci�n a a�adir a la gr�fica
	 * @return true si y solo si la gr�fica no exist�a en la gr�fica
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