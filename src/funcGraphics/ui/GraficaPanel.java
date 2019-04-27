package funcGraphics.ui;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;

import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.negocio.Grafica;

class GraficaPanel extends ChartPanel {
	private Grafica grafica;
	private JVentanaGraficar ventana;

	GraficaPanel(Grafica grafica, JVentanaGraficar ventana) {
		super(null);
		this.grafica = grafica;
		this.ventana = ventana;
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
	
	private void changeLimites() {
		Range range = getChart().getXYPlot().getDomainAxis().getRange();
		System.out.println(range.toString());
//		grafica.setLimites(range.getLowerBound(), range.getUpperBound());
//		notifyDataChange();
		ventana.getRightTextField().setValue(range.getUpperBound());
		ventana.getLeftTextField().setValue(range.getLowerBound());
	}
	
	@Override
	public void zoom(Rectangle2D selection) {
		super.zoom(selection);
		changeLimites();
	}
	
//	@Override
//	public void zoomInBoth(double x, double y) {
//		super.zoomInBoth(x, y);
//		changeLimites();
//	}
	
	@Override
	public void zoomInDomain(double x, double y) {
		super.zoomInDomain(x, y);
		changeLimites();
	}
	
	@Override
	public void zoomInRange(double x, double y) {
		super.zoomInRange(x, y);
		changeLimites();
	}
	
//	@Override
//	public void zoomOutBoth(double x, double y) {
//		super.zoomOutBoth(x, y);
//		changeLimites();
//	}
	
	@Override
	public void zoomOutDomain(double x, double y) {
		super.zoomOutDomain(x, y);
		changeLimites();
	}
	
	@Override
	public void zoomOutRange(double x, double y) {
		super.zoomOutRange(x, y);
		changeLimites();
	}
}
