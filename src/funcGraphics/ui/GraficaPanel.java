package funcGraphics.ui;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;

import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.negocio.Grafica;

class GraficaPanel extends ChartPanel {
	private Grafica grafica;

	GraficaPanel(Grafica grafica) {
		super(null);
		this.grafica = grafica;
		setChart(ChartFactory.createXYLineChart("", "x", "f(x)", grafica.getDataCollection()));
		configurarPlot();
	}

	/**
	 * Establece la gr�fica del panel
	 * @param grafica nueva gr�fica del panel
	 */
	void setGrafica(Grafica grafica) {
		this.grafica = grafica;
		setChart(ChartFactory.createXYLineChart("", "x", "f(x)", this.grafica.getDataCollection()));
		configurarPlot();
		notifyDataChange();
	}

	private void configurarPlot() {
		getChart().getXYPlot().setNoDataMessage("NO HAY FUNCIONES A�ADIDAS");
	}

	/**
	 * Notifica de un cambio en los datos de la gr�fica
	 */
	void notifyDataChange() {
//		String message = grafica.getLeftLimit()==grafica.getRightLimit() ? "INTERVALO VAC�O"
//				: "NO HAY FUNCIONES A�ADIDAS";

		XYItemRenderer renderer = getChart().getXYPlot().getRenderer();
		List series = grafica.getDataCollection().getSeries();
		XYSeries serie;
		for (int k = 0; k < series.size(); k++) {
			serie = (XYSeries) series.get(k);
			if (serie.isEmpty())
				renderer.setSeriesVisible(k, false);
			else
				renderer.setSeriesVisible(k, ((FuncionKey) serie.getKey()).getFuncion().isVisible());
		}
		this.repaint();
	}
}
