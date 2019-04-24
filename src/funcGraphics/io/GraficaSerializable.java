package funcGraphics.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeriesCollection;

import funcGraphics.dominio.Funcion;
import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.negocio.Grafica;

class GraficaSerializable implements Serializable {
	private static final long serialVersionUID = 1235984003514686804L;

	private double leftLimit;
	private double rightLimit;
	private List<FuncionParametros> parametros;

	GraficaSerializable(Grafica grafica) {
		this.leftLimit = grafica.getLeftLimit();
		this.rightLimit = grafica.getRightLimit();
		this.parametros = new ArrayList<FuncionParametros>();

		XYSeriesCollection dataCollection = grafica.getDataCollection();
		Funcion funcion;
		for (int k = 0; k < dataCollection.getSeriesCount(); k++) {
			funcion = ((FuncionKey) dataCollection.getSeries(k).getKey()).getFuncion();
			parametros.add(new FuncionParametros(funcion.getExpresion(), funcion.getParser().getVariable(),
					funcion.isVisible()));
		}
	}

	double getLeftLimit() {
		return leftLimit;
	}

	double getRightLimit() {
		return rightLimit;
	}

	List<FuncionParametros> getParametros() {
		return parametros;
	}

	Grafica getGrafica() {
		Grafica grafica = new Grafica(leftLimit, rightLimit);
		for (FuncionParametros param : parametros)
			grafica.add(new Funcion(grafica, param.getExpresion(), param.getVariable(), param.isVisible()));

		return grafica;
	}

	class FuncionParametros implements Serializable {
		private static final long serialVersionUID = -4319277069931465463L;

		private String expresion;
		private String variable;
		private boolean visible;

		private FuncionParametros(String expresion, String variable, boolean visible) {
			this.expresion = expresion;
			this.variable = variable;
			this.visible = visible;
		}

		String getExpresion() {
			return expresion;
		}

		String getVariable() {
			return variable;
		}

		boolean isVisible() {
			return visible;
		}
	}
}