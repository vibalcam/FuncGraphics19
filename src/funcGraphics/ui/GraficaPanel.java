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
	 * Establece la gráfica del panel
	 * @param grafica nueva gráfica del panel
	 */
	void setGrafica(Grafica grafica) {
		this.grafica = grafica;
		setChart(ChartFactory.createXYLineChart("", "x", "f(x)", this.grafica.getDataCollection()));
		configurarPlot();
		notifyDataChange();
	}

	private void configurarPlot() {
		getChart().getXYPlot().setNoDataMessage("NO HAY FUNCIONES AÑADIDAS");
	}

	/**
	 * Notifica de un cambio en los datos de la gráfica
	 */
	void notifyDataChange() {
//		String message = grafica.getLeftLimit()==grafica.getRightLimit() ? "INTERVALO VACÍO"
//				: "NO HAY FUNCIONES AÑADIDAS";

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
